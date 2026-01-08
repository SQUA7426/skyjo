package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
import de.htwg.se.skyjo.model.DeckImplementation.*
import de.htwg.se.skyjo.model.BoardImplementation.*
import de.htwg.se.skyjo.model.DiscardPileImplementation.*
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.GameState
import de.htwg.se.skyjo.util.ConcreteMediator
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import java.io.ByteArrayOutputStream

class DeckTest extends AnyWordSpec with Matchers {
  "A Deck" when {
    val plCount = 1
    val med = new ConcreteMediator()

    val tempState = new GameState(med, Vector.empty, null, null, 0, None)
    val ctr = new Controller(tempState)

    val d = new Deck(ctr.fullDeck(), ctr)
    val disc = new DiscardPile(ctr)

    val plBoards = Vector.fill(plCount)(new Board(med, 4, 3, Vector.empty))

    ctr.state = new GameState(med, plBoards, d, disc, 0, None)

    val tui = new Tui(ctr)
    "Initialized" should:

      //------------------------- WHEN INIT ----------------------------------//

      "have the size of 150" in:
        d.deck.size shouldBe (150)
      "toString() should be the UpperCard" in:
        d.toString() should (be ("Deck") or be (s"d.upperCard"))
      // "should have left more then one Card with a Number left" in:
      //   d.leftOf(1) shouldBe > (0)

      //------------------------- WHEN TURNED --------------------------------//

      "when initialized one turned" in:
        d.turnUpperCard should not be ("Deck")

      val d2 = new Deck(d.deck, ctr, d.turnUpperCard)
      "have the Card as upperCard when turned" in:
        d2.toString() shouldBe (d2.upperCard)
      "when initialized one turned be a Card" in:
        d2.getCard shouldBe a[None]
      "when turned again" in:
        d2.turnUpperCard should be ("Deck")

      //------------------------- EXCEPTION --------------------------------//

      // "when get upperCard throw an IllegalArgumentException" in:
      //   val throwError = the [Exception] thrownBy(d.getUpperCard())
  }
}
