package org.tycoon.preprocessor

import org.scalatest._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class TextPreProcessorUT extends AnyFlatSpec with Matchers with GivenWhenThen with PrivateMethodTester {

  "TextPreProcessor" should "fix player identifiers" in {
    Given("a Poker hand log")
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

    And("an expected fixed log")
    val expectedFixedLog =
      """
        |PokerStars Hand #157570955733: Tournament #1641817020, $7.50+$7.50+$1.50 USD Hold'em No Limit - Level XLIX (35000/70000) - 2016/08/21 17:00:44 ET
        |Table '1641817020 92' 9-max Seat #1 is the button
        |Seat 1: 13d65aceb53ab1887e7decba787dcad6d8022a39d63e9949b38edfe051a9e036 (804713 in chips)
        |Seat 2: c48d15e04e723f50fcf2ea92ff6c7f92c24fd6c4d100873ed16848822036e042 (3362321 in chips)
        |Seat 3: 3bfb2b9c03eb5b83acac12a8aa336ae14f21e550c250f7f3e7b41a34ee0a8c11 (971969 in chips)
        |Seat 4: cfc14ee30887db5b5b62db68140cbded9a2d955a22c760e1206817b8e7bff265 (1141326 in chips)
        |Seat 5: b3f636ae34ae919fae397bf62c1536bfa2e70be8fd004b700a52bac1f1112fd1 (1998814 in chips)
        |Seat 6: c947d6547a75fd9878da481cb8d3f7da798652d791d27185317b51106296386e (1866412 in chips)
        |Seat 7: acec4626706f61e82cdfd932f13f63041aabe354b57ab81dd3bfd211b51eede2 (3835006 in chips)
        |Seat 9: 9e50175fa3dec449030ee439137e917209219a7f48f9b8243ee22bb8972aaf0e (4519439 in chips)
        |13d65aceb53ab1887e7decba787dcad6d8022a39d63e9949b38edfe051a9e036: posts the ante 11000
        |c48d15e04e723f50fcf2ea92ff6c7f92c24fd6c4d100873ed16848822036e042: posts the ante 11000
        |3bfb2b9c03eb5b83acac12a8aa336ae14f21e550c250f7f3e7b41a34ee0a8c11: posts the ante 11000
        |cfc14ee30887db5b5b62db68140cbded9a2d955a22c760e1206817b8e7bff265: posts the ante 11000
        |b3f636ae34ae919fae397bf62c1536bfa2e70be8fd004b700a52bac1f1112fd1: posts the ante 11000
        |c947d6547a75fd9878da481cb8d3f7da798652d791d27185317b51106296386e: posts the ante 11000
        |acec4626706f61e82cdfd932f13f63041aabe354b57ab81dd3bfd211b51eede2: posts the ante 11000
        |9e50175fa3dec449030ee439137e917209219a7f48f9b8243ee22bb8972aaf0e: posts the ante 11000
        |c48d15e04e723f50fcf2ea92ff6c7f92c24fd6c4d100873ed16848822036e042: posts small blind 35000
        |3bfb2b9c03eb5b83acac12a8aa336ae14f21e550c250f7f3e7b41a34ee0a8c11: posts big blind 70000
        |*** HOLE CARDS ***
        |cfc14ee30887db5b5b62db68140cbded9a2d955a22c760e1206817b8e7bff265: raises 84000 to 154000
        |b3f636ae34ae919fae397bf62c1536bfa2e70be8fd004b700a52bac1f1112fd1: folds
        |c947d6547a75fd9878da481cb8d3f7da798652d791d27185317b51106296386e: folds
        |acec4626706f61e82cdfd932f13f63041aabe354b57ab81dd3bfd211b51eede2: folds
        |9e50175fa3dec449030ee439137e917209219a7f48f9b8243ee22bb8972aaf0e: folds
        |13d65aceb53ab1887e7decba787dcad6d8022a39d63e9949b38edfe051a9e036: folds
        |c48d15e04e723f50fcf2ea92ff6c7f92c24fd6c4d100873ed16848822036e042: folds
        |3bfb2b9c03eb5b83acac12a8aa336ae14f21e550c250f7f3e7b41a34ee0a8c11: folds
        |Uncalled bet (84000) returned to cfc14ee30887db5b5b62db68140cbded9a2d955a22c760e1206817b8e7bff265
        |cfc14ee30887db5b5b62db68140cbded9a2d955a22c760e1206817b8e7bff265 collected 263000 from pot
        |cfc14ee30887db5b5b62db68140cbded9a2d955a22c760e1206817b8e7bff265: doesn't show hand
        |*** SUMMARY ***
        |Total pot 263000 | Rake 0
        |Seat 1: 13d65aceb53ab1887e7decba787dcad6d8022a39d63e9949b38edfe051a9e036 (button) folded before Flop (didn't bet)
        |Seat 2: c48d15e04e723f50fcf2ea92ff6c7f92c24fd6c4d100873ed16848822036e042 (small blind) folded before Flop
        |Seat 3: 3bfb2b9c03eb5b83acac12a8aa336ae14f21e550c250f7f3e7b41a34ee0a8c11 (big blind) folded before Flop
        |Seat 4: cfc14ee30887db5b5b62db68140cbded9a2d955a22c760e1206817b8e7bff265 collected (263000)
        |Seat 5: b3f636ae34ae919fae397bf62c1536bfa2e70be8fd004b700a52bac1f1112fd1 folded before Flop (didn't bet)
        |Seat 6: c947d6547a75fd9878da481cb8d3f7da798652d791d27185317b51106296386e folded before Flop (didn't bet)
        |Seat 7: acec4626706f61e82cdfd932f13f63041aabe354b57ab81dd3bfd211b51eede2 folded before Flop (didn't bet)
        |Seat 9: 9e50175fa3dec449030ee439137e917209219a7f48f9b8243ee22bb8972aaf0e folded before Flop (didn't bet)
        |""".stripMargin

    When("fix Player Identifiers on Poker hand log")
    val fixPlayerIdentifiersValue = PrivateMethod[String]('fixPlayerIdentifiers)
    val fixedLog = TextPreProcessor invokePrivate fixPlayerIdentifiersValue(singlePokerHandLog)

    Then("player identifiers will be replaced")
    fixedLog shouldBe expectedFixedLog
  }

  "TextPreProcessor" should "remove chat lines" in {
    Given("a Poker hand log")
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
        |player4 said, "zzzzzzzzz"
        |player5: folds
        |player6: folds
        |player7: folds
        |player8: folds
        |player1: folds
        |player2: folds
        |player4 said, "jajajajaja"
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

    And("an expected fixed log")
    val expectedFixedLog =
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

    When("remove chat lines on Poker hand log")
    val removeGarbageLinesValue = PrivateMethod[String]('removeGarbageLines)
    val fixedLog = TextPreProcessor invokePrivate removeGarbageLinesValue(singlePokerHandLog)

    println(fixedLog diff expectedFixedLog)

    Then("chat lines will be removed")
    fixedLog shouldBe expectedFixedLog
  }

}