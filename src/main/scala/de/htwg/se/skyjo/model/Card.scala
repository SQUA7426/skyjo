package de.htwg.se.skyjo.Model

import de.htwg.se.skyjo.util.{Mediator, Colleague}

def len(x: Any): Int = x.toString().size

case class Card(
    val _mediator: Mediator,
    val value: Int,
    val turned: Boolean
) extends Colleague {
  override def receive(msg: String): Unit = println(s"Card Received Message: ${msg}")
  override def send(msg: String): Unit = _mediator.send(this,msg)

  def isTurned(): Boolean = turned

  def falseCopy(): Card = new Card(_mediator, value, false)

  def trueCopy(): Card = new Card(_mediator, value, true)

  override def toString(): String = if turned then s"${value}" else "#"
}
object Card:
  def apply(_mediator: Mediator, value: Int): Card = if value>(-3)&&value<(13) then new Card(_mediator,value, true) else throw new IllegalArgumentException(s"Invalid Card number: ${value}") 

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
