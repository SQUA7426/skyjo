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
  // override val next: CommandInterface = QuitCommand(ctrl, b, d, disc)

  // override def execute(command: String): Option[(Board, Deck, DiscardPile)] =
  override def execute(command: String): Boolean =
    if command.compareTo(cmd) == 0 then {
      println(s"RedoCommand executed command: ${command}")
      if (!ctrl.currMemento.redoStack.isEmpty) {
        val mem: Memento = ctrl.currMemento.redoStack(0)
        Some(ctrl.currMemento.redo(mem, d, b, disc))
          // .getOrElse(next.execute(cmd))
        true
      }
      // else next.execute(cmd)
      else false
    }
  // else this.next.execute(command)
    else false

