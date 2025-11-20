package de.htwg.se.skyjo.model
import de.htwg.se.skyjo.model.{Card}
import scala.collection.immutable.Vector

case class DiscardPile(val discPile: String) {
  override def toString(): String = s"${discPile}"

  def putToDiscardPile(from: Any): (DiscardPile, Any) =
    from match {
      case d: Deck =>
        (new DiscardPile(d.toString()), new Deck(d.remove(1), "Deck"))
    }
}
