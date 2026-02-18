package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.util.*
import scala.util.Try
import scala.xml.Node

trait CardInterface:
  def getValue: Int

  def isVal: Boolean
  def isTurned: Boolean

  def trueCopy: CardInterface
  def falseCopy: CardInterface

  def turn: Unit

  // FILEIO //

  def toXml: Node
  def fromXml(xml: Node): CardInterface
