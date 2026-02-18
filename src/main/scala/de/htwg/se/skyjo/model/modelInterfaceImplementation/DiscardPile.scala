package de.htwg.se.skyjo.model.modelInterfaceImplementation

import de.htwg.se.skyjo.model.{DiscardPileInterface, BoardInterface, CardInterface, DeckInterface}
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{DiscardPile, Deck}

import de.htwg.se.skyjo.controller.ControllerComponent.*
import de.htwg.se.skyjo.util.*

import play.api.libs.json._
import scala.xml.{Node, NodeSeq}

case class DiscardPile (
    // val ctrl: ControllerInterface,
    val discPile: String = "Disc",
    var turned: Boolean = false
) extends DiscardPileInterface:

  var preDisc = "Disc"
  override def toString(): String = s"${discPile}"

  // MEDIATOR //
  // val _mediator = ctrl.getMediator

  // override def send(msg: String): Unit = ctrl.getMediator.send(this, msg)
  // override def receive(msg: String): Boolean = {
  //   msg match
  //     case "REQUEST PUT TO DISCARDPILE" =>
  //       println(s"DiscardPile Received Message: ${msg}"); true
  //     case _ => false
  // }

  // CTRL //
  override def getDiscCard(): Option[CardInterface] =
    if discPile == "Disc" || discPile == "" then None else Some(ctrl.toCard(discPile))

  def isTurned: Boolean = turned

  override def putToDiscardPile(from: Any, ctr: ControllerInterface): (DiscardPile, DeckInterface) = {
    from match {
      case card: CardInterface =>
        val retDisc = new DiscardPile(ctrl, card.trueCopy.toString)
        retDisc.preDisc = this.discPile
        (retDisc, new Deck(ctrl.getDeck.remove(1)))
      case deck: DeckInterface => {
        val retDisc = new DiscardPile(ctrl, deck.toString())
        retDisc.preDisc = this.discPile
        (
          retDisc,
          new Deck(deck.remove(1), ctrl)
        )
      }
      case str: String => {
        val retDisc = new DiscardPile(str)
        retDisc.preDisc = this.discPile
        (
          retDisc,
          new Deck(ctrl.getDeck.remove(1), str)
        )
      }
      case _ => throw new  MatchError(s"Connot process this type: ${from.getClass}")
    }
  }

  override def remove(): DiscardPileInterface =
    new DiscardPile(this.preDisc)

  // FILEIO //

  def toXml: Node =
    <discardpile>
      <discpile>{discPile}<discpile>
      <turned>{turned}<turned>
    </discpile>
    
  def fromXml(xml: Node): DiscardPileInterface
    val discXml = {xml \ "discardpile"}
    val discPXml = {discXml \ "discpile"}
    val discTXml = Node2Bool(discXml \ "turned")
    DiscardPile(discPXml, discTXml)

  private def Node2Bool(ns: NodeSeq): Boolean =
    ns.head.text.replace(" ", "").toBoolean
