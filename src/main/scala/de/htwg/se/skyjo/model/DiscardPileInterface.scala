package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.model.{CardInterface, DiscardPileInterface, DeckInterface}
import scala.xml.Node

trait DiscardPileInterface:

  def getDiscCard(): Option[CardInterface]
  def isTurned: Boolean

  def putToDiscardPile(from: Any): (DiscardPileInterface, DeckInterface)
  def remove(): DiscardPileInterface

  // FILEIO //

  def toXml: Node
  def fromXml(xml: Node): DiscardPileInterface
