package de.htwg.se

import scalafx.beans.property.IntegerProperty
import scalafx.beans.property.StringProperty

object Card {
  case class Card(xPos_ : Int, yPos_ : Int, symbol_ : String) {
    val x = new IntegerProperty(this, "xPos_", xPos_)
    val y = new IntegerProperty(this, "yPos_", yPos_)
    val symbol = new StringProperty(this, "symbol_", symbol_)
  }
}
