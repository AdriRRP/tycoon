package org.tycoon.config

/**
 * LogParser configuration key class
 *
 * @param inputPath  path to input resources
 * @param outputPath  path where output will be written
 */
final case class LogParserConfig(
                                  inputPath: String,
                                  outputPath: String
                                )
