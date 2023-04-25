package org.tycoon.catalog

/**
 * Represents a table's seat.
 *
 * @param num          Seat number between 1 and the maximum number of players per table.
 * @param idPlayer     Identifier of the player sitting on the seat.
 * @param initialStack Number of chips that player owns at the beginning of the hand.
 * @param finalStack   Number of chips that player owns at the end of the hand.
 * @param cards        If are Known, the  player's hole cards.
 */
case class Seat(
                 num: Int,
                 idPlayer: String,
                 initialStack: Float,
                 finalStack: Float,
                 cards: String,
                 win: Boolean,
                 itm: Boolean
               )

object Seat {
  def apply(javaSeat: org.tycoon.parser.catalog.Seat): Option[Seat] = {
    Option(javaSeat).map( seat =>
      new Seat(
        num = seat.getNum,
        idPlayer = seat.getIdPlayer,
        initialStack = seat.getInitialStack,
        finalStack = seat.getFinalStack,
        cards = seat.getCards,
        win = seat.isWin,
        itm = seat.isItm
      )
    )
  }
}
