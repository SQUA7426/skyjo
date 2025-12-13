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
    var lastDisc: DiscardPile,
    replacedCardTurned: Boolean
) {
  override def toString(): String = {
    val s = (s"replacedCard: ${replacedCard.toString()}; turned: ${replacedCard.isTurned()}\n")
    val s1 = s + (s"Card is Taken From Deck: ${fromDeck}\n")
    val s2 = s1 + (s"last Board Idx: ${boardIndex}\n")
    val s3 = s2 + (s"Taken Card: ${takenCard}; turned: ${takenCard.isTurned()}\n")
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
    println("clearing undoStack...")
    undoStack.clear()
    println("saving...")
    undoStack.push(m)
    println(undoStack)
    // redoStack.clear()
  }

  def undo(
      memento: Memento,
      deck: Deck,
      board: Board,
      disc: DiscardPile
  ): Option[(Board, Deck, DiscardPile)] = {
    val newBoard:Board = board.swapFromMem(memento.replacedCard, memento.boardIndex)
    if (memento.fromDeck) {
      val tempV: Vector[Card] = memento.takenCard +: deck.deck
      updtDeck = new Deck(med, tempV, memento.takenCard.toString())
      redoStack.push(memento)
      setTrue()
      println("clearing undoStack...")
      undoStack.clear()
      println("saving...")
      undoStack.push(memento)
      println(undoStack)
      Some(newBoard, deck, memento.lastDisc)
    } else {    // DiscardPile
      val disc2:DiscardPile = disc.putToDiscardPile(memento.takenCard)._1
      redoStack.push(memento)
      setTrue()
      println("clearing undoStack...")
      undoStack.clear()
      println("saving...")
      undoStack.push(memento)
      println(undoStack)
      Some(newBoard, deck, disc2)
    }
  }
  def redo(
      memento: Memento,
      deck: Deck,
      board: Board,
      disc: DiscardPile
  ): Option[(Board, Deck, DiscardPile)] = {
    val newBoard: Board = board.swapFromMem(memento.takenCard, memento.boardIndex)
    if (memento.fromDeck) {
      updtDeck = new Deck(med, deck.deck.tail, memento.takenCard.toString())
      setTrue()
      redoStack.clear()
      Some((newBoard, updtDeck, disc))
    } else {          // FROM DISCARDPILE
      val disc2: DiscardPile = disc.putToDiscardPile(memento.replacedCard)._1
      val updtDeck = disc.putToDiscardPile(memento.replacedCard)._2
      setTrue()
      redoStack.clear()
      Some((newBoard, deck, disc2))
    }
  }
  def setTrue(): Unit = memAct = true
  def setFalse(): Unit = memAct = false
  def checkMemAct(): Boolean = memAct
  def getNewDeck(): Deck = updtDeck
}
