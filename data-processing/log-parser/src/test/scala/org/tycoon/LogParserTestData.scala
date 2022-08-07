package org.tycoon

import org.tycoon.LogParserTestData._
import org.tycoon.utils.S3Utils

import java.io.File

object LogParserTestData {
  val LogParserTestBucket: String = "testbucket"

  val S3AAccessKey: String = getEnvVar("S3A_ACCESS_KEY")
  val S3ASecretKey: String = getEnvVar("S3A_SECRET_KEY")
  val S3AEndpoint: String = getEnvVar("S3A_ENDPOINT")

  val DataResourcesPath: String = "/data"

  def getEnvVar(name: String): String = {
    sys.env.getOrElse(
      name,
      sys.props.getOrElse(
        name,
        throw new IllegalArgumentException(
          s"Undefined environment variable $name"
        )
      )
    )
  }

  def listDataResources(): Array[String] = {
    new File(getClass.getResource(DataResourcesPath).toURI)
      .list()
      .map(resource => s"$DataResourcesPath/$resource")
  }
}

trait LogParserTestData {

  val s3Utils = new S3Utils(
    S3AAccessKey,
    S3ASecretKey,
    S3AEndpoint
  )

  def setupTestData(): Unit = {
    s3Utils.createBucket(LogParserTestBucket)

    listDataResources().foreach(resource =>
      s3Utils.putObjectFromResources(
        LogParserTestBucket,
        resource.split("/").last,
        resource,
      )
    )
  }

  def teardownTestData(): Unit = {
    s3Utils.deleteBucket(LogParserTestBucket)
  }

}
