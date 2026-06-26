package de.htwg.se.skyjo.aView

import de.htwg.se.skyjo.util.{Memento, MoveCaretaker}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface

import scala.io.StdIn.readLine
import de.htwg.se.skyjo.util.utilComponent.{SupportCommand, SupportHandler}

import scala.util.{Try, Success, Failure}
import de.htwg.se.skyjo.util.Observer
import de.htwg.se.skyjo.model.{
  State,
  BoardInterface,
  CardInterface,
  DeckInterface,
  DiscardPileInterface,
  GameState
}
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{Deck, Card}

class Tui(ctr: ControllerInterface, interactive: Boolean = true) extends Observer {
  ctr.add(this)
  private def clearTerm = print("\u001b[2J")

  def startGame: Unit =
    if !interactive then return

    turnOfPlayer(ctr.getPlIdx)
    printfBoard
    discContent(ctr.getDisc)
    turnOptions

    var input: String = ""
    while (input != "quit") {
      print(">> ")
      input = scala.io.StdIn.readLine()
      if (input != "quit") {
        processInput(input)
      }
    }

  def processInput(input: String): Unit = {
    val c =
      SupportCommand(ctr, ctr.getBrds(ctr.getPlIdx), ctr.getDeck, ctr.getDisc)
    input match {
      case "undo" | "help" | "redo" | "load json" | "save json" | "load xml" |
          "save xml" =>
        c.execute(input)
        turnOfPlayer(ctr.getPlIdx)
        printfBoard
        discContent(ctr.getDisc)
        turnOptions

      case "0" =>
        inputRequest(ctr.getBrds(ctr.getPlIdx), ctr.getDisc.toString())
        val (card, _) = ctr.draw()
        printfBoard
        print(">> Position: ")
        var pos = readLine()
        val idx = Try(Integer.parseInt(pos)).getOrElse(0)
        ctr.drawFromDisc(idx)
        printfBoard
        discContent(ctr.getDisc)
        turnOptions

      case "1" =>
        inputRequestDeck(ctr.getDeck.turnUpperCard)
        printfBoard
        print(">> Position: ")
        var pos = readLine()
        if pos == "s" then {
          val tmpDeck = new Deck(ctr.getDeckCards, ctr.getDeck.turnUpperCard)
          val switch_handler =
            SupportHandler(ctr, ctr.getBrds(ctr.getPlIdx), tmpDeck, ctr.getDisc)
          switch_handler.handle(pos, 0) match {
            case Success(gs) =>
              printfBoard
              cardTurnRq(ctr.getBrds(ctr.getPlIdx))

              ctr.tuiSwitch(gs, tmpDeck)
              turnOfPlayer(ctr.getPlIdx)
              printfBoard
              discContent(ctr.getDisc)
              turnOptions

            case Failure(_) =>
              println("Discarded Turn.")
              turnOfPlayer(ctr.getPlIdx)
              printfBoard
              discContent(ctr.getDisc)
              turnOptions
          }
        } else {
          val idx = Try(pos.toInt).getOrElse(0)
          ctr.drawFromDeck(idx)
          turnOfPlayer(ctr.getPlIdx)
          printfBoard
          discContent(ctr.getDisc)
          turnOptions
        }
      case _ => ctr.assertGameState(ctr.getGameState)
    }
  }

  def inputRequest(b: BoardInterface, disc: String) =
    println(
      s"Which BoardCard [0-${b.getSize._1 * b.getSize._2 - 1}] do you want to switch with ${disc}?"
    )

  def inputRequestDeck(deckCard: String) =
    println(s"You took ${deckCard}")

  def cardTurnRq(b: BoardInterface) =
    println(
      s"Which BoardCard [0-${b.getSize._1 * b.getSize._2 - 1}] do you want to turn around?"
    )

  def turnOfPlayer(i: Int) = println(s"Player ${i}:")

  def discContent(disc: DiscardPileInterface) =
    println(s"| ${disc.toString()} |\n")

  def printfBoard = println(s"${ctr.getBrds(ctr.getPlIdx)}")

  def turnOptions =
    println(s"What do you want to do?")
    println("[0] Take discard and switch with a board card")
    println("[1] Take deck card and choose:")
    println("\t[1] switch with board card")
    println("\t[s] put on discard and flip board card")
    println("[help] Show help")

  def finished: Boolean =
    ctr.getBoard.forall(row => row.forall(c => c.isTurned == true))

  def ending: Unit =
    for i <- 0 until ctr.getBrds.size do {
      val tmpState = ctr.getGameState.copy(plIdx = i)
      turnOfPlayer(i)
      println(
        s"SUM:  ${ctr.getBoard.flatten.map(c => c.getValue).fold(0)((x, y) => x + y).toString()}"
      )
    }
    if finished then System.exit(0)

  override def update(choose: String): Boolean =
    if interactive then
      turnOfPlayer(ctr.getPlIdx)
      printfBoard
      discContent(ctr.getDisc)
      turnOptions
      if finished then ending
    true
}