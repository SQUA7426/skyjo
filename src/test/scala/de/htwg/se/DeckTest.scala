package de.htwg.se

import de.htwg.se.Card
import de.htwg.se.Hand
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
      "should have left more then one Card with a Number left" in:
        d.leftOf(1) shouldBe > (0)
      "when get upperCard throw an IllegalArgumentException" in:
        val throwError = the [IllegalArgumentException] thrownBy(d.getUpperCard())
      "when initialized one turned" in:
        d.turnUpperCard() should not be ("Deck")
      val turnedDeck: Deck = new Deck(d.deck,d.turnUpperCard())
      "when initialized one turned be a Card" in:
        turnedDeck.getUpperCard() shouldBe a[Card]
      "when turned again" in:
        turnedDeck.turnUpperCard() should be ("Deck")
  }
}
