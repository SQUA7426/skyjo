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
    val s = (s"Card is Taken From Deck: ${fromDeck}\n")
    val s1 =
      s + (s"Taken Deck Card: ${takenCard}; turned: ${takenCard.isTurned()}\n")
    val s2 = s1 + (s"last Board Idx: ${boardIndex}\n")
    val s3 =
      s2 + (s"replacedCard: ${replacedCard.toString()}; turned: ${replacedCard.isTurned()}\n")
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
  }

  def undo(
      memento: Memento,
      deck: Deck,
      board: Board,
      disc: DiscardPile
  ): Option[(Board, Deck, DiscardPile)] = {
    val newBoard: Board =
      board.swapFromMem(memento.replacedCard, memento.boardIndex)
    if (memento.fromDeck) {
      val tempV: Vector[Card] = memento.takenCard +: deck.deck
      updtDeck = new Deck(med, tempV, memento.takenCard.toString())
      // val tmpDisc = new DiscardPile(med, memento.replacedCard.toString())
      // val tmpMem = Memento(memento.fromDeck, memento.replacedCard, memento)
      redoStack.push(memento)
      setTrue()
      undoStack.clear()
      undoStack.push(memento)

      // println("undoStack")
      // println(undoStack)
      // println("redoStack")
      // println(redoStack)
      Some(newBoard, deck, memento.lastDisc)
    } else { // DiscardPile
      val disc2: DiscardPile =
        disc.putToDiscardPile(memento.takenCard.toString())._1
      updtDeck = disc.putToDiscardPile(memento.takenCard.toString())._2
      redoStack.push(memento)
      setTrue()
      undoStack.clear()
      undoStack.push(memento)
      // println("undoStack")
      // println(undoStack)
      // println("redoStack")
      // println(redoStack)
      Some(newBoard, updtDeck, disc2)
    }
  }
  def redo(
      memento: Memento,
      deck: Deck,
      board: Board,
      disc: DiscardPile
  ): Option[(Board, Deck, DiscardPile)] = {
    val newBoard: Board = board.swapFromMem(
      if memento.fromDeck then memento.takenCard else memento.replacedCard,
      memento.boardIndex
    )
    if (memento.fromDeck) {
      updtDeck = deck
      val (uptTaken, uptReplaced) = (memento.replacedCard, memento.takenCard)
      val tmpDisc = new DiscardPile(med, memento.replacedCard.toString())
      val tmpMemento = Memento(
        true,
        uptTaken,
        memento.boardIndex,
        uptReplaced,
        memento.lastDisc,
        memento.lastDisc.isTurned
      )
      undoStack.push(tmpMemento)
      setTrue()
      redoStack.clear()
      Some((newBoard, updtDeck, tmpDisc))
    } else { // FROM DISCARDPILE
      val disc2: DiscardPile =
        disc.putToDiscardPile(memento.replacedCard.toString())._1
      updtDeck = deck
      val (uptTaken, uptReplaced) = (memento.replacedCard, memento.takenCard)
      val tmpMemento = Memento(
        false,
        uptTaken,
        memento.boardIndex,
        uptReplaced,
        memento.lastDisc,
        memento.lastDisc.isTurned
      )
      undoStack.push(tmpMemento)
      setTrue()
      redoStack.clear()
      Some((newBoard, updtDeck, disc2))
    }
  }
  def setTrue(): Unit = memAct = true
  def setFalse(): Unit = memAct = false
  def checkMemAct(): Boolean = memAct
  def getNewDeck(): Deck = updtDeck
}
