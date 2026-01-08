package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
import de.htwg.se.skyjo.model.DeckImplementation.*
import de.htwg.se.skyjo.model.BoardImplementation.*
import de.htwg.se.skyjo.model.DiscardPileImplementation.*
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.GameState
import org.scalatest.matchers.should.Matchers

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.enablers.Containing
import java.io.ByteArrayInputStream

class CommandSpec extends AnyWordSpec with Matchers {
  "A Command" should {
    val plCount = 1
    val med = new ConcreteMediator()

    val tempState = new GameState(med, Vector.empty, null, null, 0, None)
    val ctr = new Controller(tempState)

    val deck = new Deck(ctr.fullDeck(), ctr)
    val disc = new DiscardPile(ctr)

    val plBoards = Vector.fill(plCount)(new Board(med, 4, 3, Vector.empty))

    ctr.state = new GameState(med, plBoards, deck, disc, 0, None)
    ctr.setup()

    val cCom = new SupportCommand(ctr)
    // ---------------------INPUT -----------------------------------------//
    "execute the undo cmd" in {
      val gl = cCom.execute("undo", ctr.state)
    }
    "execute the redo cmd" in {
        val gl = cCom.execute("redo", ctr.state)
      // }
    }

    "execute the help cmd" in:
      cCom.execute("help", ctr.state)
    // "execute the quit cmd" in:
    //   cCom.execute("quit") shouldBe None
    "not execute the x cmd" in:
      cCom.execute("x", ctr.state)
  }
}
