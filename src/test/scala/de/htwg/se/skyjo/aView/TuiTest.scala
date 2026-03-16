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

class TuiTest extends AnyWordSpec with Matchers {
  "A Tui " when {
    val plCount = 1
    val injector = Guice.createInjector(SkyjoModule(plCount))

    val ctr = injector.getInstance(classOf[ControllerInterface])

    ctr.setup()

    val tui = new Tui(ctr)
    "an Input Request is done, it" should:
      "process an Input" in:
        val simulatedInput = "s\n1\nquit\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())
        Console.withIn(in) {
          tui.startGame
        }
  }
}
