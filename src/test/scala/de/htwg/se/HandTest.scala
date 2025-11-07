package de.htwg.se

import de.htwg.se.Card
import de.htwg.se.Hand
import de.htwg.se.Deck

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._

class HandTest extends AnyWordSpec with Matchers {
  "A Hand" when {
    val h = Hand("Hand")
    "initialized" should:
      "as original String" in:
        h.toString() should (be ("Hand"))
      "be as takeFromDeck" in:
        val d: Vector[Card] = fillDeck(Seq.empty[Card])
        val s: String = "Deck"
        val deck: Deck = new Deck(d, s)
        val deck2 = h.takeFromDeck(deck)._2
        h.toString() should (be (s"|${h.handCard}| ") or be (s"${h.handCard}"))
  }
}
