package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.model.{Card, Board, DiscardPile, Deck,fillBoard}
import de.htwg.se.skyjo.util.{ConcreteMediator}

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import scala.collection.immutable.Seq

class DiscardPileTest extends AnyWordSpec with Matchers {
  "A DiscardPile" when:
    val med = ConcreteMediator()
    val bTemp = Board(med)
    val discard = new DiscardPile(med,"Disc")
    "initialized" should:
      "as original String" in:
        discard.toString() should (be (s"${discard.discPile}"))
      val b:Board = bTemp._1
      val d: Deck = bTemp._2
      "when switched with upperCard of Deck should return ._2 == Deck" in:
        val d2 = discard.putToDiscardPile(d)._2
        d2 shouldBe a[Deck]
}
