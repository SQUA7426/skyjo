package de.htwg.se.skyjo.model

<<<<<<< HEAD
import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
import de.htwg.se.skyjo.model.DeckImplementation.*
import de.htwg.se.skyjo.model.BoardImplementation.*
import de.htwg.se.skyjo.model.DiscardPileImplementation.*
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.GameState
=======
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{DiscardPile, Deck}
import de.htwg.se.skyjo.util.*
>>>>>>> origin/docker

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import scala.collection.immutable.Seq

import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.skyjo.SkyjoModule
import net.codingwell.scalaguice.InjectorExtensions.*

class DiscardPileTest extends AnyWordSpec with Matchers {
  "A DiscardPile" when:
    val plCount = 1
<<<<<<< HEAD
    val med = new ConcreteMediator()

    val tempState = new GameState(med, Vector.empty, null, null, 0, None)
    val ctr = new Controller(tempState)

    val deck = new Deck(ctr.fullDeck(), ctr)
    val discard = new DiscardPile(ctr)

    val plBoards = Vector.fill(plCount)(new Board(med, 4, 3, Vector.empty))

    ctr.state = new GameState(med, plBoards, deck, discard, 0, None)
=======
    val injector = Guice.createInjector(SkyjoModule(plCount))

    val ctr = injector.getInstance(classOf[ControllerInterface])

    ctr.setup()

>>>>>>> origin/docker
    "initialized" should:
      val deck = ctr.getDeck
      val discard = ctr.getDisc
      "as original String" in:
<<<<<<< HEAD
        discard.toString() should (be (s"${discard.discPile}"))
      "when switched with upperCard of Deck should return ._2 == Deck" in:
        val d2 = discard.putToDiscardPile(deck)._2
        d2 shouldBe a[DeckInterface]
      "be able to put an String onto DiscardPile" in:
        val d3 = discard.putToDiscardPile("5")._2
        d3 shouldBe a[DeckInterface]
      "be unable to put an unrecognized Type onto DiscardPile" in:
        val d4 = the [MatchError] thrownBy(discard.putToDiscardPile(ctr.getGameState))
=======
        discard.toString() should (be (s"${discard}"))
      "when switched with upperCard of Deck should return ._2 == Deck" in:
        val d2 = discard.putToDiscardPile(deck, ctr)._2
        d2 shouldBe a[DeckInterface]
      "be able to put an String onto DiscardPile" in:
        val d3 = discard.putToDiscardPile("5",ctr)._2
        d3 shouldBe a[DeckInterface]
      "be unable to put an unrecognized Type onto DiscardPile" in:
        val d4 = the [MatchError] thrownBy(discard.putToDiscardPile(ctr.getGameState, ctr))
      "convert toJson and fromJson" in:
        val json_disc = discard.toJson
        val dd = discard.fromJson(json_disc)
>>>>>>> origin/docker
}
