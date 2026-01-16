package de.htwg.se.skyjo.util.utilComponent

import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.{BoardInterface, DeckInterface, DiscardPileInterface}
import de.htwg.se.skyjo.util.{CommandInterface, Memento}
// import de.htwg.se.skyjo.util.utilComponent.{RedoCommand, QuitCommand,HelpCommand, LastCommand, SupportCommand}

class UndoCommand(
    ctrl: ControllerInterface,
    val b: BoardInterface,
    val d: DeckInterface,
    val disc: DiscardPileInterface
) extends CommandInterface:
  override val cmd: String = "undo"
  // override val next: CommandInterface = RedoCommand(ctrl, b, d, disc)

  // override def execute(command: String): Option[(BoardInterface, DeckInterface, DiscardPileInterface)] =
  override def execute(command: String): Boolean =
    if command.compareTo(cmd) == 0 then {
      println(s"UndoCommand executed command: ${command}")
      if (ctrl.currMemento.undoStack != null) {
        val mem: Memento = ctrl.currMemento.undoStack(0)
        Some(ctrl.currMemento.undo(mem, d, b, disc))
          // .getOrElse(this.next.execute(cmd))
        true
      }
      // else next.execute(cmd)
      else false
    }
    // else this.next.execute(command)
    else false

