package org.tycoon

import org.apache.spark.sql.SparkSession
import org.scalatest._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.io.Source

class LogParserIT
  extends AnyFlatSpec
    with Matchers
    with GivenWhenThen
    with PrivateMethodTester
    with BeforeAndAfterAll
    with EnvHacker {

  val inputPath: String = getClass.getResource("/data/sample_input.txt").getPath
  val outputPath: String = s"/tmp/${LocalDateTime.now.format(DateTimeFormatter.ofPattern("YYYYMMdd_HHmmss"))}/"

  override def beforeAll: Unit = {

    new File(outputPath).mkdirs()

    setEnv(Map(
      "LOG_PARSER_INPUT_PATH" -> inputPath,
      "LOG_PARSER_OUTPUT_PATH" -> outputPath,
    ))
  }

  "LogParser" should "convert logs into json" in {

    Given("a file with poker logs")
    And("an expected json file")
    val expectedOutput = Source.fromResource("data/expected_output.json").getLines().mkString("\n")

    When("Set local spark session")
    SparkSession
      .builder()
      .master("local[1]")
      .appName("LogParser test")
      .getOrCreate()

    And("Execute LogParser application")
    LogParser.main(Array.empty)

    Then("Obtained input is as expected")
    val obtainedOutput: String =
      new File(outputPath)
        .listFiles
        .filter(_.isDirectory)
        .flatMap(_.listFiles())
        .find(_.getName.endsWith(".json")) match {
        case Some(file) => Source.fromFile(file).getLines().mkString("\n")
        case None => ""
      }

    obtainedOutput shouldBe expectedOutput
  }

}