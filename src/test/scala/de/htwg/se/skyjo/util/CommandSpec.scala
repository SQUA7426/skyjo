package de.htwg.se.skyjo.util

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
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{Card, Deck, DiscardPile, Board}
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.GameState
import de.htwg.se.skyjo.util.utilComponent.SupportCommand

>>>>>>> origin/docker
import org.scalatest.matchers.should.Matchers

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.enablers.Containing
import java.io.ByteArrayInputStream

import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.skyjo.SkyjoModule
import net.codingwell.scalaguice.InjectorExtensions.*

class CommandSpec extends AnyWordSpec with Matchers {
  "A Command" should {
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

    val cCom = new SupportCommand(ctr)
    // ---------------------INPUT -----------------------------------------//
    "execute the undo cmd" in {
      val gl = cCom.execute("undo", ctr.state)
    }
    "execute the redo cmd" in {
        val gl = cCom.execute("redo", ctr.state)
=======
    val injector = Guice.createInjector(SkyjoModule(plCount))

    val ctr = injector.getInstance(classOf[ControllerInterface])

    ctr.setup()

    val cCom = new SupportCommand(ctr, ctr.getBrds(0), ctr.getDeck, ctr.getDisc)
    // ---------------------INPUT -----------------------------------------//
    "execute the undo cmd" in {
      val gl = cCom.execute("undo")
    }
    "execute the redo cmd" in {
        val gl = cCom.execute("redo")
>>>>>>> origin/docker
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
