package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.model.{CardInterface}
import scala.util.Try

trait DeckInterface:
  def getCard: Try[CardInterface]

  def getDeckCards: Vector[CardInterface]

  def getDeck: DeckInterface

  def turnUpperCard: String

  def remove(amount: Int): Vector[CardInterface]

  // def leftOf(worth: Int): Int

  def draw(): (CardInterface, DeckInterface)
