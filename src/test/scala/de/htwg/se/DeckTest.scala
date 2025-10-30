package de.htwg.se

import de.htwg.se.Deck
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import java.io.ByteArrayOutputStream

class DeckTest extends AnyWordSpec with Matchers {
  "A Deck" when {
    val initSeq: Seq[Card] = Seq.empty[Card]
    val dVec: Vector[Card] = fillDeck(initSeq)
    val d: Deck = new Deck(dVec, "Deck")
    "Initialized" should:
      "have the size of 150" in:
        d.deck.size shouldBe (150)
      "Should have UpperCard == Deck" in:
        d.toString() should (be (s"|${d.upperCard}| ") or be (s"${d.upperCard}"))
    "Initalized and UpperCard Taken" should:
      val h = new Hand()
      val d2: Deck = new Deck(h.takeFromDeck(d), "Deck")
      "be printed correctly" in:
        d2.toString()should (be (s"|${d2.upperCard}| ") or be (s"${d2.upperCard}")) 
      "have when upperCard==1 as Int" in:
        d2.upperCardInt() should (be >= -2 and be <= 12)
      "have upperCard.size != 4" in:
        d.toString().size should not be 4

    "it has no Cards left" should:
      val d3: Deck = new Deck(fillDeck(d.deck.toSeq), "Deck")
      "have a different toString" in:
        d3.toString() should (be (s"|${d.upperCard}| ") or be (s"${d.upperCard}"))
      "shoukd throw no Exception" in:
        noException shouldBe thrownBy(d3.turnUpperCard())
  }
}
