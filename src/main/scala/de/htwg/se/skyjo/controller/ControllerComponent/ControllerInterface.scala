package de.htwg.se.skyjo.controller.ControllerComponent

import de.htwg.se.skyjo.model.Card
import scala.collection.immutable.Vector

trait ControllerInterface {
  def setBoard: Vector[Vector[Card]]
  def getBoard: Vector[Vector[Card]]
  def getSize: (Int,Int)
  def reduceSize(row:Int,col:Int): Boolean
}
