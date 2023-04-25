package org.tycoon

import com.typesafe.scalalogging.Logger
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SparkSession}
import org.tycoon.catalog.Hand
import org.tycoon.config.LogParserConfig
import org.tycoon.constants.LogParserConstants.ConfigLogParserNamespace
import org.tycoon.parser.extractor.HandsExtractor
import org.tycoon.preprocessor.TextPreProcessor
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`
import scala.util.{Failure, Success, Try}

object LogParser {

  // Class logger
  val logger: Logger = Logger(getClass.getName)

  // Application config
  private val logParserConfig: LogParserConfig =
    ConfigSource.default.at(ConfigLogParserNamespace).load[LogParserConfig] match {
      case Left(failures) =>
        failures.toList.foreach(failure => logger.warn(failure.toString))
        throw new IllegalArgumentException(
          s"Undefined '$ConfigLogParserNamespace' configuration"
        )
      case Right(config) => config
    }

  def main(args: Array[String]): Unit = {

    logger.debug(s"Get or create Spark Session")
    val spark = SparkSession.builder().getOrCreate()

    val rdd: RDD[Either[Hand,String]] = spark.sparkContext
      .wholeTextFiles(logParserConfig.inputPath, 5)
      .flatMap {
        case (fileName, fileContent) =>
          Try(HandsExtractor.extract(TextPreProcessor.fix(fileContent))) match {
            case Success(hands) => hands.toList.flatMap(Hand.apply(_)).map(Left(_))
            case Failure(err) =>
              logger.error(s"Error parsing file $fileName: ${err.toString}")
              List(Right(s"Error parsing file $fileName: ${err.toString}"))
          }
      }

    import spark.implicits._

    val handsDS: Dataset[Hand] = spark.createDataset(rdd.filter(_.isLeft).map(_.left.get))
    val errorsDS: Dataset[String] = spark.createDataset(rdd.filter(_.isRight).map(_.right.get))

    val timeId = LocalDateTime.now.format(DateTimeFormatter.ofPattern("YYYYMMdd_HHmmss"))

    handsDS.write.json(s"${logParserConfig.outputPath}/${LogParser.getClass.getSimpleName}$timeId.json")
    errorsDS.write.json(s"${logParserConfig.outputPath}/${LogParser.getClass.getSimpleName}$timeId-errors.txt")

  }

}
