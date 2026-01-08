package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.util.{Mediator,ConcreteMediator,Colleague,Handler, MoveCaretaker,Memento}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.Controller
import de.htwg.se.skyjo.model.BoardInterface
import de.htwg.se.skyjo.model.BoardImplementation.Board
import de.htwg.se.skyjo.model.BoardImplementation.fillBoard
import de.htwg.se.skyjo.model.DeckImplementation.Deck
import de.htwg.se.skyjo.model.DeckInterface
import de.htwg.se.skyjo.model.DiscardPileImplementation.DiscardPile
import de.htwg.se.skyjo.model.CardImplementation.Card
import de.htwg.se.skyjo.model.CardImplementation.{toCard, len, isCard}
import de.htwg.se.skyjo.model.GameState
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.enablers.Containing

class MementoSpec extends AnyWordSpec with Matchers {
  "A MoveCaretaker" should {
    val med = new ConcreteMediator
    val disc = new DiscardPile(med,"4")
    val tempB = Board(med)
    val b = tempB._1
    val deck = tempB._2
    val c1 = Some(Card(med,1, true))
    val state = GameState(Vector(b),deck,disc,0, c1)
    val mc = new MoveCaretaker()
    "save, undo and redo" in:
      mc.save(state)
      mc.undo(state)
      mc.redo(state)
  }
}
