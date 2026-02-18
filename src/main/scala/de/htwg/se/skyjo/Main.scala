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

def main(args: Array[String]): Unit = {
  // println("How many players:")
  // var pl = scala.io.StdIn.readLine()
  // if pl == "" then pl = "1"
  // val plCount = Integer.parseInt(pl)
  val plCount = 1

  val tempState = new GameState(Vector.empty, Vector.empty, null, null, 0, State.BEGIN)
  val ctr = new Controller(tempState, plCount)

  ctr.setup()

  val tui = new Tui(ctr)
  val tuiThread = new Thread(() => tui.startGame)
  tuiThread.start()

  Gui.ctr = ctr
  Gui.main(args)
}
