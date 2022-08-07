package org.tycoon

import com.amazonaws.services.s3.model.Bucket
import org.tycoon.LogParserTestData.{LogParserTestBucket, S3AAccessKey, S3ASecretKey}

object LogParserTestData {
  val LogParserTestBucket: String = "test"
  val S3AAccessKey: String = getEnvVar("S3A_ACCESS_KEY")
  val S3ASecretKey: String = getEnvVar("S3A_SECRET_KEY")

  def getEnvVar(name: String): String =
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

trait LogParserTestData extends S3TestUtils with EnvHacker {

  def setupTestData(): Unit = {
    setEnv(Map(
      "AWS_ACCESS_KEY" -> S3AAccessKey,
      "AWS_SECRET_KEY" -> S3ASecretKey,
    ))
    createBucket(LogParserTestBucket)
  }

}
