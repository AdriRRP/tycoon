package org.tycoon.utils

import org.apache.spark.sql.SparkSession
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.{BeforeAndAfterAll, DoNotDiscover}
import org.tycoon.LogParserTestData
import org.tycoon.constants.LogParserConstants._

@DoNotDiscover
class ZipUtilsPerformanceIT
  extends AnyFlatSpec
    with BeforeAndAfterAll {

  val s3Utils: S3Utils = new S3Utils(
    LogParserTestData.S3AAccessKey,
    LogParserTestData.S3ASecretKey,
    LogParserTestData.S3AEndpoint
  )

  val spark: SparkSession = SparkSession.builder()
    .config(SparkConfigS3aAccessKey, LogParserTestData.S3AAccessKey)
    .config(SparkConfigS3aSecretKey, LogParserTestData.S3ASecretKey)
    .config(SparkConfigS3aEndpoint, LogParserTestData.S3AEndpoint)
    .config(SparkConfigS3aPathStyleAccess, true.toString)
    .master("local[*]")
    .getOrCreate()

  val bigZipPath: String = LogParserTestData.getEnvVar("BIG_ZIP_PATH")

  override def beforeAll(): Unit = {
    // Set hadoop s3 configuration
    spark.conf.getAll.filter(_._1.startsWith(SparkConfigS3aPrefix)).foreach {
      case (key, value) => spark.sparkContext.hadoopConfiguration.set(key, value)
    }
  }

  override def afterAll(): Unit = {
    spark.close()
  }

  "ZipUtils" should "successfully runs with properly configured" in {
    import ZipUtils.SparkSessionZipExtensions

    // Create Bucket
    s3Utils.createBucket(LogParserTestData.LogParserTestBucket)

    val bigZipName = bigZipPath.split("/").last

    // Upload big file to S3
    s3Utils.putObject(
      LogParserTestData.LogParserTestBucket,
      bigZipName,
      bigZipPath
    )

    val searchPath = s"s3a://${LogParserTestData.LogParserTestBucket}/$bigZipName"

    val textRDD = spark.readZippedTextFiles(searchPath)

    val textArray = textRDD.collect().sorted

    textArray.foreach(println)

    // Create Bucket
    s3Utils.deleteBucket(LogParserTestData.LogParserTestBucket)

  }

}