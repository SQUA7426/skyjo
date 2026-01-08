package de.htwg.se.skyjo.aView
import de.htwg.se.skyjo.model.DeckImplementation.*
import de.htwg.se.skyjo.model.DiscardPileImplementation.*
import de.htwg.se.skyjo.model.BoardImplementation.*
import de.htwg.se.skyjo.util.{Memento, MoveCaretaker, SupportHandler}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface

import scala.collection.mutable.Stack
import scala.io.StdIn.readLine
import de.htwg.se.skyjo.util.SupportCommand
import scala.util.Try
import scala.util.{Failure, Success}

import scala.util.{Try, Success, Failure}
import scala.util.Random
import de.htwg.se.skyjo.util.Observer

class Tui(ctrl: ControllerInterface) extends Observer {
  ctrl.add(this)

  def processInput(input: String) = {
    val state = ctrl.getGameState
    val result = SupportHandler(ctrl)
      .handle(input, state)
      .orElse(SupportCommand(ctrl).execute(input, state))

    result match {
      case Some(newState) =>
        ctrl.uptGameState(
          newState
        )
      case None =>
        println("input unknown")
    }
  }

  override def update: Boolean = {
    // println("[DEBUG] TUI Update triggered!")
    val state = ctrl.getGameState
    val b = state.boards(state.playerIdx)

    println(s"\n--- Player ${state.playerIdx}'s Turn ---")
    println(b)
    println(s"Discard Pile: | ${state.disc} |")

    state.drawnCard match {
      case Some(card) =>
        println(s">> Holding: $card.\n[Index] to put onto Board or [s] to swap with discard & flip BoardCard.")
      case None if state.isFlippingPhase =>
        println("Card discarded! Now enter index [0-11] to flip a board card.")
      case None =>
        println("[0] Take discard | [1] Take deck")
    }
    true
  }
}
