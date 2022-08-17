package org.tycoon

import org.apache.spark.sql.SparkSession
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec

class LogParserIT
  extends AnyFlatSpec
    with BeforeAndAfterAll
    with LogParserTestData {

  val spark: SparkSession = SparkSession.builder().master("local[*]").getOrCreate()

  override def beforeAll(): Unit = {
    setupTestData()
  }

  override def afterAll(): Unit = {
    teardownTestData()
    spark.close()
  }

  "LogParser.main" should "successfully runs with properly configured" in {
    LogParser.main(Array.empty)
  }

}
