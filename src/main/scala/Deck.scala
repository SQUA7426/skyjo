package de.htwg.se

import de.htwg.se.Card
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

abstract class Deck {
  var deck: ArrayBuffer[Card] = new ArrayBuffer(150)
  var size: Int = 0
  var upperCard: String = "Deck"

  override def toString(): String = if upperCard.size==4 then s"|${upperCard}| " else s"${upperCard}"

  def turnUpperCard(): Unit = {
    if upperCard.compareTo("Deck")==0 then
      var random = Random.between(-2, 12)
      while cardsLeftOf(random) == 0 do random = Random.between(-2, 12)
      upperCard = takeCard(random).toString()
    else this.upperCard = "Deck"
  }


  def isEmptyAt(position: Int): Boolean = {
    return if isCard(deck(position)) then true else false
  }

  def fillDeck(): Unit =
    upperCard = "Deck"
    for j <- 1 to 10 do
      if j <= 5 then 
        deck.addOne(new Card(-2)).addOne(new Card(0))
        size += 2
      for k <- -1 to 12 do
        deck.addOne(new Card(k))
        size += 1
    // deck.sortInPlaceWith((c1, c2) => c1.value < c2.value)

  def cardsLeftOf(v: Int): Int =
    val left = deck.filter(c => c.value == v).size
    left

  def takeCard(worth: Int): Card =
    var idx: Int = 0
    for cards: Card <- deck do
      if cards.value == worth then
        deck.remove(idx)
        size -= 1
        return cards
      idx += 1
    new Card(Integer.MAX_VALUE)
}

class DiscardPile {
  private var discPile: String = "Disc"
  val discard: ArrayBuffer[Card] = ArrayBuffer()
  override def toString(): String = if discPile.length() == 4 then s"|${discPile}| " else s"${discPile}"
}
