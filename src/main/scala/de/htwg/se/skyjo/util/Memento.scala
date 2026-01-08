package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.model.GameState
import scala.collection.mutable.Stack

case class Memento(state: GameState)

class MoveCaretaker {
  private val undoStack = Stack[GameState]()
  private val redoStack = Stack[GameState]()

  def save(state: GameState): Unit = {
    undoStack.push(state)
    redoStack.clear()
  }

  def undo(currentState: GameState): Option[GameState] = {
    if (undoStack.nonEmpty) {
      redoStack.push(currentState)
      Some(undoStack.pop())
    } else None
  }

  def redo(currentState: GameState): Option[GameState] = {
    if (redoStack.nonEmpty) {
      undoStack.push(currentState)
      Some(redoStack.pop())
    } else None
  }
}
