package de.htwg.se

def len(x: Int): Int = x.toString().size

class Card(v: Int) {
  val value: Int = {
    require(v >= -2 && v <= 12, "Card value should be between >= -2 and <= 12")
    v.toString()
    v
  }

  override def toString(): String = {
    val s = value.toString
    if (s.length == 2) s"| $s | "
    else s"|  $s | "
  }

  def isCard(c: Any): Boolean = c match {
    case _: Card => true
    case _ => false
  }
}