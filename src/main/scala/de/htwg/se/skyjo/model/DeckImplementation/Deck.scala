package de.htwg.se.skyjo.model.DeckImplementation

import de.htwg.se.skyjo.model.CardInterface
import scala.collection.immutable.Vector
import scala.util.Random
import scala.collection.immutable.Seq
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.controller.ControllerComponent.*
import de.htwg.se.skyjo.model.DeckInterface

class Deck(
    var   deck: Vector[CardInterface],
    val ctrl: ControllerInterface,
    val upperCard: String = "Deck"
) extends Colleague with DeckInterface {
  val _mediator = ctrl.getMediator
  override def receive(msg: String): Boolean = {
    msg match
      case "REQUEST REMOVE UPPERCARD" =>
        println(s"Deck Received Message: ${msg}"); true
      case "REQUEST CARD FROM DECK" =>
        println(s"Deck Received Message: ${msg}"); true
      case _ => false
  }
  override def send(msg: String): Unit = _mediator.send(this, msg)

  override def getCard: CardInterface = ctrl.toCard(
    this._mediator,
    if upperCard == "Deck" then turnUpperCard else upperCard
  )

  override def turnUpperCard: String = {
    upperCard.compareTo("Deck") match {
      case 0 => deck.last.toString()
      case _ => "Deck"
    }
  }

  

  override def getDeck: Vector[CardInterface] = deck

  override def remove(amount: Int): Vector[CardInterface] =
    val nDeck = deck.dropRight(amount)
    nDeck

  // override def leftOf(worth: Int): Int =
  //   deck.count(_ == ctrl.toCard(_mediator, worth))

  override def draw(): (CardInterface, DeckInterface) = {
    if (upperCard != "Deck") {
      val card = ctrl.toCard(_mediator, upperCard)
      (card, new Deck(this.remove(1), ctrl, "Deck"))
    } else {
      val card = deck.last.trueCopy
      (card, new Deck(this.remove(1), ctrl, "Deck"))
    }
  }
  override def toString(): String =
    if upperCard.compareTo("Deck") == 0 then "Deck" else upperCard
}

object Deck:
  def apply(ctrl: ControllerInterface): DeckInterface =
    new Deck(ctrl.fullDeck(), ctrl)
