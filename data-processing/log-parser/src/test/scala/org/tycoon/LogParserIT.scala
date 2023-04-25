package org.tycoon

import org.apache.spark.sql.SparkSession
import org.scalatest._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class LogParserIT extends AnyFlatSpec with Matchers with GivenWhenThen with PrivateMethodTester {

  "TextPreProcessor" should "fix player identifiers" in {
    val spark: SparkSession =
      SparkSession
        .builder()
        .master("local[*]")
        .appName("LogParser test")
        .getOrCreate()

    //LogParser.main(Array.empty)
  }

}