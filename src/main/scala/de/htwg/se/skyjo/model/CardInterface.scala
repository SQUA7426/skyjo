package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.util.*
import scala.util.Try

trait CardInterface:
  def isTurned: Boolean

  def trueCopy: CardInterface

  def falseCopy: CardInterface

  def turn: Unit

  def getVal: Try[Int]
  def getValue: Int
  def isVal: Boolean
