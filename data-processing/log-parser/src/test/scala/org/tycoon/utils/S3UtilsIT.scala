package org.tycoon.utils

import org.apache.commons.io.FileUtils
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.tycoon.LogParserTestData

import java.io.File

class S3UtilsIT
  extends AnyFlatSpec {

  val s3Utils: S3Utils = new S3Utils(
    LogParserTestData.S3AAccessKey,
    LogParserTestData.S3ASecretKey,
    LogParserTestData.S3AEndpoint
  )

  "S3Utils" should "throw exception when invalid uri provided" in {
    val caught = intercept[java.lang.IllegalArgumentException](
      new S3Utils("", "", "")
    )
    assert(caught.getCause.toString contains "java.net.URISyntaxException")
  }

  it should "create arbitrary bucket" in {
    val newBucket = s3Utils.createBucket(LogParserTestData.LogParserTestBucket)
    newBucket.map(_.getName) shouldBe Some(LogParserTestData.LogParserTestBucket)
  }

  it should "retrieve arbitrary bucket" in {
    val bucket = s3Utils.getBucket(LogParserTestData.LogParserTestBucket)
    bucket.map(_.getName) shouldBe Some(LogParserTestData.LogParserTestBucket)
  }

  it should "delete arbitrary bucket" in {
    s3Utils.deleteBucket(LogParserTestData.LogParserTestBucket)
    val bucket = s3Utils.getBucket(LogParserTestData.LogParserTestBucket)
    bucket shouldBe None
  }

  it should "upload object to s3 and retrieve as file" in {
    val resource = "file_1.txt"
    val resourcePath = s"/data/$resource"

    s3Utils.createBucket(LogParserTestData.LogParserTestBucket)

    s3Utils.putObjectFromResources(
      LogParserTestData.LogParserTestBucket,
      resource,
      resourcePath
    )

    val retrievedFile = s3Utils.getObjectAsFile(
      LogParserTestData.LogParserTestBucket,
      resource,
      s"/tmp/$resource"
    )

    val expectedFile = new File(getClass.getResource(resourcePath).toURI)

    assert(FileUtils.contentEquals(retrievedFile, expectedFile))

    s3Utils.deleteBucket(LogParserTestData.LogParserTestBucket)
  }

}
