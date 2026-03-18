package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.model.{CardInterface}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import scala.util.Try
import scala.xml.Node
import play.api.libs.json.JsObject

trait DeckInterface:
  def getDeck: DeckInterface
  def getCard: Try[CardInterface]

  def getDeckCards: Vector[CardInterface]

  def turnUpperCard: String

  def draw(ctr: ControllerInterface): (CardInterface, DeckInterface)
  def remove(amount: Int): Vector[CardInterface]
  // def leftOf(worth: Int): Int

  // FILEIO //
  def toJson: JsObject

  def toXml: Node
  def fromXml(ctr: ControllerInterface, dn: Node): DeckInterface
