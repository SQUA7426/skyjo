package de.htwg.se.skyjo.Model

import de.htwg.se.util.Mediator
import de.htwg.se.util.ConcreteMediator

def len(x: Any): Int = x.toString().size

class Card(
    private val _mediator: Mediator,
    val value: Int,
    turned: Boolean = false
) extends Mediator {
  def apply(mediator: Mediator, value: Int): Card = new Card(mediator, value)

  override def notify(sender: Mediator, event: String): Unit = {
    _mediator.notify(this, "Created Card")
  }

  override def send(msg: String): Unit = _mediator.send(msg)

  def falseCopy(): Card = new Card(_mediator, value, false)

  def trueCopy(): Card = new Card(_mediator, value, true)
}

def toCard(x: Any): Card = {
  val med = new Mediator {}
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
