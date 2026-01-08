package de.htwg.se.skyjo.controller.ControllerComponent

import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
<<<<<<< HEAD
import de.htwg.se.skyjo.model.CardImplementation.*
=======
>>>>>>> components
import de.htwg.se.skyjo.model.DeckImplementation.*
import de.htwg.se.skyjo.model.BoardImplementation.*
import de.htwg.se.skyjo.model.DiscardPileImplementation.*
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.{GameState, DeckInterface, CardInterface, BoardInterface, DiscardPileInterface}

import scala.io.StdIn.{readInt, readLine}
import scala.util.Random
import java.io.ByteArrayInputStream
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ControllerTest extends AnyWordSpec with Matchers {
  "A Controller" when:
    val plCount = 1
    val med = new ConcreteMediator()

    val tempState = new GameState(med, Vector.empty, null, null, 0, None)
    val ctr = new Controller(tempState)

    val deck = new Deck(ctr.fullDeck(), ctr)
    val disc = new DiscardPile(ctr)

    val plBoards = Vector.fill(plCount)(new Board(med, 4, 3, Vector.empty))

    ctr.state = new GameState(med, plBoards, deck, disc, 0, None)

<<<<<<< HEAD
    // setup()
    ctr.setup()

    val tui = new Tui(ctr)

    val state:GameState = ctr.getGameState
    val card8 = Card(9,ctr)
    val anotherState = state.copy(
      drawnCard = Some(card8)
      )
    "it is working, it" should {
=======
    val tui = new Tui(ctr)
    "it is working, it" should {
      "do a setup() and update Tui" in:
        ctr.setup()
>>>>>>> components
      "get Mediator, GameState, Deck and Discard-Card" in:
        ctr.getMediator shouldBe a[Mediator]
        ctr.getGameState shouldBe a[GameState]
        ctr.getDeck shouldBe a[Vector[CardInterface]]
        ctr.getDiscCard() shouldBe a[None.type]
      "be able to fill a Board" in:
        val (afterBoard, afterDeck) = ctr.fillBoard(4, 3, ctr.state.deck)
        afterBoard shouldBe a[BoardInterface]
        afterDeck shouldBe a[DeckInterface]
<<<<<<< HEAD
      val oldState = ctr.state
      "execute save, undo and redo" in:
        ctr.save(oldState)
        ctr.undo()
        ctr.redo()
      "execute a move and update GAMESTATE" in:
=======
      "execute undo and redo" in:
        val oldState = ctr.state
        ctr.undo()
        ctr.redo()
      "execute a move and update GAMESTATE" in:
        val oldState = ctr.state
>>>>>>> components
        ctr.executeMove(oldState)
        ctr.uptGameState(oldState)
      "draw from Deck and DiscardPile" in:
        ctr.drawFromDeck()
        ctr.drawFromDisc()
        ctr.draw()
      "replaceCard on Board" in:
        ctr.replaceCard(0)
      "execute a swap between Deck and DiscardPile" in:
        ctr.discardDrawnCard()
      "execute a SwapHandler()" in:
        ctr.SwapHandler(0)
      "remove a Card From Disc" in:
        ctr.remove()
      "remove a Card From Deck" in:
        ctr.remove(1)
      "be able to turn Deck UpperCard" in:
        ctr.turnUpperCard shouldBe a[String]

      "execute a fullDeck()" in:
        val fullDeck = ctr.fullDeck()
        fullDeck.length shouldBe 150
<<<<<<< HEAD

      ctr.state = anotherState
      "be able to putCardOnBoard" in
        ctr.putCardOnBoard(0)
      "be able to turnBoardCard" in:
        ctr.turnBoardCard(0)
        ctr.turnBoardCard(1)
        ctr.turnBoardCard(2)
        ctr.turnBoardCard(3)
        ctr.turnBoardCard(4)
        ctr.turnBoardCard(5)
        ctr.turnBoardCard(6)
        ctr.turnBoardCard(7)
        ctr.turnBoardCard(8)
        ctr.turnBoardCard(9)
        ctr.turnBoardCard(10)
        ctr.turnBoardCard(11)
    }

    "A GAMESTATE" should:
      "have a drawnCard" in:
        state.drawnCard shouldBe None
      "be parsed state toString()" in:
        state.toString() shouldBe a[String]
      "be parsed anotherState toString()" in:
        anotherState.toString() shouldBe a[String]
=======
    }
>>>>>>> components
}
