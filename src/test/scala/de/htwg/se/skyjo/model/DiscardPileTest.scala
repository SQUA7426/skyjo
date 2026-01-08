package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
import de.htwg.se.skyjo.model.DeckImplementation.*
import de.htwg.se.skyjo.model.BoardImplementation.*
import de.htwg.se.skyjo.model.DiscardPileImplementation.*
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.GameState

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import scala.collection.immutable.Seq

class DiscardPileTest extends AnyWordSpec with Matchers {
  "A DiscardPile" when:
    val plCount = 1
    val med = new ConcreteMediator()

    val tempState = new GameState(med, Vector.empty, null, null, 0, None)
    val ctr = new Controller(tempState)

    val deck = new Deck(ctr.fullDeck(), ctr)
    val discard = new DiscardPile(ctr)

    val plBoards = Vector.fill(plCount)(new Board(med, 4, 3, Vector.empty))

    ctr.state = new GameState(med, plBoards, deck, discard, 0, None)
    "initialized" should:
      "as original String" in:
        discard.toString() should (be (s"${discard.discPile}"))
      "when switched with upperCard of Deck should return ._2 == Deck" in:
        val d2 = discard.putToDiscardPile(deck)._2
        d2 shouldBe a[DeckInterface]
}
