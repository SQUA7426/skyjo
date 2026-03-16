package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{DiscardPile, Deck, Board, Card}
import de.htwg.se.skyjo.util.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import java.io.ByteArrayOutputStream

import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.skyjo.SkyjoModule
import net.codingwell.scalaguice.InjectorExtensions.*

class DeckTest extends AnyWordSpec with Matchers {
  "A Deck" when {
    val plCount = 1
    val injector = Guice.createInjector(SkyjoModule(plCount))

    val ctr = injector.getInstance(classOf[ControllerInterface])
    val d = Deck(ctr)

    ctr.setup()

    "Initialized" should:

      //------------------------- WHEN INIT ----------------------------------//
      "be able to be init by alt. way" in:
        Deck(ctr) shouldBe a[DeckInterface]

      // "have the size of 150" in:
      //   d.getDeckCards.size shouldBe (150)
      "toString() should be the UpperCard" in:
        d.toString() should (be ("Deck") or be (s"d.upperCard"))
      // "should have left more then one Card with a Number left" in:
      //   d.leftOf(1) shouldBe > (0)

      //------------------------- WHEN TURNED --------------------------------//

      "when initialized one turned" in:
        d.turnUpperCard should not be ("Deck")

      val d2 = new Deck(d.getDeckCards, d.turnUpperCard)
      "have the Card as upperCard when turned" in:
        d2.toString() shouldBe (d2.upperCard)
      "when turned again" in:
        d2.turnUpperCard should be ("Deck")

      //------------------------- EXCEPTION --------------------------------//

      // "when get upperCard throw an IllegalArgumentException" in:
      //   val throwError = the [Exception] thrownBy(d.getUpperCard())
  }
}
