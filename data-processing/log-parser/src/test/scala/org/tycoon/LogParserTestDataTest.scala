package org.tycoon

import org.apache.spark.sql.SparkSession
import org.scalatest.flatspec.AnyFlatSpec

class LogParserTestDataTest extends AnyFlatSpec with LogParserTestData {

  "An empty Set" should "have size 0" in {
    setupTestData()
  }

}
