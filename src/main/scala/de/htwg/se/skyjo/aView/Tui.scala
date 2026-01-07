package de.htwg.se.skyjo.aView
import de.htwg.se.skyjo.model
import de.htwg.se.skyjo.model.{Board, Deck, DiscardPile}
import de.htwg.se.skyjo.controller.ControllerComponent.Controller
import de.htwg.se.skyjo.util.{Memento, MoveCaretaker, SupportHandler}

import scala.collection.mutable.Stack
import scala.io.StdIn.readLine
import de.htwg.se.skyjo.util.SupportCommand
import scala.util.Try
import scala.util.{Failure, Success}

import scala.util.{Try, Success, Failure}
import scala.util.Random

class Tui(cont: Controller) {

  def inputRequest(b: Board, disc: String) =
    println(
      s"Which BoardCard [0-${b.xSize * b.ySize - 1}] do you want to switch with ${disc}?"
    )

  def inputRequestDeck(deckCard: String) = (
    println(s"You took ${deckCard}")
  )

  def cardTurnRq(b: Board) =
    println(
      s"Which BoardCard [0-${b.xSize * b.ySize - 1}] do you want to turn around?"
    )

  def turnOfPlayer(i: Int) = (println(s"Player ${i}:"))

  def finishedConf() = (
    println(s"someone is finished: \n")
  )
  def processInput(i: Int): Unit = ()

  def turn(
      b: Board,
      d: Deck,
      disc: DiscardPile
  ): Option[(Board, Deck, DiscardPile)] = {
    println(b)
    println(s"| ${disc.toString()} |\n")
    println(s"What do you want to do?")
    println("[0] Take discard and switch with a board card")
    println("[1] Take deck card and choose:")
    println("\t[1] switch with board card")
    println("\t[2] put on discard and flip board card")
    println("[help] Show help")

    val choose: String = readLine()
    // val tempD: Deck = {if choose != "1" then d else new Deck(d._mediator, d.deck, d.getUpperCard().toString())}
    val tempD = d
    val h = SupportHandler(cont, b, tempD, disc)
    val c = SupportCommand(cont, b, tempD, disc)

    val action: Try[Option[(Board, Deck, DiscardPile)]] = Try {
      choose match {
        case "0" | "1" | "2" | "undo" | "help" | "redo" | "quit" =>
          if (choose == "1")
            inputRequestDeck(d.getUpperCard().toString())
          val return_H = h.handle(choose)
          if (return_H == None) then c.execute(choose) else return_H
        case _ =>
          throw new IllegalArgumentException(
            s"${choose} is not valid, doing nothing."
          )
      }
    }

    action match {
      case Success(result) => result
      case Failure(e) =>
        println(e.getMessage)
        turn(b, d, disc)
    }
  }
}
