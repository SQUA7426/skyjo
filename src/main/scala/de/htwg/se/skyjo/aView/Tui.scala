package de.htwg.se.skyjo.aView

// import de.htwg.se.skyjo.model.{}
import de.htwg.se.skyjo.util.{Memento, MoveCaretaker}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface

import scala.collection.mutable.Stack
import scala.io.StdIn.readLine
import de.htwg.se.skyjo.util.utilComponent.{SupportCommand, SupportHandler}

import scala.util.{Try, Success, Failure}
import scala.util.Random
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

class Tui(ctr: ControllerInterface) extends Observer {
  ctr.add(this)
  private def clearTerm = print("\u001b[2J")

  def startGame: Unit =
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
    update(input)
  }

  def inputRequest(b: BoardInterface, disc: String) =
    println(
      s"Which BoardCard [0-${b.getSize._1 * b.getSize._2 - 1}] do you want to switch with ${disc}?"
    )

  def inputRequestDeck(deckCard: String) = (
    println(s"You took ${deckCard}")
  )

  def cardTurnRq(b: BoardInterface) =
    println(
      s"Which BoardCard [0-${b.getSize._1 * b.getSize._2 - 1}] do you want to turn around?"
    )

  def turnOfPlayer(i: Int) = (println(s"Player ${i}:"))

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
    System.exit(0)

  override def update(choose: String): Boolean =
    turnOfPlayer(ctr.getPlIdx)
    val b = ctr.getBrds(ctr.getPlIdx)
    val d = ctr.getDeck
    val disc = ctr.getDisc

    var h = SupportHandler(ctr, b, d, disc)
    val c = SupportCommand(ctr, b, d, disc)

    val action: GameState =
      choose match
        case "0" | "1" | "s" | "undo" | "help" | "redo" | "quit" => {
          if c.execute(choose) then ctr.getGameState
          else
            if choose == "0" then inputRequest(b, disc.toString())
            else if choose == "1" then inputRequestDeck(d.turnUpperCard)

            print(">> Position: ")
            var pos = readLine()
            clearTerm
            if pos == "s" then
              val tmpDeck = new Deck(ctr.getDeckCards, ctr, d.turnUpperCard)
              // switch
              h = SupportHandler(ctr, b, tmpDeck, disc)
              h.handle(pos, 0) match
                case Success(gs) => 
                  printfBoard
                  cardTurnRq(b)
                  print(">> Position: ")
                  pos = readLine()
                  val idx = Integer.parseInt(pos)
                  val newGameState = ctr.switchDeckDisc(gs,b,tmpDeck,idx)
                  newGameState
                case Failure(e)  => ctr.getGameState
            else
              if pos == "" then pos = "0"
              val return_H = h.handle(choose, pos.toInt)
              return_H match
                case Success(gs:GameState) => {
                  val newBrd: BoardInterface = ctr.getReducedBrd(gs.boards(ctr.getPlIdx))._1
                  val copyGameState = gs.copy(
                    boards = gs.boards.updated(ctr.getPlIdx, newBrd)
                    )
                  copyGameState
                }
                case Failure(e) => ctr.getGameState
        }
        case _ => ctr.getGameState
    ctr.assertGameState(action)
    turnOfPlayer(ctr.getPlIdx)
    println(ctr.getBrds(ctr.getPlIdx))
    discContent(ctr.getDisc)
    turnOptions
    if finished then ending
    true

}
