package de.htwg.se.skyjo.aView
import de.htwg.se.skyjo.model
import de.htwg.se.skyjo.model.{Board, Deck, DiscardPile}
import de.htwg.se.skyjo.controller.ControllerComponent.Controller
import de.htwg.se.skyjo.util.{MoveCaretaker, Memento, SupportHandler}
import scala.collection.mutable.Stack

import scala.io.StdIn.readLine
import de.htwg.se.skyjo.util.SupportCommand
import scala.util.Try
import scala.util.{Failure, Success}

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

    val choose = readLine()
    choose match {
      case "0" | "1" | "2" =>
        {
          println(d.toString())
          if (choose == "1") {
            val turnD = new Deck(d._mediator, d.remove(1), d.turnUpperCard())
            inputRequestDeck(d.toString())
            // toOption(tryHandler(choose, cont, b, turnD, disc))
            println("In if choose==1")
            return cont.takeFromDeck(b,turnD,disc)
          }
          toOption(tryHandler(choose, cont, b, d, disc))
        }
      case "undo" | "help" | "redo" | "quit" =>
        toOption(tryCommand(choose, cont, b, d, disc))
      case _ =>
        println("Invalid input. Please try again.")
        turn(b, d, disc)
    }
  }

  def toOption(
      e: Either[(Board, Deck, DiscardPile), Option[(Board, Deck, DiscardPile)]]
  ): Option[(Board, Deck, DiscardPile)] =
    e match {
      case Right(opt)  => opt
      case Left(tuple) => Some(tuple)
    }

  def tryHandler(
      inputStr: String,
      cont: Controller,
      b: Board,
      d: Deck,
      disc: DiscardPile
  ): Either[(Board, Deck, DiscardPile), Option[(Board, Deck, DiscardPile)]] = {
    val h = SupportHandler(cont, b, d, disc)
    Try {
      println("In TryHandler")
      h.handle(inputStr)
    } match {
      case Success(s)            => Right(s)
      case Failure(e: Throwable) => Left((b, d, disc))
    }
  }

  def tryCommand(
      inputStr: String,
      cont: Controller,
      b: Board,
      d: Deck,
      disc: DiscardPile
  ): Either[(Board, Deck, DiscardPile), Option[
    (Board, Deck, DiscardPile)
  ]] = {
    val c = SupportCommand(cont, b, d, disc)
    Try {
      println("In TryCommand")
      c.execute(inputStr)
    } match {
      case Success(s)            => Right(s)
      case Failure(e: Throwable) => Left((b, d, disc))
      // case Some((b:Board,d:Deck,disc:DiscardPile)) => Left(Some(b,d,disc))
    }
  }
}
