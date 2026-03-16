package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{DiscardPile, Deck}
import de.htwg.se.skyjo.util.*

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
    val injector = Guice.createInjector(SkyjoModule(plCount))

    val ctr = injector.getInstance(classOf[ControllerInterface])

    ctr.setup()

    "initialized" should:
      val deck = ctr.getDeck
      val discard = ctr.getDisc
      "as original String" in:
        discard.toString() should (be (s"${discard}"))
      "when switched with upperCard of Deck should return ._2 == Deck" in:
        val d2 = discard.putToDiscardPile(deck, ctr)._2
        d2 shouldBe a[DeckInterface]
      "be able to put an String onto DiscardPile" in:
        val d3 = discard.putToDiscardPile("5",ctr)._2
        d3 shouldBe a[DeckInterface]
      "be unable to put an unrecognized Type onto DiscardPile" in:
        val d4 = the [MatchError] thrownBy(discard.putToDiscardPile(ctr.getGameState, ctr))
}
