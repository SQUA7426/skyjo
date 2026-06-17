package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.model.{CardInterface, DiscardPileInterface, DeckInterface}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import scala.xml.Node
import play.api.libs.json.JsObject

trait DiscardPileInterface:

  def getDiscCard(ctrl: ControllerInterface): Option[CardInterface]
  def isTurned: Boolean

  def last: DiscardPileInterface
  def pre: String

  def putToDiscardPile(from: Any, ctrl: ControllerInterface): (DiscardPileInterface, DeckInterface)
  def remove(): DiscardPileInterface

  // FILEIO //
  def toJson: JsObject
  def fromJson(js: JsObject): DiscardPileInterface

  def toXml: Node
  def fromXml(xml: Node): DiscardPileInterface
