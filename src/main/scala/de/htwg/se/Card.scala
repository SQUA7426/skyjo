package de.htwg.se

def len(x: Int): Int = x.toString().size

case class Card(val value: Int):
  override def toString(): String = s"${value}"
object Card:
  def apply(value: Int): Card = if value>(-3)&&value<(13) then new Card(value) else throw new IllegalArgumentException(s"Invalid Card number: ${value}")

def toCard(x: Any): Card = {
  val val1d =
    (for { j <- -2 to 12 } yield j.toString()).toVector
  x match {
    case a: Int                      => Card(a.toInt)
    case b: String if val1d.contains(b) => Card(Integer.parseInt(b))
    case other =>
      throw new IllegalArgumentException(s"Invalid input:$other")
  }
}

def isCard(c: Any): Boolean = c match {
  case _: Card => true
  case _       => false
}
