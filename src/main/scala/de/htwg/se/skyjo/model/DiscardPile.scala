package de.htwg.se.skyjo.model
import de.htwg.se.skyjo.model.{Card, Board}
import de.htwg.se.skyjo.util.{Mediator, Colleague, ConcreteMediator}

class DiscardPile(
    val _mediator: Mediator,
    val discPile: String
) extends Colleague {
  var isTurned: Boolean = false

  override def receive(msg: String): Boolean = {
    msg match
      case "REQUEST PUT TO DISCARDPILE" =>
        println(s"DiscardPile Received Message: ${msg}"); true
      case _ => false
  }

  override def send(msg: String): Unit = _mediator.send(this, msg)

  override def toString(): String = s"${discPile}"

  def putToDiscardPile(from: Any): (DiscardPile, Deck) = {
    from match {
      case d: Deck => {
        (
          new DiscardPile(_mediator, d.toString()),
          new Deck(_mediator, d.remove(1), "Deck")
        )
      }
      case s:String => {
        (
          new DiscardPile(_mediator, s),
          new Deck(_mediator, Deck(_mediator).remove(1), s)
        )
      }
    }
  }
}
