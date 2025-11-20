package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.model.Card
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
  val shuffled = Random.shuffle(diffs)
  shuffled

def fullDeck() : (Vector[Card], String) = (fillDeck(Seq.empty[Card]), "Deck")
case class Deck(deck: Vector[Card], upperCard: String) {
  def turnUpperCard(): String =
    upperCard.compareTo("Deck") match {
      case 0 => deck.last.toString()
      case _ => "Deck"
    }
  def remove(amount: Int): Vector[Card] =
    val nDeck = deck.dropRight(amount)
    nDeck
  def leftOf(worth: Int): Int = deck.count(_ == Card(worth))
  def getUpperCard(): Card = if upperCard.compareTo("Deck") != 0 then
    toCard(upperCard.toInt)
    else throw new IllegalArgumentException(s"Invalid upperCard:${upperCard}")
  override def toString(): String = if upperCard.compareTo("Deck")==0 then "Deck" else upperCard
}
