package de.htwg.se.skyjo.Model

import de.htwg.se.skyjo.Model.Card
import scala.collection.immutable.Vector
import scala.util.Random
import scala.collection.immutable.Seq
import de.htwg.se.util.Mediator
import de.htwg.se.util.ConcreteMediator

class Deck(
    private val _mediator: Mediator,
    val deck: Vector[Card],
    val upperCard: String
) extends Mediator {

  override def notify(sender: Mediator, event: String): Unit =
    _mediator.notify(this, "Created Deck")

  override def send(msg: String): Unit = _mediator.send(msg)

  def turnUpperCard(): String =
    upperCard.compareTo("Deck") match {
      case 0 => deck.last.toString()
      case _ => "Deck"
    }

  def remove(amount: Int): Vector[Card] =
    val nDeck = deck.dropRight(amount)
    nDeck

  def leftOf(worth: Int): Int = deck.count(_ == Card(_mediator, worth))

  def getUpperCard(): Card = if upperCard.compareTo("Deck") != 0 then
    toCard(upperCard.toInt)
  else throw new IllegalArgumentException(s"Invalid upperCard:${upperCard}")
  override def toString(): String =
    if upperCard.compareTo("Deck") == 0 then "Deck" else upperCard
}

def fullDeck(_mediator: Mediator) = {
  val seqCards = Seq.empty[Card]
  val v1: Vector[Card] =
    (for { i <- 1 to 10; j <- -1 to 12 } yield Card(_mediator, j)).toVector
  val v2: Vector[Card] = (for {
    i <- 1 to 5; j <- -2 to 0; if j == -2 || j == 0
  } yield Card(_mediator, j)).toVector
  val fullDeck: Vector[Card] = v1 ++ v2
  val diffs: Vector[Card] = fullDeck.diff(seqCards)
  val shuffled = Random.shuffle(diffs)
  shuffled
}

object Deck:
  def apply(_mediator: Mediator): Deck =
    new Deck(_mediator, fullDeck(_mediator), "Deck")
