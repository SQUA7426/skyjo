package de.htwg.se.skyjo.model.modelInterfaceImplementation

import de.htwg.se.skyjo.model.{
  DeckInterface,
  CardInterface,
  DiscardPileInterface
}
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{Card}
import scala.collection.immutable.Vector
import scala.util.Random
import scala.util.Try
import scala.collection.immutable.Seq
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.controller.ControllerComponent.*

import jakarta.inject.Inject
import play.api.libs.json._
import scala.xml.{Node, NodeSeq}

case class Deck(
    val deck: Vector[CardInterface],
    // val ctrl: ControllerInterface,
    val upperCard: String = "Deck"
) extends DeckInterface:

  override def toString(): String = upperCard
  // if upperCard.compareTo("Deck") == 0 then "Deck" else upperCard

  // CTRL //
  override def getDeck: DeckInterface = this

  override def getCard: Try[CardInterface] =
    Try {
      val value: String =
        if upperCard == "Deck" then turnUpperCard else upperCard
      Card(
        Integer.parseInt(value)
      )
    }

  override def getDeckCards: Vector[CardInterface] = deck

  override def turnUpperCard: String =
    upperCard.compareTo("Deck") match {
      case 0 => deck.last.toString()
      case _ => upperCard
    }

  override def remove(amount: Int): Vector[CardInterface] =
    val nDeck = deck.dropRight(amount)
    nDeck

  override def draw(
      ctr: ControllerInterface
  ): (CardInterface, DeckInterface) = {
    if (upperCard != "Deck") {
      val card = ctr.toCard(upperCard)
      (card, new Deck(this.remove(1), "Deck"))
    } else {
      val card = deck.last.trueCopy
      (card, new Deck(this.remove(1), "Deck"))
    }
  }

  // override def leftOf(worth: Int): Int =
  //   deck.count(_ == ctrl.toCard(_mediator, worth))

  // FILEIO //

  def toJson: JsObject = Json.obj(
    "deck" -> deck.map(_.toJson),
    "uppercard" -> upperCard
  )

  def toXml: Node =
    <deck>
        <size>{deck.size}</size>
        <deckcards>
          {deck.map(card => card.toXml)}
        </deckcards>
        <uppercard>{upperCard}</uppercard>
      </deck>

  def fromXml(ctr: ControllerInterface, dn: Node): DeckInterface =
    val size = { dn \\ "size" }.text.toInt
    val upper = { dn \ "uppercard" }
    val deckXml = { dn \ "deckcards" }
    val cardXml = deckXml \\ "card"
    val cards: Vector[CardInterface] = cardXml.map { c =>
      ctr.toCard(
        ctr.toCard(0).fromXml(c.head)
      )
    }.toVector
    Deck(
      cards,
      upper.text.toString
    )

object Deck:
  def apply(ctr: ControllerInterface): DeckInterface =
    new Deck(ctr.fullDeck())
