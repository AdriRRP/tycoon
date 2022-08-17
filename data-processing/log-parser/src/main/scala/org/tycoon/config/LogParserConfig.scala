package org.tycoon.config

/**
 * LogParser configuration key class
 *
 * @param path        s3a access key
 * @param inputBucket s3a input bucket
 * @param inputFiles  s3a target file or comma separated files (allow glob patterns)
 * @param fileFilter  filter of file extensions to be processed
 */
final case class LogParserConfig(
                                  path: String,
                                  inputBucket: String,
                                  inputFiles: String,
                                  fileFilter: String
                                )
