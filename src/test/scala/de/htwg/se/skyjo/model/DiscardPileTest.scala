package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.model.{Card, Board, DiscardPile, Deck, fillDeck, fillBoard}

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
      val b:Board = fillBoard(4,3,d)._1
      "when switched with upperCard of Deck should return ._2 == Deck" in:
        val d2 = discard.putToDiscardPile(d)._2
        d2 shouldBe a[Deck]
}
