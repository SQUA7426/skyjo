package de.htwg.se.skyjo.model.modelInterfaceImplementation

import de.htwg.se.skyjo.model.{DeckInterface, CardInterface}
import scala.collection.immutable.Vector
import scala.util.Random
import scala.util.Try
import scala.collection.immutable.Seq
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.controller.ControllerComponent.*

import jakarta.inject.Inject

case class Deck (
    val deck: Vector[CardInterface],
    val ctrl: ControllerInterface,
    val upperCard: String = "Deck"
) extends DeckInterface:

  override def toString(): String =
    if upperCard.compareTo("Deck") == 0 then "Deck" else upperCard

  // MEDIATOR //
  // val _mediator = ctrl.getMediator

  // override def send(msg: String): Unit = _mediator.send(this, msg)

  // override def receive(msg: String): Boolean = {
  //   msg match
  //     case "REQUEST REMOVE UPPERCARD" =>
  //       println(s"Deck Received Message: ${msg}"); true
  //     case "REQUEST CARD FROM DECK" =>
  //       println(s"Deck Received Message: ${msg}"); true
  //     case _ => false
  // }

  // CTRL //
  override def getDeck: DeckInterface = this

  override def getCard: Try[CardInterface] =
    Try {
      val value: String =
        if upperCard == "Deck" then turnUpperCard else upperCard
      Card(
        Integer.parseInt(value),
        ctrl
      )
    }

  override def getDeckCards: Vector[CardInterface] = deck

  override def turnUpperCard: String =
    upperCard.compareTo("Deck") match {
      case 0 => deck.last.toString()
      case _ => "Deck"
    }

  override def draw(): (CardInterface, DeckInterface) = {
    if (upperCard != "Deck") {
      val card = ctrl.toCard(upperCard)
      (card, new Deck(this.remove(1), ctrl, "Deck"))
    } else {
      val card = deck.last.trueCopy
      (card, new Deck(this.remove(1), ctrl, "Deck"))
    }
  }

  override def remove(amount: Int): Vector[CardInterface] =
    val nDeck = deck.dropRight(amount)
    nDeck

  // override def leftOf(worth: Int): Int =
  //   deck.count(_ == ctrl.toCard(_mediator, worth))


object Deck:
  def apply(ctrl: ControllerInterface): DeckInterface =
    new Deck(ctrl.fullDeck(), ctrl)
