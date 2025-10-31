package de.htwg.se

import de.htwg.se.Card
import scala.collection.immutable.Vector
import scala.util.Random
import scala.collection.immutable.Seq

def fillDeck(seqCards: Seq[Card]): Vector[Card] =
  val v1: Vector[Card] =
    (for { i <- 1 to 10; j <- -1 to 12 } yield Card(j)).toVector
  val v2: Vector[Card] = (for {
    i <- 1 to 5; j <- -2 to 0; if j == -2 || j == 0
  } yield Card(j)).toVector
  val fullDeck: Vector[Card] = v1 ++ v2
  val diffs: Vector[Card] = fullDeck.diff(seqCards)
  diffs

class Deck(d: Vector[Card], s: String) {
  val deck: Vector[Card] = d
  val upperCard: String = s

  override def toString(): String =
    if upperCard.size == 4 then s"|${upperCard}| " else s"${upperCard}"

  def upperCardInt(): Int = {
    val cardList: List[String] = (for i <- -2 to 12 yield Card(i).toString()).toList
    val c = upperCard.toString()
    val ret =  cardList.indexOf(c)
    ret - 2
  }

  def turnUpperCard(): Seq[Card] = {
    if upperCard.compareTo("Deck")!= 0 || deck.size==0 then Seq.empty[Card]
    else {
      var random = Random.between(-2, 12)
      while cardsLeftOf(random) == 0 do random = Random.between(-2, 12)
      removeCardFromDeck(random, deck)
    }
  }

  def cardsLeftOf(v: Int): Int =
    deck.count(_.value.toInt == v)

  def removeCardFromDeck(worth: Int, deck: Vector[Card]): Seq[Card] =
    val aDiffSeq: Seq[Card] = Seq(Card(worth))
    val seqDiff: Seq[Card] = deck.diff(aDiffSeq)
    seqDiff
}
