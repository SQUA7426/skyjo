package de.htwg.se.skyjo.util.utilComponent

import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.{BoardInterface, DeckInterface, DiscardPileInterface}
import de.htwg.se.skyjo.util.{CommandInterface, Memento}
import de.htwg.se.skyjo.util.utilComponent.{RedoCommand,HelpCommand, LastCommand, SupportCommand, LoadSaveCommand}

case class SupportCommand(
    ctrl: ControllerInterface,
    b: BoardInterface,
    d: DeckInterface,
    disc: DiscardPileInterface
):
  def execute(command: String): Boolean =
    command match {
      case "redo" => RedoCommand(ctrl,b,d,disc).execute(command)
      case "undo" => UndoCommand(ctrl,b,d,disc).execute(command)
      case "load json" | "save json" | "load xml" | "save xml" => LoadSaveCommand(ctrl, b, d, disc).execute(command)
      case "help" => HelpCommand(ctrl,b,d,disc).execute(command)
      case _ => {LastCommand().execute(command); false}
    }
