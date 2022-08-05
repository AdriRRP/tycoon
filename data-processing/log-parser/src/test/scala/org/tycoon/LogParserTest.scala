package org.tycoon

import org.apache.spark.sql.SparkSession
import org.scalatest.flatspec.AnyFlatSpec

class LogParserTest extends AnyFlatSpec {
  val spark: SparkSession = SparkSession.builder().master("local[*]").getOrCreate()

  "An empty Set" should "have size 0" in {
    LogParser.main(Array.empty)
  }

}
