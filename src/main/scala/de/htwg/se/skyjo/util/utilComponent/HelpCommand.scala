package de.htwg.se.skyjo.util.utilComponent

import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.{BoardInterface, DeckInterface, DiscardPileInterface}
import de.htwg.se.skyjo.util.{CommandInterface, Memento}
// import de.htwg.se.skyjo.util.utilComponent.{RedoCommand, QuitCommand,HelpCommand, LastCommand, SupportCommand}

class HelpCommand(
    ctrl: ControllerInterface,
    val b: BoardInterface,
    val d: DeckInterface,
    val disc: DiscardPileInterface
) extends CommandInterface:
  override val cmd: String = "help"

  override def execute(command: String): Boolean =
    if command.compareTo(cmd) == 0 then {
      println(s"RedoCommand executed command: ${command}")
      println("-----------------------------------------")
      println("[undo] undoing the previous changes")
      println("[redo] redoing the undone changes")
      println("[quit] exit game")
      println("-----------------------------------------")
      true
    }
    else
      false
