package de.htwg.se.skyjo.model.modelInterfaceImplementation

import de.htwg.se.skyjo.model.{DiscardPileInterface, BoardInterface, CardInterface, DeckInterface}
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{DiscardPile, Deck}

import de.htwg.se.skyjo.controller.ControllerComponent.*
import de.htwg.se.skyjo.util.*

import jakarta.inject.Inject

case class DiscardPile @Inject() (
    val ctrl: ControllerInterface,
    val discPile: String = "Disc",
    var turned: Boolean = false
) extends Colleague with DiscardPileInterface {

  val _mediator = ctrl.getMediator

  def isTurned: Boolean = turned

  override def receive(msg: String): Boolean = {
    msg match
      case "REQUEST PUT TO DISCARDPILE" =>
        println(s"DiscardPile Received Message: ${msg}"); true
      case _ => false
  }

  override def remove(): DiscardPileInterface =
    new DiscardPile(ctrl, "Disc")

  override def getDiscCard(): Option[CardInterface] =
    if discPile == "Disc" || discPile == "" then None else Some(ctrl.toCard(discPile))

  override def send(msg: String): Unit = ctrl.getMediator.send(this, msg)

  override def toString(): String = s"${discPile}"

  override def putToDiscardPile(from: Any): (DiscardPile, DeckInterface) = {
    from match {
      case card: CardInterface =>
        (new DiscardPile(ctrl, card.trueCopy.toString), ctrl.getGameState.deck)
      case d: DeckInterface => {
        (
          new DiscardPile(ctrl, d.toString()),
          new Deck(d.remove(1), ctrl)
        )
      }
      case s: String => {
        (
          new DiscardPile(ctrl, s),
          new Deck(Deck(ctrl).remove(1), ctrl, s)
        )
      }
      case _ => throw new  MatchError(s"Connot process this type: ${from.getClass}")
    }
  }
}
