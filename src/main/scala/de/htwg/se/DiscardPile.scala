package de.htwg.se
import de.htwg.se.{Hand, Deck, Card,Board}
import scala.collection.immutable.Vector
case class DiscardPile(val discPile: String) {
  override def toString(): String = s"${discPile}"

  def putToDiscardPile(from: Any): (DiscardPile, Any) =
    from match {
      case h: Hand => (DiscardPile(h.handCard), new Hand("Hand"))
      case d: Deck => (DiscardPile(d.upperCard), new Deck(d.remove(1), "Deck"))
    }
}
