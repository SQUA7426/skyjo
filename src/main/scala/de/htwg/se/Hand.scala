package de.htwg.se

import de.htwg.se.Deck

case class Hand(val handCard: String):
  override def toString(): String = s"${handCard}"
  def takeFromDeck(d: Deck): (Hand, Deck) =
    (new Hand(d.upperCard), Deck(d.remove(1), "Deck"))
