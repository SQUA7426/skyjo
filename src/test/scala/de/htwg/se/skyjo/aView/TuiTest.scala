package de.htwg.se.skyjo.aView

import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.{GameState}
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{
  Deck,
  Board,
  Card,
  DiscardPile
}

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import java.io.ByteArrayInputStream

import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.skyjo.SkyjoModule
import net.codingwell.scalaguice.InjectorExtensions.*
import de.htwg.se.skyjo.util.utilComponent.{SupportCommand}

class TuiTest extends AnyWordSpec with Matchers {
  "A Tui " when {
    val plCount = 1
    val injector = Guice.createInjector(SkyjoModule(plCount))

    val ctr = injector.getInstance(classOf[ControllerInterface])

    ctr.setup()

    val tui = new Tui(ctr)
    "an Input Request is done, it" should:
      "handle an unsigned input" in:
        val cmd = new SupportCommand(ctr, ctr.getBrds(0), ctr.getDeck, ctr.getDisc)
        cmd.execute("last")
      "process an 1-0-Input" in:
        val simulatedInput = "1\n0\n0\n0\nquit\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())
        Console.withIn(in) {
          tui.startGame
        }
        
      "process an undo-redo-help-Input" in:
        val simulatedInput = "1\n1\n0\n0\nundo\nredo\nhelp\nquit\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())
        Console.withIn(in) {
          tui.startGame
        }
      "process an Switch-Input" in:
        val simulatedInput = "s\n1\nquit\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())
        Console.withIn(in) {
          tui.startGame
        }
  }
}
