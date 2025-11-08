package de.htwg.se

def len(x: Any): Int = x.toString().size

case class Card(val value: Int, val turned: Boolean):
  override def toString(): String = if turned==true then s"${value}" else "#"
  def falseCopy(): Card = new Card(value, false)
  def trueCopy(): Card = new Card(value, true)
object Card:
  def apply(value: Int): Card = if value>(-3)&&value<(13) then new Card(value, true) else throw new IllegalArgumentException(s"Invalid Card number: ${value}")

def toCard(x: Any): Card = {
  val val1d =
    (for { j <- -2 to 12 } yield j.toString()).toVector
  x match {
    case a: Int                      => Card(a.toInt,true)
    case b: String if val1d.contains(b) => Card(Integer.parseInt(b),true)
    case other =>
      throw new IllegalArgumentException(s"Invalid input:$other")
  }
}

def isCard(c: Any): Boolean = c match {
  case _: Card => true
  case _       => false
}
