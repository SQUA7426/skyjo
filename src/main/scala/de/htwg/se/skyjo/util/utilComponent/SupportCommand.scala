package de.htwg.se.skyjo.util.utilComponent

import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.{BoardInterface, DeckInterface, DiscardPileInterface}
import de.htwg.se.skyjo.util.{CommandInterface, Memento}
import de.htwg.se.skyjo.util.utilComponent.{RedoCommand,HelpCommand, LastCommand, SupportCommand}

case class SupportCommand(
    ctrl: ControllerInterface,
    b: BoardInterface,
    d: DeckInterface,
    disc: DiscardPileInterface
):
  // private val c = new HelpCommand(ctrl, b, d, disc)

  // def execute(command: String): Option[(Board, Deck, DiscardPile)] =
  def execute(command: String): Boolean =
    // c.execute(command)
    command match {
      case "redo" => RedoCommand(ctrl,b,d,disc).execute(command)
      case "undo" => UndoCommand(ctrl,b,d,disc).execute(command)
      // case "quit" => QuitCommand(ctrl,b,d,disc).execute(command)
      case "help" => HelpCommand(ctrl,b,d,disc).execute(command)
      case _ => false
    }
