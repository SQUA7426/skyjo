package de.htwg.se

import de.htwg.se.Deck

class Hand {
  var handCard: String = "Hand"
  override def toString(): String =
    if handCard.length() == 4 then s"|${handCard}| " else s"${handCard}"

  def takeFromDeck(d: Deck): Vector[Card] = {
    // if d.upperCard.compareTo("Deck") == 0 then
    d.turnUpperCard().toVector
  }
}
