package de.htwg.se.skyjo.Model
import de.htwg.se.skyjo.Model.{Card,Board}
import de.htwg.se.util.Mediator
import de.htwg.se.util.ConcreteMediator

class DiscardPile(private val _mediator: Mediator, val discPile: String) {
  override def notify(sender: Mediator, event: String): Unit =
    _mediator.notify(this._mediator, "Created DiscardPile")
  override def send(msg: String): Unit = _mediator.send(msg)

  override def toString(): String = s"${discPile}"

  def putToDiscardPile(from: Any): Unit =
    from match { case d: Deck => _mediator.send("Put To DiscardPile") }
}
