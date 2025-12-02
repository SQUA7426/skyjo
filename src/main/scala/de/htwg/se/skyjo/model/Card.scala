package de.htwg.se.skyjo.Model

import de.htwg.se.skyjo.util.{Mediator, Colleague}

def len(x: Any): Int = x.toString().size

class Card(
    val _mediator: Mediator,
    val value: Int,
    turned: Boolean = false
) extends Colleague {
  def apply(mediator: Mediator, value: Int): Card = new Card(mediator, value)

  def isTurned(): Boolean = turned

  def falseCopy(): Card = new Card(_mediator, value, false)

  def trueCopy(): Card = new Card(_mediator, value, true)

}

def toCard(med: Mediator, x: Any): Card = {
  val val1d =
    (for { j <- -2 to 12 } yield j.toString()).toVector
  x match {
    case a: Int                         => Card(med, a.toInt, true)
    case b: String if val1d.contains(b) => Card(med, Integer.parseInt(b), true)
    case other =>
      throw new IllegalArgumentException(s"Invalid input:$other")
  }
}

def isCard(c: Any): Boolean = c match {
  case _: Card => true
  case _       => false
}
