package org.tycoon.utils

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class EnvHackerUT
  extends AnyFlatSpec
    with EnvHacker {

  val testEnvironment: Map[String, String] = Map(
    "TEST_ENV_VAR_1" -> "1",
    "TEST_ENV_VAR_2" -> "2",
    "TEST_ENV_VAR_3" -> "3",
  )

  val originalEnvironment: Map[String, String] = sys.env

  "EnvHacker" should "set arbitrary environment variables" in {
    setEnv(testEnvironment)

    testEnvironment.foreach {
      case (envVar, value) =>
        sys.env.get(envVar) shouldBe Some(value)
    }
  }

  it should "override original environment variables" in {
    originalEnvironment.foreach {
      case (envVar, value) =>
        sys.env.get(envVar) shouldBe None
    }
  }

  it should "clear environment variables" in {
    setEnv(Map.empty)

    sys.env shouldBe Map.empty
  }

}
