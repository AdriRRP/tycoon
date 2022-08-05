package org.tycoon

import com.typesafe.scalalogging.Logger
import org.apache.spark.sql.SparkSession
import org.tycoon.config.{LogParserConfig, S3aConfig}
import org.tycoon.constants.LogParserConstants._
import pureconfig._
import pureconfig.generic.auto._

object LogParser {

  val logger: Logger = Logger(getClass.getName)

  val s3aConfig: Option[S3aConfig] =
    ConfigSource.default.at(ConfigS3aNamespace).load[S3aConfig] match {
      case Left(failures) =>
        failures.toList.foreach(failure => logger.warn(failure.toString))
        None
      case Right(config) => Some(config)
    }

  val logParserConfig: LogParserConfig =
    ConfigSource.default.at(ConfigLogParserNamespace).load[LogParserConfig] match {
      case Left(failures) =>
        failures.toList.foreach(failure => logger.warn(failure.toString))
        throw new IllegalArgumentException(
          s"Undefined '$ConfigLogParserNamespace' configuration"
        )
      case Right(config) => config
    }


  def addS3aConfig(builder: SparkSession.Builder): SparkSession.Builder = {
    s3aConfig match {
      case None => builder
      case Some(config) =>
        builder
          .config(SparkConfigS3aAccessKey, config.accessKey)
          .config(SparkConfigS3aSecretKey, config.secretKey)
          .config(SparkConfigS3aEndpoint, config.endpoint)
    }

  }


  def main(args: Array[String]): Unit = {


    val sparkBuilder = SparkSession.builder().appName(getClass.getName)
    val spark = addS3aConfig(sparkBuilder).getOrCreate()
    //val df = spark.read.csv(logParserConfig.path)
    //df.show(1000, false)
    spark.sparkContext.binaryFiles(logParserConfig.path).map(binFile => binFile._1).foreach(println)
  }

}
