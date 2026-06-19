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

  // CTRL //
  override def getDiscCard(ctrl: ControllerInterface): Option[CardInterface] =
    if discPile == "Disc" || discPile == "" then None else Some(ctrl.toCard(discPile).trueCopy)

  def isTurned: Boolean = turned

  override def putToDiscardPile(from: Any, ctrl: ControllerInterface): (DiscardPile, DeckInterface) = {
    from match {
      case card: CardInterface =>
        val retDisc = new DiscardPile(card.trueCopy.toString, card.isTurned)
        retDisc.preDisc = this.discPile
        (retDisc, new Deck(ctrl.getDeck.remove(1)))
      case deck: DeckInterface => {
        val retDisc = new DiscardPile(deck.toString())
        retDisc.preDisc = this.discPile
        (
          retDisc,
          new Deck(deck.remove(1))
        )
      }
      case str: String => {
        val retDisc = new DiscardPile(str)
        retDisc.preDisc = this.discPile
        (
          retDisc,
          new Deck(ctrl.remove(1), str)
        )
      }
      case _ => throw new  MatchError(s"Connot process this type: ${from.getClass}")
    }
  }

  override def remove(): DiscardPileInterface =
    new DiscardPile(this.preDisc, true)


  override def last: DiscardPileInterface =
    val last_turned = if (this.preDisc == "Disc") then false else true;
    new DiscardPile(this.preDisc, last_turned)

  override def pre = this.preDisc

  // FILEIO //
  def toJson: JsObject = Json.obj(
    "discPile"  -> discPile,
    "turned"    -> turned
    )

  def fromJson(js: JsObject): DiscardPileInterface =
    val disc = (js \ "discPile").as[String]
    val t = (js \ "turned").as[Boolean]
    DiscardPile(disc,t)

  def toXml: Node =
    <discardpile>
      <discpile>{discPile}</discpile>
      <turned>{turned}</turned>
    </discardpile>
    
  def fromXml(srcXml: Node): DiscardPileInterface =
    val discXml = {srcXml \ "discardpile"}
    val discPXml = {discXml \ "discpile"}.text
    val discTXml = Node2Bool(discXml \ "turned")
    DiscardPile(discPXml, discTXml)

  private def Node2Bool(ns: NodeSeq): Boolean =
    ns.head.text.replace(" ", "").toBoolean
