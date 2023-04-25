package org.tycoon.catalog.game

import scala.util.matching.Regex

case class Card(
            suit: Symbol,
            rank: Symbol
          )  extends Ordered[Card]{
  override def compare(that: Card): Int = Card.rankValues(this.rank) - Card.rankValues(that.rank)
}

object Card {

  object suits {
    val SPADES = 'spades
    val HEARTS = 'hearts
    val DIAMONDS = 'diamonds
    val CLUBS = 'clubs
  }

  object rank {
    val ACE = 'ace
    val DEUCE = 'deuce
    val THREE = 'three
    val FOUR = 'four
    val FIVE = 'five
    val SIX = 'six
    val SEVEN = 'seven
    val EIGHT = 'eight
    val NINE = 'nine
    val TEN = 'ten
    val JACK = 'jack
    val QUEEN = 'queen
    val KING = 'king
  }

  val rankValues: Map[Symbol,Int] = Map(
    Card.rank.ACE -> 100,
    Card.rank.DEUCE -> 2,
    Card.rank.THREE -> 3,
    Card.rank.FOUR -> 4,
    Card.rank.FIVE -> 5,
    Card.rank.SIX -> 6,
    Card.rank.SEVEN -> 7,
    Card.rank.EIGHT -> 8,
    Card.rank.NINE -> 9,
    Card.rank.TEN -> 10,
    Card.rank.JACK -> 11,
    Card.rank.QUEEN -> 12,
    Card.rank.KING -> 13
  )

  val CardPattern: Regex = "^([aA23456789tTjJqQkK]|10)([sShHdDcC])$".r

  def apply(
             str: String
           ): Card =
    str match {
      case CardPattern(card, rank) => new Card(
        card.toUpperCase match {
          case "A" => Card.rank.ACE
          case "2" => Card.rank.DEUCE
          case "3" => Card.rank.THREE
          case "4" => Card.rank.FOUR
          case "5" => Card.rank.FIVE
          case "6" => Card.rank.SIX
          case "7" => Card.rank.SEVEN
          case "8" => Card.rank.EIGHT
          case "9" => Card.rank.NINE
          case "10" => Card.rank.TEN
          case "T" => Card.rank.TEN
          case "J" => Card.rank.JACK
          case "Q" => Card.rank.QUEEN
          case "K" => Card.rank.KING
        },
        rank.toLowerCase match {
          case "s" => Card.suits.SPADES
          case "h" => Card.suits.HEARTS
          case "d" => Card.suits.DIAMONDS
          case "c" => Card.suits.CLUBS
        }
      )
      case _ => throw new IllegalArgumentException(s"Invalid card definition: $str")
  }
}


