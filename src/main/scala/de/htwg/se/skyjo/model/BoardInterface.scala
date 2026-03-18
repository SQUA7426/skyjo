package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.model.{CardInterface, BoardInterface}
import de.htwg.se.skyjo.util.*
import scala.xml.Node
import play.api.libs.json.JsObject
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface

trait BoardInterface:

  def getSize: (Int, Int)
  def getBoard: Vector[Vector[CardInterface]]

  def getBoardCard(pos: Int): CardInterface
  def turnBoardCard(pos: Int): BoardInterface

  def swapFromMem(c: CardInterface, pos: Int): BoardInterface
  def switch(newCard: CardInterface, pos: Int): (CardInterface, BoardInterface)

  def reduce(row: Int, col: Int): (BoardInterface, Boolean, Int, Int)

  // FILEIO //
  def toJson: JsObject

  def toXml: Node
  def fromXml(ctr: ControllerInterface,xml: Node): BoardInterface
