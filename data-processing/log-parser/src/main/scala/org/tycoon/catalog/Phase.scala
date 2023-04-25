package org.tycoon.catalog

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

/**
 * Represents a hand's phase.
 *
 * @param id      Current game phase, corresponding companion object's constant.
 * @param cards   Community cards avaiable.
 * @param pot     Pot size at the begining of the fase.
 * @param actions List of Actions done in this phase.
 */
case class Phase(
                  id: Int,
                  cards: String,
                  pot: Float,
                  actions: List[Action]
                )

/**
 * Companion Object of Phase case class
 */
object Phase {
  val HOLECARDS = 0
  val FLOP = 1
  val TURN = 2
  val RIVER = 3
  val SHOWDOWN = 4

  def apply(javaPhase: org.tycoon.parser.catalog.Phase): Option[Phase] = {
    Option(javaPhase).map(phase =>
      new Phase(
        id = phase.getId,
        cards = phase.getCards,
        pot = phase.getPot,
        actions = Option(phase.getActions).map(action =>
          action.toList.flatMap(Action.apply(_))
        ).getOrElse(List.empty)
      )
    )
  }

}