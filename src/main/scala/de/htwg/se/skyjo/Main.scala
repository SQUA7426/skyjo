package de.htwg.se.skyjo

import de.htwg.se.skyjo.aView.Gui.Gui
import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.Controller
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.util.MoveCaretaker
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.{
  State,
  GameState,
  BoardInterface,
  CardInterface,
  DeckInterface,
  DiscardPileInterface
}
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{Deck, DiscardPile, Board, Card}
import scala.io.StdIn.readLine

import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.skyjo.SkyjoModule
import net.codingwell.scalaguice.InjectorExtensions.*

@main def start(): Unit = {
  // println("How many players:")
  // var pl = scala.io.StdIn.readLine()
  // if pl == "" then pl = "1"
  // val plCount = Integer.parseInt(pl)
  val plCount = 1
  val injector = Guice.createInjector(SkyjoModule(plCount))

  val ctr = injector.getInstance(classOf[ControllerInterface])

  ctr.setup()

  val tui = new Tui(ctr)
  val tuiThread = new Thread(() => tui.startGame)
  tuiThread.setDaemon(true)
  tuiThread.start()

  Gui.ctr = ctr
  Gui.main(Array.empty)
}
