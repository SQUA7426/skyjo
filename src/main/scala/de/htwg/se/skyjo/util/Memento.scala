package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.model.{Board, Card, Deck, DiscardPile, fullDeck}
import scala.collection.mutable.Stack
import scala.collection.mutable.Stack
import de.htwg.se.skyjo.util.Mediator

case class Memento(
    fromDeck: Boolean, // true = Deck, false = DiscardPile
    takenCard: Card,
    boardIndex: Int,
    replacedCard: Card,
    var lastDisc: DiscardPile
) {
  override def toString(): String = {
    val s = (s"replacedCard: ${replacedCard.toString()}\n")
    val s1 = s + (s"Card is Taken From Deck: ${fromDeck}\n")
    val s2 = s1 + (s"last Board Idx: ${boardIndex}\n")
    val s3 = s2 + (s"Taken Card: ${takenCard}; ${takenCard.isTurned()}\n")
    val s4 = s3 + (s"lastDisc: ${lastDisc}")
    s4
  }
}

class MoveCaretaker(val med: Mediator) {
  val undoStack = Stack[Memento]()
  val redoStack = Stack[Memento]()
  var updtDeck: Deck = Deck(med)
  var memAct: Boolean = false

  def save(m: Memento): Unit = {
    println("save")
    undoStack.push(m)
    println(undoStack)
    redoStack.clear()
  }

  def undo(
      memento: Memento,
      deck: Deck,
      board: Board,
      disc: DiscardPile
  ): Option[(Board, Deck, DiscardPile)] = {
    val newBoard = board.switch(memento.takenCard, memento.boardIndex)._2
    if (memento.fromDeck) {
      val tempV: Vector[Card] = memento.takenCard +: deck.deck
      updtDeck = new Deck(med, tempV, memento.takenCard.toString())
      redoStack.push(memento)
      setTrue()
      Some(board, deck, memento.lastDisc)
    } else {
      disc.putToDiscardPile(memento.takenCard)
      redoStack.push(memento)
      setTrue()
      Some((newBoard, deck, disc))
    }
  }
  def redo(
      memento: Memento,
      deck: Deck,
      board: Board,
      disc: DiscardPile
  ): Option[(Board, Deck, DiscardPile)] = {
    val newBoard: Board = board.switch(memento.takenCard, memento.boardIndex)._2
    if (memento.fromDeck) {
      updtDeck = new Deck(med, deck.deck.tail, memento.takenCard.toString())
      setTrue()
      Some((newBoard, updtDeck, disc))
    } else {
      disc.putToDiscardPile(memento.replacedCard)
      setTrue()
      Some((newBoard, deck, disc))
    }
  }
  def setTrue(): Unit = memAct = true
  def setFalse(): Unit = memAct = false
  def checkMemAct(): Boolean = memAct
  def getNewDeck(): Deck = updtDeck
}
