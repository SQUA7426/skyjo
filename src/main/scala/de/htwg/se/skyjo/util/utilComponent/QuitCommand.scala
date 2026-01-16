package de.htwg.se.skyjo.util.utilComponent

import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.{BoardInterface, DeckInterface, DiscardPileInterface}
import de.htwg.se.skyjo.util.{CommandInterface, Memento}
// import de.htwg.se.skyjo.util.utilComponent.{RedoCommand, QuitCommand,HelpCommand, LastCommand, SupportCommand}
class QuitCommand(
    ctrl: ControllerInterface,
    val b: BoardInterface,
    val d: DeckInterface,
    val disc: DiscardPileInterface
) extends CommandInterface:
  override val cmd: String = "quit"
  // override val next: Command = LastCommand()

  // override def execute(command: String): Option[(Board, Deck, DiscardPile)] =
  override def execute(command: String): Boolean =
    if command.compareTo(cmd) == 0 then {
      println(s"QuitCommand executed command: ${command}")
      println("Quitting Game...")
      System.exit(0)
      // None
      true
    }
    // else this.next.execute(command)
    else
      false

