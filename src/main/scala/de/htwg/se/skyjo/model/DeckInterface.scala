package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.model.{CardInterface}
import scala.util.Try
import scala.xml.Node

trait DeckInterface:
  def getDeck: DeckInterface
  def getCard: Try[CardInterface]

  def getDeckCards: Vector[CardInterface]

  def turnUpperCard: String

  def draw(): (CardInterface, DeckInterface)
  def remove(amount: Int): Vector[CardInterface]
  // def leftOf(worth: Int): Int

  // FILEIO //

  def toXml: Node
  def fromXml(d: Node): DeckInterface
