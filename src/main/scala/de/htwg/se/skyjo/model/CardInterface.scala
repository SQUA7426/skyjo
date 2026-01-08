package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.util.*

trait CardInterface:
  def isTurned: Boolean

  def trueCopy: CardInterface

  def falseCopy: CardInterface

  def turn: Unit
