package de.htwg.se.skyjo.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
import de.htwg.se.skyjo.model.CardImplementation.*
import de.htwg.se.skyjo.model.DeckImplementation.*
import de.htwg.se.skyjo.model.BoardImplementation.*
import de.htwg.se.skyjo.model.DiscardPileImplementation.*
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.GameState

import de.htwg.se.skyjo.util.ConcreteMediator

class CardTest extends AnyWordSpec with Matchers {
  "A Card" when {
    val plCount = 1
    val med = new ConcreteMediator()

    val tempState = new GameState(med, Vector.empty, null, null, 0, None)
    val ctr = new Controller(tempState)

    val deck = new Deck(ctr.fullDeck(), ctr)
    val disc = new DiscardPile(ctr)

    val plBoards = Vector.fill(plCount)(new Board(med, 4, 3, Vector.empty))

    ctr.state = new GameState(med, plBoards, deck, disc, 0, None)

    val tui = new Tui(ctr)

    //------------------------- INACCEPTABLE CARDS --------------------------//

    "has the value -3" should:
      "not be acceptable as Card" in:
        val lowerCardErr = the [IllegalArgumentException] thrownBy(ctr.toCard(-3, ctr))
    "has the value 20" should:
      "not be acceptable as Card" in:
        val highCardErr = the [IllegalArgumentException] thrownBy(ctr.toCard(20, ctr))

    //--------------------------- ACCEPTABLE CARDS ----------------------------//

    val betweenCard = ctr.toCard(5)
    "has the value 11" should:
      val num: Int = 11
      val card11 = ctr.toCard(num)
      "as string" in:
        card11.toString() shouldBe (f"${num}")
    "A Card with value 9" should:
      val num9: Int = 9
      val card9 = Card(num9,ctr)

      //--------------------------- CONVERTING ----------------------------//

      "as string 9 be converted correctly" in:
        val n9 = "9"
        ctr.toCard(n9) shouldBe card9
      "as int 9 be converted correctly" in:
        ctr.toCard(num9) shouldBe card9
      "not be acceptable from boolean" in:
        val highCardErr = the [IllegalArgumentException] thrownBy(Card(99,ctr))

      //--------------------------- CARDCOPY ----------------------------------//

      "return # if a False Copy of it is created" in:
        card9.falseCopy.toString() should be ("#")
      "return the number if a True Copy of it is created" in:
        card9.trueCopy shouldBe ctr.toCard(9)

      //------------------------- OPERATORS ---------------------------//

      "is from type: Card" in:
        ctr.isCard(card9) shouldBe true
  }
}
