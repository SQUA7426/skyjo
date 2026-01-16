package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{Card, Deck, DiscardPile, Board}
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.GameState
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.enablers.Containing

class MementoSpec extends AnyWordSpec with Matchers {
  "A MoveCaretaker" should {
    val plCount = 1
    val med = new ConcreteMediator()

    val tempState = new GameState(med, Vector.empty, null, null, 0, None)
    val ctr = new Controller(tempState)

    val deck = new Deck(ctr.fullDeck(), ctr)
    val disc = new DiscardPile(ctr)

    val plBoards = Vector.fill(plCount)(new Board(med, 4, 3, Vector.empty))

    ctr.state = new GameState(med, plBoards, deck, disc, 0, None)
    ctr.setup()

    val mc = new MoveCaretaker()
    "save, undo and redo" in:
      mc.save(ctr.state)
      mc.undo(ctr.state)
      mc.redo(ctr.state)
  }
}
