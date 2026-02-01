package de.htwg.se.skyjo.util.utilComponent

import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.{BoardInterface, DeckInterface, DiscardPileInterface}
import de.htwg.se.skyjo.util.{CommandInterface, Memento}
// import de.htwg.se.skyjo.util.utilComponent.{RedoCommand, QuitCommand,HelpCommand, LastCommand, SupportCommand}
class RedoCommand(
    ctrl: ControllerInterface,
    val b: BoardInterface,
    val d: DeckInterface,
    val disc: DiscardPileInterface
) extends CommandInterface:
  override val cmd: String = "redo"

  override def execute(command: String): Boolean =
    if command.compareTo(cmd) == 0 then {
      println(s"RedoCommand executed command: ${command}")
      if (!ctrl.currMemento.redoStack.isEmpty) {
        ctrl.redo()
        true
      }
      else false
    }
    else false

