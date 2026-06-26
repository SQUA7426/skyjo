package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.aView.Tui
<<<<<<< HEAD
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
import de.htwg.se.skyjo.model.DeckImplementation.*
import de.htwg.se.skyjo.model.BoardImplementation.*
import de.htwg.se.skyjo.model.DiscardPileImplementation.*
=======
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{Card, Deck, DiscardPile, Board}
>>>>>>> origin/docker
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.GameState
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.enablers.Containing

import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.skyjo.SkyjoModule
import net.codingwell.scalaguice.InjectorExtensions.*

class MementoSpec extends AnyWordSpec with Matchers {
  "A MoveCaretaker" should {
    val plCount = 1
<<<<<<< HEAD
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
=======
    val injector = Guice.createInjector(SkyjoModule(plCount))

    val ctr = injector.getInstance(classOf[ControllerInterface])

    ctr.setup()

    val card8 = ctr.toCard(8)
    val mc = ctr.currMemento
    "save, undo and redo" in:
      val mem = Memento(0,card8,0,card8,ctr.getDisc,false)
      mc.save(mem)
      mc.undo(mem, ctr.getDeck, ctr.getBrds(0), ctr.getDisc)
      mc.redo(mem, ctr.getDeck, ctr.getBrds(0), ctr.getDisc)
>>>>>>> origin/docker
  }
}
