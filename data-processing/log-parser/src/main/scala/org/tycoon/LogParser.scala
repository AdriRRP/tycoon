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
import scala.collection.convert.ImplicitConversions.`list asScalaBuffer`
import scala.util.{Failure, Success, Try}

object LogParser {

  // Class logger
  private val logger: Logger = Logger(getClass.getName)

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

    val handsRDD: RDD[Hand] = spark.sparkContext
      .wholeTextFiles(logParserConfig.inputPath, 1000)
      .flatMap {
        case (fileName, fileContent) =>
          logger.error(s"Processing file $fileName")
          Try(HandsExtractor.extract(TextPreProcessor.fix(fileContent))) match {
            case Success(hands) =>
              hands.toList.flatMap(Hand.apply(_))
            case Failure(err) =>
              logger.error(s"Error parsing file $fileName: ${err.toString}")
              List.empty
          }
      }

    import spark.implicits._

    val handsDS: Dataset[Hand] = spark.createDataset(handsRDD)

    val timeId = LocalDateTime.now.format(DateTimeFormatter.ofPattern("YYYYMMdd_HHmmss"))
    val outputJsonFileName = s"${LogParser.getClass.getSimpleName.stripSuffix("$")}$timeId.json"

    handsDS.write.json(s"${logParserConfig.outputPath}/$outputJsonFileName")

  }

}
