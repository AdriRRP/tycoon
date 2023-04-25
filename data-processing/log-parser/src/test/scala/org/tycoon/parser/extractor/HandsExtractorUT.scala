package org.tycoon.parser.extractor

import org.scalatest._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import org.tycoon.parser.catalog.{Action, Hand, Phase, Seat}
import org.tycoon.preprocessor.TextPreProcessor

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.text.SimpleDateFormat
import java.util
import java.util.Locale
import scala.io.Source

class HandsExtractorUT extends AnyFlatSpec with Matchers with GivenWhenThen {

  "Hands Extractor" should "extract single poker Hand" in {
    Given("a single Poker hand log")
    val singlePokerHandLog: String =
      """
        |PokerStars Hand #157570955733: Tournament #1641817020, $7.50+$7.50+$1.50 USD Hold'em No Limit - Level XLIX (35000/70000) - 2016/08/21 17:00:44 ET
        |Table '1641817020 92' 9-max Seat #1 is the button
        |Seat 1: player1 (804713 in chips)
        |Seat 2: player2 (3362321 in chips)
        |Seat 3: player3 (971969 in chips)
        |Seat 4: player4 (1141326 in chips)
        |Seat 5: player5 (1998814 in chips)
        |Seat 6: player6 (1866412 in chips)
        |Seat 7: player7 (3835006 in chips)
        |Seat 9: player8 (4519439 in chips)
        |player1: posts the ante 11000
        |player2: posts the ante 11000
        |player3: posts the ante 11000
        |player4: posts the ante 11000
        |player5: posts the ante 11000
        |player6: posts the ante 11000
        |player7: posts the ante 11000
        |player8: posts the ante 11000
        |player2: posts small blind 35000
        |player3: posts big blind 70000
        |*** HOLE CARDS ***
        |player4: raises 84000 to 154000
        |player5: folds
        |player6: folds
        |player7: folds
        |player8: folds
        |player1: folds
        |player2: folds
        |player3: folds
        |Uncalled bet (84000) returned to player4
        |player4 collected 263000 from pot
        |player4: doesn't show hand
        |*** SUMMARY ***
        |Total pot 263000 | Rake 0
        |Seat 1: player1 (button) folded before Flop (didn't bet)
        |Seat 2: player2 (small blind) folded before Flop
        |Seat 3: player3 (big blind) folded before Flop
        |Seat 4: player4 collected (263000)
        |Seat 5: player5 folded before Flop (didn't bet)
        |Seat 6: player6 folded before Flop (didn't bet)
        |Seat 7: player7 folded before Flop (didn't bet)
        |Seat 9: player8 folded before Flop (didn't bet)
        |""".stripMargin

    And("an expected Hand array instance")
    val expectedSeats = new util.ArrayList[Seat](
      util.Arrays.asList(
        new Seat(1, "player1", 804713.0f, 793713.0f, "", false, false),
        new Seat(2, "player2", 3362321.0f, 3316321.0f, "", false, false),
        new Seat(3, "player3", 971969.0f, 890969.0f, "", false, false),
        new Seat(4, "player4", 1141326.0f, 1323326.0f, "", false, false),
        new Seat(5, "player5", 1998814.0f, 1987814.0f, "", false, false),
        new Seat(6, "player6", 1866412.0f, 1855412.0f, "", false, false),
        new Seat(7, "player7", 3835006.0f, 3824006.0f, "", false, false),
        new Seat(9, "player8", 4519439.0f, 4508439.0f, "", false, false),
      )
    )
    val expectedActions = new util.ArrayList[Action](
      util.Arrays.asList(
        new Action(0, 3, 5, 84000.0f, 154000.0f, false, 347000.0f),
        new Action(1, 4, 1, 0.0f, 0.0f, false, 347000.0f),
        new Action(2, 5, 1, 0.0f, 0.0f, false, 347000.0f),
        new Action(3, 6, 1, 0.0f, 0.0f, false, 347000.0f),
        new Action(4, 7, 1, 0.0f, 0.0f, false, 347000.0f),
        new Action(5, 0, 1, 0.0f, 0.0f, false, 347000.0f),
        new Action(6, 1, 1, 0.0f, 0.0f, false, 347000.0f),
        new Action(7, 2, 1, 0.0f, 0.0f, false, 347000.0f)
      )
    )
    val expectedPhases = new util.ArrayList[Phase](
      util.Arrays.asList(
        new Phase(0, null, 193000.0f, expectedActions)
      )
    )

    val expectedHands = new util.ArrayList[Hand](
      util.Arrays.asList(
        new Hand(
          "157570955733",
          "Hold'em No Limit",
          "USD",
          "1641817020",
          16.5f,
          49,
          "1641817020 92",
          9,
          expectedSeats,
          0,
          1,
          70000.0f,
          35000.0f,
          11000.0f,
          expectedPhases,
          {
            val formatter = new SimpleDateFormat("EE MMM dd HH:mm:ss zzzz yyyy", Locale.US)
            formatter.parse("Sun Aug 21 17:00:44 CEST 2016")
          },
          "ET"
        )
      )
    )

    When("extract hands from log")
    val extractedHands: util.ArrayList[Hand] = HandsExtractor.extract(singlePokerHandLog)

    Then("extracted hand is as expected")
    extractedHands shouldBe expectedHands
  }
  "TEST" should "extract single poker Hand" in {
    Given("a single Poker hand log")
    val file: String = Source.fromFile("/home/adrirrp/repos/tycoon/data-processing/log-parser/PS_114673_2016_8_22_JHTQ-001.txt").getLines().mkString("\n")

    val preprocessedFile = TextPreProcessor.fix(file)

    Files.write(Paths.get("/home/adrirrp/repos/tycoon/data-processing/log-parser/PS_114673_2016_8_22_JHTQ-001-preproc.txt"), preprocessedFile.getBytes(StandardCharsets.UTF_8))

    HandsExtractor.extract(preprocessedFile)
  }
}