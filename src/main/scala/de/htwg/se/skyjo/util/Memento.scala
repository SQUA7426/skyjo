package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.model.{BoardInterface, CardInterface, DiscardPileInterface, DeckInterface, GameState}
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{DiscardPile, Deck}
import scala.collection.mutable.Stack
import de.htwg.se.skyjo.util.Mediator
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface

// case class Memento(
//     fromDeck: Boolean,
//     takenCard: CardInterface,
//     boardIndex: Int,
//     replacedCard: CardInterface,
//     lastDisc: DiscardPileInterface,
//     replacedCardTurned: Boolean,
// ) {
//   override def toString(): String = {
//     val s = (s"Card is Taken From Deck: ${fromDeck}\n")
//     val s1 =
//       s + (s"Taken Deck Card: ${takenCard}; turned: ${takenCard.isTurned}\n")
//     val s2 = s1 + (s"last Board Idx: ${boardIndex}\n")
//     val s3 =
//       s2 + (s"replacedCard: ${replacedCard.toString()}; turned: ${replacedCard.isTurned}\n")
//     val s4 = s3 + (s"lastDisc: ${lastDisc}")
//     s4
//   }
// }
//
// // class MoveCaretaker(val med: Mediator) {
// class MoveCaretaker(val ctrl: ControllerInterface) {
//   val undoStack = Stack[Memento]()
//   val redoStack = Stack[Memento]()
//
//   def save(m: Memento): Unit = {
//     println("clearing undoStack...")
//     undoStack.clear()
//     println("saving...")
//     undoStack.push(m)
//     println(undoStack)
//   }
//
//   def undo(
//       memento: Memento,
//       deck: DeckInterface,
//       board: BoardInterface,
//       disc: DiscardPileInterface
//   ): Option[(BoardInterface, DeckInterface, DiscardPileInterface)] = {
//     val newBoard: BoardInterface =
//       board.swapFromMem(memento.replacedCard, memento.boardIndex)
//     if (memento.fromDeck) {
//       val tempV: Vector[CardInterface] = memento.takenCard +: deck.getDeck
//       // updtDeck = new DeckInterface(med, tempV, memento.takenCard.toString())
//       redoStack.push(memento)
//       // setTrue()
//       undoStack.clear()
//       undoStack.push(memento)
//
//       Some(newBoard, deck, memento.lastDisc)
//     } else { // DiscardPileInterface
//       val disc2: DiscardPileInterface =
//         disc.putToDiscardPile(memento.takenCard.toString())._1
//       val updtDeck = disc.putToDiscardPile(memento.takenCard.toString())._2
//       redoStack.push(memento)
//       // setTrue()
//       undoStack.clear()
//       undoStack.push(memento)
//       // println("undoStack")
//       // println(undoStack)
//       // println("redoStack")
//       // println(redoStack)
//       Some(newBoard, updtDeck, disc2)
//     }
//   }
//   def redo(
//       memento: Memento,
//       deck: DeckInterface,
//       board: BoardInterface,
//       disc: DiscardPileInterface
//   ): Option[(BoardInterface, DeckInterface, DiscardPileInterface)] = {
//     val newBoard: BoardInterface = board.swapFromMem(
//       if memento.fromDeck then memento.takenCard else memento.replacedCard,
//       memento.boardIndex
//     )
//     if (memento.fromDeck) {
//       val updtDeck = deck
//       val (uptTaken, uptReplaced) = (memento.replacedCard, memento.takenCard)
//       val tmpDisc = new DiscardPile(ctrl, memento.replacedCard.toString())
//       val tmpMemento = Memento(
//         true,
//         uptTaken,
//         memento.boardIndex,
//         uptReplaced,
//         memento.lastDisc,
//         memento.lastDisc.isTurned
//       )
//       undoStack.push(tmpMemento)
//       // setTrue()
//       redoStack.clear()
//       Some((newBoard, updtDeck, tmpDisc))
//     } else { // FROM DISCARDPILE
//       val disc2: DiscardPileInterface =
//         disc.putToDiscardPile(memento.replacedCard.toString())._1
//       val updtDeck = deck
//       val (uptTaken, uptReplaced) = (memento.replacedCard, memento.takenCard)
//       val tmpMemento = Memento(
//         false,
//         uptTaken,
//         memento.boardIndex,
//         uptReplaced,
//         memento.lastDisc,
//         memento.lastDisc.isTurned
//       )
//       undoStack.push(tmpMemento)
//       // setTrue()
//       redoStack.clear()
//       Some((newBoard, updtDeck, disc2))
//     }
//   }
// }
// case class Memento(state: GameState)
//
// class MoveCaretaker {
//   private val undoStack = Stack[GameState]()
//   private val redoStack = Stack[GameState]()
//
//   def save(state: GameState): Unit = {
//     undoStack.push(state)
//     redoStack.clear()
//   }
//
//   def undo(currentState: GameState): Option[GameState] = {
//     if (undoStack.nonEmpty) {
//       redoStack.push(currentState)
//       Some(undoStack.pop())
//     } else None
//   }
//
//   def redo(currentState: GameState): Option[GameState] = {
//     if (redoStack.nonEmpty) {
//       undoStack.push(currentState)
//       Some(redoStack.pop())
//     } else None
//   }
// }

case class Memento(
    fromDeck: Boolean, // true = Deck, false = DiscardPile
    takenCard: CardInterface,
    boardIndex: Int,
    replacedCard: CardInterface,
    var lastDisc: DiscardPileInterface,
    replacedCardTurned: Boolean
) {
  override def toString(): String = {
    val s = (s"Card is Taken From Deck: ${fromDeck}\n")
    val s1 =
      s + (s"Taken Deck Card: ${takenCard}; turned: ${takenCard.isTurned}\n")
    val s2 = s1 + (s"last Board Idx: ${boardIndex}\n")
    val s3 =
      s2 + (s"replacedCard: ${replacedCard.toString()}; turned: ${replacedCard.isTurned}\n")
    val s4 = s3 + (s"lastDisc: ${lastDisc}")
    s4
  }
}

// class MoveCaretaker(val med: Mediator) {
class MoveCaretaker(val ctrl: ControllerInterface) {
  val undoStack = Stack[Memento]()
  val redoStack = Stack[Memento]()
  // var updtDeck: Deck = Deck(med)
  // var memAct: Boolean = false

  def save(m: Memento): Unit = {
    println("clearing undoStack...")
    undoStack.clear()
    println("saving...")
    undoStack.push(m)
    println(undoStack)
  }

  def undo(
      memento: Memento,
      deck: DeckInterface,
      board: BoardInterface,
      disc: DiscardPileInterface
  ): Option[(BoardInterface, DeckInterface, DiscardPileInterface)] = {
    val newBoard: BoardInterface =
      board.swapFromMem(memento.replacedCard, memento.boardIndex)
    if (memento.fromDeck) {
      val tempV: Vector[CardInterface] = memento.takenCard +: deck.getDeckCards
      val updtDeck = new Deck(tempV, ctrl, memento.takenCard.toString())
      // val tmpDisc = new DiscardPile(med, memento.replacedCard.toString())
      // val tmpMem = Memento(memento.fromDeck, memento.replacedCard, memento)
      redoStack.push(memento)
      // setTrue()
      undoStack.clear()
      undoStack.push(memento)

      // println("undoStack")
      // println(undoStack)
      // println("redoStack")
      // println(redoStack)
      Some(newBoard, deck, memento.lastDisc)
    } else { // DiscardPile
      val disc2: DiscardPileInterface =
        disc.putToDiscardPile(memento.takenCard.toString())._1
      val updtDeck = disc.putToDiscardPile(memento.takenCard.toString())._2
      redoStack.push(memento)
      // setTrue()
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
      deck: DeckInterface,
      board: BoardInterface,
      disc: DiscardPileInterface
  ): Option[(BoardInterface, DeckInterface, DiscardPileInterface)] = {
    val newBoard: BoardInterface = board.swapFromMem(
      if memento.fromDeck then memento.takenCard else memento.replacedCard,
      memento.boardIndex
    )
    if (memento.fromDeck) {
      val updtDeck = deck
      val (uptTaken, uptReplaced) = (memento.replacedCard, memento.takenCard)
      val tmpDisc = new DiscardPile(ctrl, memento.replacedCard.toString())
      val tmpMemento = Memento(
        true,
        uptTaken,
        memento.boardIndex,
        uptReplaced,
        memento.lastDisc,
        memento.lastDisc.isTurned
      )
      undoStack.push(tmpMemento)
      // setTrue()
      redoStack.clear()
      Some((newBoard, updtDeck, tmpDisc))
    } else { // FROM DISCARDPILE
      val disc2: DiscardPileInterface =
        disc.putToDiscardPile(memento.replacedCard.toString())._1
      val updtDeck = deck
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
      // setTrue()
      redoStack.clear()
      Some((newBoard, updtDeck, disc2))
    }
  }
  // def setTrue(): Unit = memAct = true
  // def setFalse(): Unit = memAct = false
  // def checkMemAct(): Boolean = memAct
  // def getNewDeck(): Deck = updtDeck
}
