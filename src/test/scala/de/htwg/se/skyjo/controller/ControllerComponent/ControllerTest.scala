package de.htwg.se.skyjo.controller.ControllerComponent

import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
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

    val tui = new Tui(ctr)
    "it is working, it" should {
      "do a setup() and update Tui" in:
        ctr.setup()
      "get Mediator, GameState, Deck and Discard-Card" in:
        ctr.getMediator shouldBe a[Mediator]
        ctr.getGameState shouldBe a[GameState]
        ctr.getDeck shouldBe a[Vector[CardInterface]]
        ctr.getDiscCard() shouldBe a[CardInterface]
      "be able to fill a Board" in:
        val (afterBoard, afterDeck) = ctr.fillBoard(4, 3, ctr.state.deck)
        afterBoard shouldBe a[BoardInterface]
        afterDeck shouldBe a[DeckInterface]
      "execute undo and redo" in:
        val oldState = ctr.state
        ctr.undo()
        ctr.redo()
      "execute a move and update GAMESTATE" in:
        val oldState = ctr.state
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
    }
}
