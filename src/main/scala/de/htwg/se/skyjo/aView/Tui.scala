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
    println("\t[2] put on discard and flip board card")
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

    val tempD = d
    val h = SupportHandler(ctr, b, tempD, disc)
    val c = SupportCommand(ctr, b, tempD, disc)

    val action: GameState =
      choose match
        case "0" | "1" | "2" | "undo" | "help" | "redo" | "quit" => {
          if c.execute(choose) then ctr.getGameState
          else
            if (choose == "1") then
              inputRequestDeck(ctr.getDeck.getCard.get.toString())
            print(">> Position: ")
            var pos = readLine()
            if pos == "" then pos = "0"
            val return_H = h.handle(choose, pos.toInt)
            return_H match
              case Success(gs) => {
                println("\nIn HANDLE SUCCESS \n")
                gs
              }
              case Failure(e) => ctr.getGameState
        }
        case _ => ctr.getGameState
    ctr.assertGameState(action)
    turnOfPlayer(ctr.getPlIdx)
    println(ctr.getBrds(ctr.getPlIdx))
    discContent(ctr.getDisc)
    turnOptions
    // val action: Try[GameState] =
    //   Try {
    //     choose match {
    //       case "0" | "1" | "2" | "undo" | "help" | "redo" | "quit" =>
    //         // problem beim undo, dass das 'vorherige Deck' all the time used wird
    //         if (choose == "1")
    //           inputRequestDeck(ctr.getDeck.getCard.get.toString())
    //         print(">> Position: ")
    //         val pos = readLine()
    //         val return_H = h.handle(choose, pos.toInt)
    //         return_H match {
    //           case Success(gs) => {
    //             // clearTerm
    //             println("\nIn HANDLE SUCCESS \n")
    //             val nState = ctr.getGameState.currentState.reset()
    //             val nextPl =
    //               ctr.getGameState.copy(plIdx =
    //                 (ctr.getPlIdx + 1) % ctr.getBrds.size,
    //                 currentState = nState
    //               )
    //             ctr.assertGameState(nextPl)
    //             // ctr.assertGameState(ctr.getGameState)
    //
    //             nextPl
    //           }
    //           case Failure(e) => ctr.getGameState
    //         }
    //       case _ =>
    //         // clearTerm
    //         if finished then ending
    //         else
    //           println("\nIn INPUT FAILURE \n")
    //
    //           println(s"${choose} is not valid, doing nothing.\n")
    //
    //           turnOfPlayer(ctr.getPlIdx)
    //           println(ctr.getBrds(ctr.getPlIdx))
    //           discContent(ctr.getDisc)
    //           turnOptions
    //         return true
    //     }
    //   }
    //
    // action match {
    //   case Success(result) => {
    //     val nState = ctr.getGameState.currentState.reset()
    //     println("\nAFTER HANDLE SUCCESS \n")
    //     val nextPl =
    //       ctr.getGameState.copy(
    //         // plIdx = (ctr.getPlIdx + 1) % ctr.getBrds.size,
    //         plIdx = ctr.getPlIdx,
    //         currentState = nState
    //       )
    //     // clearTerm
    //     ctr.assertGameState(nextPl)
    //
    //     // println(s"GameState: ${ctr.currState}")
    //     turnOfPlayer(ctr.getPlIdx)
    //     printfBoard
    //     discContent(ctr.getDisc)
    //     turnOptions
    //   }
    //   case Failure(e) =>
    //     // println(e.getMessage)
    //     println("Inside Action - Failure")
    //     val nState = ctr.getGameState.currentState.nextState()
    //     if c.execute(choose) then
    //       println("\nIn EXECUTE SUCCESS \n")
    //       ctr.assertGameState(ctr.copy(currentState = nState))
    // }
    if finished then ending
    true

}
