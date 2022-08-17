package org.tycoon.utils

import org.apache.spark.sql.SparkSession
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.tycoon.LogParserTestData
import org.tycoon.constants.LogParserConstants._

class ZipUtilsIT
  extends AnyFlatSpec
    with BeforeAndAfterAll
    with LogParserTestData {

  val spark: SparkSession = SparkSession.builder()
    .config(SparkConfigS3aAccessKey, LogParserTestData.S3AAccessKey)
    .config(SparkConfigS3aSecretKey, LogParserTestData.S3ASecretKey)
    .config(SparkConfigS3aEndpoint, LogParserTestData.S3AEndpoint)
    .config(SparkConfigS3aPathStyleAccess, true.toString)
    .master("local[*]")
    .getOrCreate()

  override def beforeAll(): Unit = {
    // Set hadoop s3 configuration
    spark.conf.getAll.filter(_._1.startsWith(SparkConfigS3aPrefix)).foreach {
      case (key, value) => spark.sparkContext.hadoopConfiguration.set(key, value)
    }

    setupTestData()
  }

  override def afterAll(): Unit = {
    teardownTestData()
    spark.close()
  }

  "ZipUtils" should "successfully runs with properly configured" in {
    import ZipUtils.SparkSessionZipExtensions


    val searchPath = s"s3a://${LogParserTestData.LogParserTestBucket}/*.*"

    val fileFilter = List(".txt")

    val textRDD = spark.readZippedTextFiles(searchPath, fileFilter)
    val textArray = textRDD.collect().sorted

    val expectedTextArray = Array(
      "file_1.txt",
      "file_2.txt inside zipped folder\n",
      "file_3.txt inside subfolder\n",
      "file_4.txt inside zipped subfolder\n",
    ).sorted

    textArray shouldBe expectedTextArray

  }

}
