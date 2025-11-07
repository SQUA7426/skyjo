package de.htwg.se
case class DiscardPile(disc: String) {
  var discPile: String = disc
  override def toString(): String = s"${discPile}"
}
