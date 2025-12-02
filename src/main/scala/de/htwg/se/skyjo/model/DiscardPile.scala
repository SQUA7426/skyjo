package de.htwg.se.skyjo.Model
import de.htwg.se.skyjo.Model.{Card, Board}
import de.htwg.se.skyjo.util.{Mediator,Colleague}

class DiscardPile(val _mediator: Mediator, val discPile: String)
    extends Colleague:
  override def send(msg: String): Unit = _mediator.send(this, msg)

  override def toString(): String = s"${discPile}"

  def putToDiscardPile(from: Any): Unit = {
    from match {
      case d: Deck => {
        _mediator.send(this, "Put To DiscardPile")
        (
          new DiscardPile(_mediator, d.toString()),
          new Deck(_mediator, d.remove(1), "Deck")
        )
      }
    }
  }
