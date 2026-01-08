package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.model.DeckInterface
import de.htwg.se.skyjo.model.CardInterface

trait DiscardPileInterface:
  def remove(): DiscardPileInterface

  def getDiscCard(): Option[CardInterface]

  def putToDiscardPile(from: Any): (DiscardPileInterface, DeckInterface)
