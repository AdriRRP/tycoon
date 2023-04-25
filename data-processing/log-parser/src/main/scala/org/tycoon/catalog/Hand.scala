package org.tycoon.catalog

import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.write

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

/**
 * Represents a poker hand. A poker hand is a complete round of play.
 *
 * @param id              Hand Identifier.
 * @param gameType        Type of game to which the hand refers, e.g. No Limit Hold'em, Pot Limit Omaha...
 * @param currency        Currency used in the game, e.g. EUR, USD, SC ...
 * @param tournamentId    (Only Tournaments) Tournament identifier.
 * @param entryPrice      (Only Tournaments) Cost to join the tournament. The currency is the one indicated above.
 * @param level           (Only Tournaments) Temporary unit indicating the state of the blinds.
 * @param tableId         Identifier of the table where the hand happens.
 * @param tableMaxPlayers Maximum number of players who can occupy the table.
 * @param seats           List of Seat that make up the table.
 * @param dealerIndex
 * @param dealerSeatNum   Player's seat number in dealer position.
 * @param bigBlind        Current chip value of the big blind.
 * @param smallBlind      Current chip value of the small blind.
 * @param ante            Current chip value of the ante blind.
 * @param phases          List of Phase that make up the hand.
 * @param date            Date on which the hand was played.
 * @param timeZone        Time Zone Abbreviation.
 */
case class Hand(
                 id: String,
                 gameType: String,
                 currency: String,
                 tournamentId: String,
                 entryPrice: Float,
                 level: Int,
                 tableId: String,
                 tableMaxPlayers: Int,
                 seats: List[Seat],
                 dealerIndex: Int,
                 dealerSeatNum: Int,
                 bigBlind: Float,
                 smallBlind: Float,
                 ante: Float,
                 phases: List[Phase],
                 date: String,
                 timeZone: String
               ) {
  override def toString: String = {
    implicit val formats: DefaultFormats.type = DefaultFormats
    write(this)
  }
}

object Hand {
  def apply(javaHand: org.tycoon.parser.catalog.Hand): Option[Hand] = {
    Option(javaHand).map(hand =>
      new Hand(
        id = hand.getId,
        gameType = hand.getGameType,
        currency = hand.getCurrency,
        tournamentId = hand.getTournamentId,
        entryPrice = hand.getEntryPrice,
        level = hand.getLevel,
        tableId = hand.getTableId,
        tableMaxPlayers = hand.getTableMaxPlayers,
        seats = Option(hand.getSeats).map(
          _.toList.flatMap(Seat.apply(_))
        ).getOrElse(List.empty),
        dealerIndex = hand.getDealerIndex,
        dealerSeatNum = hand.getDealerSeatNum,
        bigBlind = hand.getBigBlind,
        smallBlind = hand.getSmallBlind,
        ante = hand.getAnte,
        phases = Option(hand.getPhases).map(
          _.toList.flatMap(Phase.apply(_))
        ).getOrElse(List.empty),
        date = Option(hand.getDate).map(_.toString).getOrElse(""), // TODO: Revisar
        timeZone = hand.getTimeZone
      )
    )
  }
}