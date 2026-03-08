package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.model.{BoardInterface, CardInterface, DiscardPileInterface, DeckInterface, GameState}
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{DiscardPile, Deck}
import scala.collection.mutable.Stack
import de.htwg.se.skyjo.util.Mediator
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import scala.xml.{Node, NodeSeq}

import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.*

import play.api.libs.json.{Json, JsObject, Reads}
import play.api.libs.json.Json.JsValueWrapper

case class Memento(
    fromDeck: Boolean,
    takenCard: CardInterface,
    boardIndex: Int,
    replacedCard: CardInterface,
    var lastDisc: DiscardPileInterface,
    replacedCardTurned: Boolean
) {
  override def toString(): String = {
    val s = (s"Card is Taken From Deck: ${fromDeck}\n")
    val s1 =
      s + (s"Taken Deck Card: ${takenCard.trueCopy}; turned: ${takenCard.isTurned}\n")
    val s2 = s1 + (s"last Board Idx: ${boardIndex}\n")
    val s3 =
      s2 + (s"replacedCard: ${replacedCard.getValue.toString()}; turned: ${replacedCard.isTurned}\n")
    val s4 = s3 + (s"lastDisc: ${lastDisc}")
    s4
  }

  def toJson: JsObject = Json.obj(
    "fromDeck" -> fromDeck,
    "takenCard" -> takenCard.toJson,
    "boardIndex" -> boardIndex,
    "replacedCard" -> replacedCard.toJson,
    "lastDisc" -> lastDisc.toJson,
    "replacedCardTurned" -> replacedCardTurned
    )
}

class MoveCaretaker(val ctrl: ControllerInterface) {
  val undoStack = Stack[Memento]()
  val redoStack = Stack[Memento]()

  def save(m: Memento): Unit = {
    // println("clearing undoStack...")
    // undoStack.clear()
    undoStack.pop()
    // println("saving...")
    undoStack.push(m)
    // println(undoStack)
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
      val updtDeck = new Deck(tempV, memento.takenCard.toString())

      redoStack.push(memento)
      undoStack.clear()
      // undoStack.push(memento)
      undoStack.pop()
      // println(redoStack)

      Some(newBoard, deck, memento.lastDisc)
    } else {
      val disc2: DiscardPileInterface =
        disc.putToDiscardPile(memento.takenCard.toString(),ctrl)._1
      val updtDeck = disc.putToDiscardPile(memento.takenCard.toString(),ctrl)._2
      redoStack.push(memento)
      // undoStack.clear()
      undoStack.pop
      undoStack.push(memento)
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
      val tmpDisc = new DiscardPile(memento.replacedCard.toString())
      val tmpMemento = Memento(
        true,
        uptTaken,
        memento.boardIndex,
        uptReplaced,
        memento.lastDisc,
        memento.lastDisc.isTurned
      )
      undoStack.push(tmpMemento)
      // redoStack.clear()
      redoStack.pop()
      redoStack.push(tmpMemento)
      // println(undoStack)
      Some((newBoard, updtDeck, tmpDisc))
    } else {
      val disc2: DiscardPileInterface =
        disc.putToDiscardPile(memento.replacedCard.toString(),ctrl)._1
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
      // redoStack.clear()
      redoStack.pop()
      redoStack.push(tmpMemento)
      // println(undoStack)
      Some((newBoard, updtDeck, disc2))
    }
  }

  // FILEIO //
  // def toJson: JsObject = Json.obj(
  //   "undoStack" -> Json.toJson(undoStack.toSeq.map(_.toJson)),
  //   "redoStack" -> Json.toJson(redoStack.toSeq.map(_.toJson))
  //   )

  // XML //
  private def Node2Bool(ns: NodeSeq): Boolean =
    ns.head.text.replace(" ", "").toBoolean
  private def Node2Int(ns: NodeSeq): Int =
    ns.head.text.replace(" ", "").toInt

  private def undoToXml: Node =
    if !undoStack.isEmpty then
      <undostack>
        <fromdeck>{undoStack(0).fromDeck}</fromdeck>
        <takenCard>{undoStack(0).takenCard.toXml}</takenCard>
        <boardIndex>{undoStack(0).boardIndex}</boardIndex>
        <replacedCard>{undoStack(0).replacedCard.toXml}</replacedCard>
        <lastDisc>{undoStack(0).lastDisc.toXml}</lastDisc>
        <replacedCardTurned>{undoStack(0).replacedCardTurned}</replacedCardTurned>
      </undostack>
    else <undostack></undostack>

  private def redoToXml: Node =
    if !redoStack.isEmpty then
      <redostack>
        <fromdeck>{redoStack(0).fromDeck}</fromdeck>
        <takenCard>{redoStack(0).takenCard.toXml}</takenCard>
        <boardIndex>{redoStack(0).boardIndex}</boardIndex>
        <replacedCard>{redoStack(0).replacedCard.toXml}</replacedCard>
        <lastDisc>{redoStack(0).lastDisc.toXml}</lastDisc>
        <replacedCardTurned>{redoStack(0).replacedCardTurned}</replacedCardTurned>
      </redostack>
    else <redostack></redostack>

  private def xmlToMem(stackXml: NodeSeq): Memento = {
    val fromD: Boolean = Node2Bool(stackXml \ "fromDeck")
    val taken: CardInterface = ctrl.toCard((stackXml \ "takenCard" \ "value"), (stackXml \ "takenCard" \ "turned"))
    val idx = Node2Int(stackXml \ "boardIndex")
    val replaced: CardInterface = ctrl.toCard((stackXml \ "replacedCard" \ "value"), (stackXml \ "replacedCard" \ "turned"))
    val ldisc = {stackXml \ "lastDisc"}
    val discP = {ldisc \ "discpile"}.text
    val discT = {ldisc \ "turned"}.text.toBoolean
    val disc = DiscardPile(discP, discT)
    val replacedT: Boolean = Node2Bool(stackXml \ "replacedCardTurned")
    Memento(fromD, taken, idx, replaced, disc, replacedT)
  }

  def toXml: Node =
    <movecaretaker>
      undoToXml
      redoToXml
    </movecaretaker>
  def fromXml(d: Node): MoveCaretaker =
    val mcXml = {d \ "movecaretaker"}
    val undoXml = {mcXml \ "undostack"}
    val redoXml = {mcXml \ "redostack"}

    val u = xmlToMem(undoXml)
    val r = xmlToMem(redoXml)
    val tempMC = this
    tempMC.save(u)
    tempMC.redoStack.push(r)
    tempMC
    
}
