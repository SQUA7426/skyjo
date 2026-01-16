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
import de.htwg.se.skyjo.model.{BoardInterface, CardInterface}

class Tui(ctr: ControllerInterface) extends Observer {
  ctr.add(this)

  def startGame: Unit =
    var input: String = ""
    while (input != "quit") {
      print(">> ")
      input = scala.io.StdIn.readLine()
      if (input != "quit") {
        processInput(input)
      }
    }

  // Try Success Failure
  def processInput(input: String) = {
    val result: Try[Unit] =
      Try(SupportHandler(ctr, ctr.getBrds(ctr.getPlIdx), ctr.getDeck, ctr.getDisc).handle(input))
    // .orElse(SupportCommand(ctr).execute(input, state))
    result match {
      case Success(_) => {}
      case Failure(e) => {
        println(s"Handler Failure with: ${input}")
        SupportCommand(ctr, ctr.getBrds(ctr.getPlIdx), ctr.getDeck, ctr.getDisc)
          .execute(input)
      }
    }
  }

  override def update: Boolean = {
    // println("[DEBUG] TUI Update triggered!")
    val state = ctr.getGameState
    val b = state.boards(ctr.getPlIdx)

    println(s"\n--- Player ${ctr.getPlIdx}'s Turn ---")
    println(b)
    println(s"Discard Pile: | ${state.disc} |")

    // if ctr.hasDrawn then {
      ctr.getDrawn match {
        case Some(card: CardInterface) => {
          println(
            s">> Holding: $card.\n[Index] to put onto Board or [s] to swap with discard & flip BoardCard."
          )
        }
        // case None if state.isFlippingPhase =>
        //   println("Card discarded! Now enter index [0-11] to flip a board card.")
        case None =>
          println("[0] Take discard | [1] Take deck")
      }
    // }
    true
  }
// }
// class Tui(cont: Controller) {

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

  def finishedConf() = (
    println(s"someone is finished: \n")
  )

  // def turn(
  //     b: Board,
  //     d: Deck,
  //     disc: DiscardPile
  // ): Option[(Board, Deck, DiscardPile)] = {
  //   println(b)
  //   println(s"| ${disc.toString()} |\n")
  //   println(s"What do you want to do?")
  //   println("[0] Take discard and switch with a board card")
  //   println("[1] Take deck card and choose:")
  //   println("\t[1] switch with board card")
  //   println("\t[2] put on discard and flip board card")
  //   println("[help] Show help")
  //
  //   val choose: String = readLine()
  //   // val tempD: Deck = {if choose != "1" then d else new Deck(d._mediator, d.deck, d.getUpperCard().toString())}
  //   val tempD = d
  //   val h = SupportHandler(ctr, b, tempD, disc)
  //   val c = SupportCommand(ctr, b, tempD, disc)
  //
  //   val action
  //       : Try[Option[(BoardInterface, DeckInterface, DiscardPileInterface)]] =
  //     Try {
  //       choose match {
  //         case "0" | "1" | "2" | "undo" | "help" | "redo" | "quit" =>
  //           if (choose == "1")
  //             inputRequestDeck(d.getUpperCard().toString())
  //           val return_H = h.handle(choose)
  //           if (return_H == None) then c.execute(choose) else return_H
  //         case _ =>
  //           throw new IllegalArgumentException(
  //             s"${choose} is not valid, doing nothing."
  //           )
  //       }
  //     }
  //
  //   action match {
  //     case Success(result) => result
  //     case Failure(e) =>
  //       println(e.getMessage)
  //       turn(b, d, disc)
  //   }
  // }
}
