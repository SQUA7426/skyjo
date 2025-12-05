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
    println("clearing undoStack...")
    undoStack.clear()
    println("saving...")
    undoStack.push(m)
    undoStack.foreach(println)
    println()
    redoStack.clear()
  }

  def undo(
      memento: Memento,
      deck: Deck,
      board: Board,
      disc: DiscardPile
  ): (Board, Deck, DiscardPile) = {
    val b = board.swapFromMem(memento.replacedCard, memento.boardIndex)
    if (memento.fromDeck) {
      val tempV: Vector[Card] = memento.takenCard +: deck.deck;
      updtDeck = new Deck(med, tempV, memento.takenCard.toString())
      println(memento.toString())
      println(b.toString())
      setTrue()
      (b, deck, memento.lastDisc)
    } else {
      disc.putToDiscardPile(memento.takenCard)
      redoStack.push(memento)
      memento.lastDisc = DiscardPile(med, disc.putToDiscardPile(memento.takenCard).toString())
      println(memento.toString())
      setTrue()
      memento.lastDisc.isTurned = disc.isTurned
      (board, deck, memento.lastDisc)
    }
  }

  def redo(memento: Memento, deck: Deck, board: Board, disc: DiscardPile) = {
    if (memento.fromDeck) {
      updtDeck = new Deck(med, deck.deck.tail, memento.takenCard.toString())
      board.switch(memento.takenCard, memento.boardIndex)
      setTrue()
    } else {
      disc.putToDiscardPile(memento.replacedCard)
      board.switch(memento.takenCard, memento.boardIndex)
      setTrue()
    }
    (board, deck, disc)
  }
  def setTrue(): Unit = memAct = true
  def setFalse(): Unit = memAct = false
  def checkMemAct(): Boolean = memAct
  def getNewDeck(): Deck = updtDeck
}
