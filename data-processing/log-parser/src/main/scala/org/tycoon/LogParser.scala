package org.tycoon

import com.typesafe.scalalogging.Logger
import org.apache.spark.sql.SparkSession
import org.tycoon.config.{LogParserConfig, S3aConfig}
import org.tycoon.constants.LogParserConstants._
import org.tycoon.utils.ZipUtils
import pureconfig._
import pureconfig.generic.auto._

object LogParser {

  // Class logger
  val logger: Logger = Logger(getClass.getName)

  // S3a related config
  val s3aConfig: Option[S3aConfig] =
    ConfigSource.default.at(ConfigS3aNamespace).load[S3aConfig] match {
      case Left(failures) =>
        failures.toList.foreach(failure => logger.warn(failure.toString))
        None
      case Right(config) => Some(config)
    }

  // Application config
  val logParserConfig: LogParserConfig =
    ConfigSource.default.at(ConfigLogParserNamespace).load[LogParserConfig] match {
      case Left(failures) =>
        failures.toList.foreach(failure => logger.warn(failure.toString))
        throw new IllegalArgumentException(
          s"Undefined '$ConfigLogParserNamespace' configuration"
        )
      case Right(config) => config
    }

  /**
   * Add parsed s3a configuration if success parsed to spark builder.
   * If s3a configuration not found, return the same spark session
   *
   * @param builder target spark builder
   * @return spark builder with s3a configurations if exists
   */
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

  /**
   * Given a SparkSession, set all fs.s3a properties in underlying hadoop configuration
   *
   * @param spark target spark session
   */
  def refreshHadoopConfig(spark: SparkSession): Unit =
    spark.conf.getAll.filter(_._1.startsWith(SparkConfigS3aPrefix)).foreach {
      case (key, value) => spark.sparkContext.hadoopConfiguration.set(key, value)
    }

  def main(args: Array[String]): Unit = {

    val sparkBuilder = SparkSession.builder()
      .appName(getClass.getName)
      .config(SparkConfigS3aPathStyleAccess, true.toString)

    val spark = addS3aConfig(sparkBuilder).getOrCreate()

    // Add s3 configurations to Hadoop config
    refreshHadoopConfig(spark)

    // Add bucket prefix to each input file
    val searchPath = logParserConfig.inputFiles.split(",")
      .map(file => s"s3a://${logParserConfig.inputBucket}/$file")
      .mkString(",")

    // Add file extension filter
    val fileFilter = logParserConfig.fileFilter.split(",").toList

    val inputRDD = spark.sparkContext
      .binaryFiles(searchPath)
      .flatMap(target => ZipUtils.unzip(target, fileFilter))

    inputRDD.foreach(println)
  }

}
