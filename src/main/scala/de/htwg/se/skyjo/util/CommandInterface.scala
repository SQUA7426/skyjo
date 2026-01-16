package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.model.{GameState}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface

// trait Command:
//   val next: Option[Command]
//   def execute(cmd: String, state: GameState): Option[GameState]
//
// class UndoCommand(ctrl: ControllerInterface, override val next: Option[Command])
//     extends Command:
//   override def execute(input: String, state: GameState): Option[GameState] =
//     if input == "undo" then
//       ctrl.undo()
//       Some(ctrl.getGameState)
//     else next.flatMap(_.execute(input, state))
//
// class RedoCommand(ctrl: ControllerInterface, override val next: Option[Command])
//     extends Command:
//   override def execute(input: String, state: GameState): Option[GameState] =
//     if input == "redo" then
//       ctrl.redo()
//       Some(ctrl.getGameState)
//     else next.flatMap(_.execute(input, state))
//
// class HelpCommand(override val next: Option[Command]) extends Command:
//   override def execute(input: String, state: GameState): Option[GameState] =
//     if input == "help" then
//       println("-----------------------------------------")
//       println("[undo] undoing the previous changes")
//       println("[redo] redoing the undone changes")
//       println("[quit] exit game")
//       println("-----------------------------------------")
//       Some(state)
//     else next.flatMap(_.execute(input, state))
//
// case class SupportCommand(ctrl: ControllerInterface):
//   private val chain = Some(
//     new HelpCommand(
//       Some(
//         new UndoCommand(
//           ctrl,
//           Some(new RedoCommand(ctrl, None))
//         )
//       )
//     )
//   )
//
//   def execute(input: String, state: GameState): Option[GameState] =
//     chain.flatMap(_.execute(input, state))

trait CommandInterface:
  val cmd: String
  def execute(cmd: String): Boolean
  // def execute(cmd: String, state: GameState): Boolean

