package de.htwg.se
class DiscardPile {
  var discPile: String = "Disc"
  override def toString(): String = (if discPile.length() == 4 then s"|${discPile}| " else s"${discPile}")
}
