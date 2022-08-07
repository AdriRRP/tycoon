package org.tycoon.constants

/**
 * Contains LogParser constants
 */
object LogParserConstants {

  // S3 related constants
  val SparkConfigS3aPrefix: String = "fs.s3a"
  val SparkConfigS3aAccessKey: String = "fs.s3a.access.key"
  val SparkConfigS3aSecretKey: String = "fs.s3a.secret.key"
  val SparkConfigS3aEndpoint: String = "fs.s3a.endpoint"
  val SparkConfigS3aPathStyleAccess: String = "fs.s3a.path.style.access"

  // Configuration constants
  val ConfigS3aNamespace: String = "s3a"
  val ConfigLogParserNamespace: String = "log-parser"
}
