import org.tycoon.preprocessor.TextPreProcessor

val fileContent =
  """
     |PokerStars Hand #157570955733: Tournament #1641817020, $7.50+$7.50+$1.50 USD Hold'em No Limit - Level XLIX (35000/70000) - 2016/08/21 17:00:44 ET
     |Table '1641817020 92' 9-max Seat #1 is the button
     |Seat 1: madtimm (804713 in chips)
     |Seat 2: zugmoraes (3362321 in chips)
     |Seat 3: Hulkyyyy (971969 in chips)
     |Seat 4: hawkx1984 (1141326 in chips)
     |Seat 5: Wealthiest1 (1998814 in chips)
     |Seat 6: xeD_ox_es (1866412 in chips)
     |Seat 7: reijomo (3835006 in chips)
     |Seat 9: jareth3542 (4519439 in chips)
     |madtimm: posts the ante 11000
     |zugmoraes: posts the ante 11000
     |Hulkyyyy: posts the ante 11000
     |hawkx1984: posts the ante 11000
     |Wealthiest1: posts the ante 11000
     |xeD_ox_es: posts the ante 11000
     |reijomo: posts the ante 11000
     |jareth3542: posts the ante 11000
     |zugmoraes: posts small blind 35000
     |Hulkyyyy: posts big blind 70000
     |*** HOLE CARDS ***
     |hawkx1984: raises 84000 to 154000
     |Wealthiest1: folds
     |xeD_ox_es: folds
     |reijomo: folds
     |jareth3542: folds
     |madtimm: folds
     |zugmoraes: folds
     |Hulkyyyy: folds
     |Uncalled bet (84000) returned to hawkx1984
     |hawkx1984 collected 263000 from pot
     |hawkx1984: doesn't show hand
     |*** SUMMARY ***
     |Total pot 263000 | Rake 0
     |Seat 1: madtimm (button) folded before Flop (didn't bet)
     |Seat 2: zugmoraes (small blind) folded before Flop
     |Seat 3: Hulkyyyy (big blind) folded before Flop
     |Seat 4: hawkx1984 collected (263000)
     |Seat 5: Wealthiest1 folded before Flop (didn't bet)
     |Seat 6: xeD_ox_es folded before Flop (didn't bet)
     |Seat 7: reijomo folded before Flop (didn't bet)
     |Seat 9: jareth3542 folded before Flop (didn't bet)
     |""".stripMargin

val chatPattern = "^.*said,\\s+\".*?\"$".r
fileContent.split("\n").filterNot(chatPattern.findFirstIn(_).nonEmpty).mkString("", "\n", "\n")
