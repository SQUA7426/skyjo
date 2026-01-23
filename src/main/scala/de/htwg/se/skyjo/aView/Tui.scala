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
  var iter: Int = 0

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

  def finishedConf() = (
    println(s"someone is finished: \n")
  )

  override def update(choose: String): Boolean =
    val b = ctr.getBrds(ctr.getPlIdx)
    val d = ctr.getDeck
    val disc = ctr.getDisc
    // turnOfPlayer(ctr.getPlIdx)
    // println(b)
    // discContent(disc)
    // turnOptions

    val tempD = d
    val h = SupportHandler(ctr, b, tempD, disc)
    val c = SupportCommand(ctr, b, tempD, disc)

    val action: Try[GameState] =
      Try {
        choose match {
          case "0" | "1" | "2" | "undo" | "help" | "redo" | "quit" =>
            if (choose == "1")
              inputRequestDeck(d.getCard.get.toString())
            print(">> Position: ")
            val pos = readLine()
            val return_H = h.handle(choose, pos.toInt)
            return_H match {
              case Success(gs) => {
                val ng = gs.copy(currentState = gs.currentState.nextState())
                choose match { case "0" | "1" | "2" => ctr.assertGameState(ng) }

                println(s"GameState: ${ctr.currState}")
                printfBoard
                discContent(ctr.getDisc)
                turnOptions

                ng
              }
              case Failure(e) => ctr.getGameState
            }
          case _ =>
            println(s"${choose} is not valid, doing nothing.\n")
            println(s"GameState: ${ctr.currState}")
            println(ctr.getBrds(ctr.getPlIdx))
            discContent(ctr.getDisc)
            turnOptions
            ctr.getGameState
        }
      }

    action match {
      case Success(result) => {
        val nState = ctr.getGameState.currentState.reset()
        ctr.assertGameState(ctr.copy(currentState = nState))
        true
      }
      case Failure(e) =>
        // println(e.getMessage)
        println("Inside Action - Failure")
        val nState = ctr.getGameState.currentState.nextState()
        if c.execute(choose) then
          ctr.assertGameState(ctr.copy(currentState = nState))
        else ctr.assertGameState(ctr.copy(currentState = ctr.currState.reset()))
        true
    }

}
