package de.htwg.se.skyjo.util
import de.htwg.se.skyjo.model.{Board,Deck,DiscardPile}
import de.htwg.se.skyjo.controller.ControllerComponent.Controller

trait Command:
  val cmd: String
  val next: Command
  def execute(command: String):Option[(Board,Deck,DiscardPile)]

class HelpCommand(
    ctrl: Controller,
    val b: Board,
    val d: Deck,
    val disc: DiscardPile
) extends Command:
  override val cmd: String = "help"
  override val next: Command = UndoCommand(ctrl,b,d,disc)

  override def execute(command: String): Option[(Board, Deck, DiscardPile)] =
    if command.compareTo(cmd) == 0 then {
      println(s"RedoCommand executed command: ${command}")
      println("-----------------------------------------")
      println("[undo] undoing the previous changes")
      println("[redo] redoing the undone changes")
      println("[quit] exit game")
      println("-----------------------------------------")
      None
    } else this.next.execute(command)


class UndoCommand(
    ctrl: Controller,
    val b: Board,
    val d: Deck,
    val disc: DiscardPile
) extends Command:
  override val cmd: String = "undo"
  override val next: Command = RedoCommand(ctrl,b,d,disc)

  override def execute(command: String): Option[(Board, Deck, DiscardPile)] =
    if command.compareTo(cmd) == 0 then {
      println(s"UndoCommand executed command: ${command}")
      if (ctrl.mementostack.undoStack != null) {
        val mem: Memento = ctrl.mementostack.undoStack(0)
        return Some(ctrl.mementostack.undo(mem, d, b, disc)).getOrElse(this.next.execute(cmd))
      }
      next.execute(cmd)
    } else this.next.execute(command)

class RedoCommand(
    ctrl: Controller,
    val b: Board,
    val d: Deck,
    val disc: DiscardPile
) extends Command:
  override val cmd: String = "redo"
  override val next: Command = QuitCommand(ctrl,b,d,disc)

  override def execute(command: String): Option[(Board, Deck, DiscardPile)] =
    if command.compareTo(cmd) == 0 then {
      println(s"RedoCommand executed command: ${command}")
      val mem: Memento = ctrl.mementostack.redoStack(0)
      return Some(ctrl.mementostack.redo(mem,d,b,disc)).getOrElse(next.execute(cmd))
    } else this.next.execute(command)

class QuitCommand(
    ctrl: Controller,
    val b: Board,
    val d: Deck,
    val disc: DiscardPile
) extends Command:
  override val cmd: String = "quit"
  override val next: Command = LastCommand()

  override def execute(command: String): Option[(Board, Deck, DiscardPile)] =
    if command.compareTo(cmd) == 0 then {
      println(s"QuitCommand executed command: ${command}")
      println("Quitting Game...")
      System.exit(0)
      None
    } else this.next.execute(command)

class LastCommand extends Command:
  override val cmd: String = "LAST"
  override val next: Command = this

  override def execute(command: String): Option[(Board, Deck, DiscardPile)] = {
    println(s"The command: '${command}' arrived at the LastCommand"); None
  }

case class SupportCommand(
    ctrl: Controller,
    b: Board,
    d: Deck,
    disc: DiscardPile
):
  private val c = new HelpCommand(ctrl, b, d, disc)

  def execute(command: String): Option[(Board, Deck, DiscardPile)] =
    c.execute(command)
