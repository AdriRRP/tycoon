package org.tycoon.catalog

/**
 * Represents a player's action.
 *
 * @param seqNum     Action's squence number in the phase.
 * @param seatIdx    Index of Seat who do the action.
 * @param actionType Which action is done, corresponding companion object's constant.
 * @param amount     If the action requires put chips, the total amount of those chips.
 * @param betSize    If the action is bet or raise, the amount this action adds to pot.
 * @param allin      Indicates if player goes allin.
 * @param pot        The pot size before the action.
 */
case class Action(
                   seqNum: Int,
                   seatIdx: Int,
                   actionType: Int,
                   amount: Float,
                   betSize: Float,
                   allin: Boolean,
                   pot: Float
                 )

/**
 * Companion Object of Action case class
 */
object Action {
  val BIG_BLIND: Int = -2
  val SMALL_BLIND: Int = -1
  val ANTE = 0
  val FOLD = 1
  val CHECK = 2
  val CALL = 3
  val BET = 4
  val RAISE = 5

  def apply(javaAction: org.tycoon.parser.catalog.Action): Option[Action] = {
    Option(javaAction).map(action =>
      new Action(
        seqNum = action.getSeqNum,
        seatIdx = action.getSeatIdx,
        actionType = action.getType,
        amount = action.getAmount,
        betSize = action.getBetSize,
        allin = action.isAllin,
        pot = action.getPot
      )
    )
  }


}