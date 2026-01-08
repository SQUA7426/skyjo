package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.model.CardInterface

trait DeckInterface:
  def getCard: CardInterface

  def getDeck: Vector[CardInterface]

  def turnUpperCard: String

  def remove(amount: Int): Vector[CardInterface]

  // def leftOf(worth: Int): Int

  def draw(): (CardInterface, DeckInterface)
