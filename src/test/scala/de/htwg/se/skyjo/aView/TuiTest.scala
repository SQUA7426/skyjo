package de.htwg.se.skyjo.aView

import de.htwg.se.skyjo.aView.Tui
<<<<<<< HEAD
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
import de.htwg.se.skyjo.model.DeckImplementation.*
import de.htwg.se.skyjo.model.BoardImplementation.*
import de.htwg.se.skyjo.model.DiscardPileImplementation.*
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.GameState
=======
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.{GameState}
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{
  Deck,
  Board,
  Card,
  DiscardPile
}
>>>>>>> origin/docker

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import java.io.ByteArrayInputStream

import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.skyjo.SkyjoModule
import net.codingwell.scalaguice.InjectorExtensions.*
import de.htwg.se.skyjo.util.utilComponent.{SupportCommand, LastHandler, LoadSaveCommand, DeckHandler, DiscHandler, SwitchHandler, UndoCommand, RedoCommand}

class TuiTest extends AnyWordSpec with Matchers {
  "A Tui " when {
    val plCount = 1
<<<<<<< HEAD
    val med = new ConcreteMediator()

    val tempState = new GameState(med, Vector.empty, null, null, 0, None)
    val ctr = new Controller(tempState)

    val deck = new Deck(ctr.fullDeck(), ctr)
    val disc = new DiscardPile(ctr)

    val plBoards = Vector.fill(plCount)(new Board(med, 4, 3, Vector.empty))

    ctr.state = new GameState(med, plBoards, deck, disc, 0, None)
=======
    val injector = Guice.createInjector(SkyjoModule(plCount))

    val ctr = injector.getInstance(classOf[ControllerInterface])

>>>>>>> origin/docker
    ctr.setup()

    val tui = new Tui(ctr)
    "an Input Request is done, it" should:
<<<<<<< HEAD
      "process an Input" in:
        tui.processInput("1")
        tui.processInput("x")
      "update shouldBe true" in:
        tui.update shouldBe true
        ctr.state = ctr.state.copy(isFlippingPhase = true)
        tui.update shouldBe true
      "can quit the game" in:
        val simulatedInput = "1\nquit"
=======
      "execute an unsigned input" in:
        val cmd = new SupportCommand(ctr, ctr.getBrds(0), ctr.getDeck, ctr.getDisc)
        cmd.execute("last")
      "handle a drawDeck" in:
        val deH = new DeckHandler(ctr, ctr.getBrds(0), ctr.getDeck, ctr.getDisc).handle("1",0)
      "handle a discInput" in:
        val diH = new DiscHandler(ctr, ctr.getBrds(0), ctr.getDeck, ctr.getDisc).handle("0",0)
      "handle a Switch" in:
        val sH = new SwitchHandler(ctr, ctr.getBrds(0), Deck(ctr.getDeckCards, ctr.getDeck.turnUpperCard), ctr.getDisc).handle("s", 0)
      "handle an unsigned input" in:
        val lh = new LastHandler(ctr).handle("last",0)

      "handle invalid undo-, redo-, load_save-Command" in:
        val uC = new UndoCommand(ctr, ctr.getBrds(0), ctr.getDeck, ctr.getDisc).execute("x")
        val rC = new RedoCommand(ctr, ctr.getBrds(0), ctr.getDeck, ctr.getDisc).execute("x")
        val lsC = new LoadSaveCommand(ctr, ctr.getBrds(0), ctr.getDeck, ctr.getDisc).execute("x")
        


      "process an 1-0-Input" in:
        val simulatedInput = "undo\nredo\n1\n0\n0\n0\nquit\n"
>>>>>>> origin/docker
        val in = new ByteArrayInputStream(simulatedInput.getBytes())
        Console.withIn(in) {
          tui.startGame
        }
<<<<<<< HEAD
=======
        
      "process an undo-redo-help-Input" in:
        val simulatedInput = "undo\nredo\n1\n1\n0\n0\nundo\nredo\nhelp\nquit\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())
        Console.withIn(in) {
          tui.startGame
        }

      "process an save-load-Input" in:
        val simulatedInput = "1\n0\n0\n0\nsave xml\nsave json\nload xml\nload json\nquit\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())
        Console.withIn(in) {
          tui.startGame
        }

      "process an Switch-Input" in:
        val simulatedInput = "1\ns\n0\n1\nx\nquit\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())
        Console.withIn(in) {
          tui.startGame
        }
      "execute the ending" in:
        tui.ending
      "can update" in:
        tui.update("") shouldBe a[Boolean]
>>>>>>> origin/docker
  }
}
