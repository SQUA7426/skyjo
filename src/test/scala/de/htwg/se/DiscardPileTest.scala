package de.htwg.se

import de.htwg.se.{Hand,DiscardPile,Deck,Board}

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import scala.collection.immutable.Seq

class DiscardPileTest extends AnyWordSpec with Matchers {
  "A DiscardPile" when:
    val discard = DiscardPile("Disc")
    "initialized" should:
      "as original String" in:
        discard.toString() should (be (s"${discard.discPile}"))
      val d: Deck = new Deck(fillDeck(Seq.empty[Card]),"Deck")
      val h: Hand = new Hand("Hand")
      val b:Board = new Board(4,3,fillBoard(4,3,d)._1)
      "when switched with upperCard of Deck should return ._2 == Deck" in:
        val d2 = discard.putToDiscardPile(d)._2
        d2 shouldBe a[Deck]
      "when switched with HandCard should return ._2 == Hand" in:
        val h2: Hand = h.takeFromDeck(d)._1
        val h3 = discard.putToDiscardPile(h2)._2
        h3 shouldBe a[Hand]
      "when switched with HandCard should have another String" in:
        val h2: Hand = h.takeFromDeck(d)._1
        val disc2 = discard.putToDiscardPile(h2)._1
        disc2.toString() should not (be ("disc"))

}
