package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.model.{
  BoardInterface,
  CardInterface,
  DiscardPileInterface,
  DeckInterface,
  GameState
}
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
    fromDeck: Int, // 0: fromDeck // 1: fromDisc // 2. Switch
    takenCard: CardInterface,
    boardIndex: Int,
    replacedCard: CardInterface,
    var lastDisc: DiscardPileInterface,
    replacedCardTurned: Boolean
) {
  override def toString(): String = {
    val s = (s"Card is Taken From ${
        if fromDeck == 0 then "Deck"
        else if fromDeck == 1 then "Disc"
        else "Switch"
      }\n")
    val s1 =
      s + (s"Taken Card: ${takenCard.trueCopy}; turned: ${takenCard.isTurned}\n")
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
    // if !undoStack.isEmpty then undoStack.pop()
    // println("saving...")
    undoStack.push(m)
    redoStack.clear()
    // println(undoStack)
  }

  def undo(
      memento: Memento,
      deck: DeckInterface,
      board: BoardInterface,
      disc: DiscardPileInterface
  ): Option[(BoardInterface, DeckInterface, DiscardPileInterface)] = {
    if undoStack.isEmpty then return None
    val memento = undoStack.pop()
    redoStack.push(memento) // Original direkt rüber — kein copy, kein Tauschen

    memento.fromDeck match {
      case 0 =>
        val newBoard =
          board.swapFromMem(memento.replacedCard, memento.boardIndex)
        val updtDeck = new Deck(
          deck.getDeckCards :+ memento.takenCard,
          memento.takenCard.getValue.toString
        )
        Some(newBoard, updtDeck, memento.lastDisc)

      case 1 =>
        // Disc→Board Undo: Boardkarte zurück auf Board, disc-Karte zurück auf Disc
        val newBoard =
          board.swapFromMem(memento.replacedCard, memento.boardIndex)
        val (disc2, updtDeck) =
          disc.putToDiscardPile(memento.takenCard.getValue.toString, ctrl)
        Some(newBoard, updtDeck, disc2)

      case _ => // fromDeck == 2: Deck→Disc + BoardTurn
        val cardToRestore =
          ctrl.toCard(memento.replacedCard, memento.replacedCardTurned)
        val newBoard = board.swapFromMem(cardToRestore, memento.boardIndex)
        val deck2 = new Deck(deck.getDeckCards :+ memento.takenCard)
        Some(newBoard, deck2, memento.lastDisc)
    }
  }

  def redo(
      memento: Memento,
      deck: DeckInterface,
      board: BoardInterface,
      disc: DiscardPileInterface
  ): Option[(BoardInterface, DeckInterface, DiscardPileInterface)] = {
    if redoStack.isEmpty then return None
    val memento = redoStack.pop()
    undoStack.push(memento) // Original zurück — kein copy

    memento.fromDeck match {
      case 0 =>
        val newBoard = board.swapFromMem(memento.takenCard, memento.boardIndex)
        val disc2 =
          new DiscardPile(memento.replacedCard.getValue.toString, true)
        Some(newBoard, deck, disc2)

      case 1 =>
        // Disc→Board Redo: disc-Karte auf Board, Boardkarte auf Disc
        val newBoard = board.swapFromMem(memento.takenCard, memento.boardIndex)
        val disc2 =
          disc.putToDiscardPile(memento.replacedCard.getValue.toString, ctrl)._1
        Some(newBoard, deck, disc2)

      case _ => // fromDeck == 2
        val newBoard =
          board.swapFromMem(memento.replacedCard.trueCopy, memento.boardIndex)
        val disc2 = new DiscardPile(memento.takenCard.getValue.toString, true)
        val deck2 = new Deck(deck.getDeckCards.dropRight(1))
        Some(newBoard, deck2, disc2)
    }
  }
  // FILEIO //
  // def toJson: JsObject = Json.obj(
  //   "undoStack" -> Json.toJson(undoStack.toSeq.map(_.toJson)),
  //   "redoStack" -> Json.toJson(redoStack.toSeq.map(_.toJson))
  //   )

  // XML //

  private def undoToXml: Node =
    if !undoStack.isEmpty then
      <undostack>
        <fromdeck>{undoStack(0).fromDeck}</fromdeck>
        <takenCard>{undoStack(0).takenCard.toXml}</takenCard>
        <boardIndex>{undoStack(0).boardIndex}</boardIndex>
        <replacedCard>{undoStack(0).replacedCard.toXml}</replacedCard>
        <lastDisc>{undoStack(0).lastDisc.toXml}</lastDisc>
        <replacedCardTurned>{
        undoStack(0).replacedCardTurned
      }</replacedCardTurned>
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
        <replacedCardTurned>{
        redoStack(0).replacedCardTurned
      }</replacedCardTurned>
      </redostack>
    else <redostack></redostack>

  private def xmlToMem(stackXml: NodeSeq): Option[Memento] = {
    val exists: Boolean = (stackXml \ "fromDeck").nonEmpty
    // println(f"exists: $exists")
    if exists then
      val fromD: Int = (stackXml \ "fromDeck").text.toInt
      val taken: CardInterface = ctrl.toCard(
        (stackXml \ "takenCard" \ "value"),
        (stackXml \ "takenCard" \ "turned").text.toBoolean
      )
      val idx = (stackXml \ "boardIndex").text.toInt
      val replaced: CardInterface = ctrl.toCard(
        (stackXml \ "replacedCard" \ "value"),
        (stackXml \ "replacedCard" \ "turned").text.toBoolean
      )
      val ldisc = { stackXml \ "lastDisc" }
      val discP = { ldisc \ "discpile" }.text
      val discT = { ldisc \ "turned" }.text.toBoolean
      val disc = DiscardPile(discP, discT)
      val replacedT: Boolean = (stackXml \ "replacedCardTurned").text.toBoolean
      Some(Memento(fromD, taken, idx, replaced, disc, replacedT))
    else None
  }

  def toXml: Node =
    <movecaretaker>
      {undoToXml}
      {redoToXml}
    </movecaretaker>

  def fromXml(d: Node): MoveCaretaker =
    val mcXml = { d \ "movecaretaker" }.head
    // println(f"mc: ${mcXml}")
    val undoXml = { mcXml \ "undostack" }.head
    // println(f"undo: ${undoXml}")
    val redoXml = { mcXml \ "redostack" }.head
    // println(f"redo: ${redoXml}")

    val tmpMem: Memento = Memento(
      1,
      ctrl.toCard(0),
      0,
      ctrl.toCard(0),
      DiscardPile("Disc", false),
      false
    )
    val u: Memento = xmlToMem(undoXml).getOrElse(tmpMem)
    // println("converted xml to undo Memeto")
    // println(f"u:\n${u.toString()}")
    val r: Memento = xmlToMem(redoXml).getOrElse(tmpMem)
    // println("converted xml to redo Memeto")
    // println(f"r:\n${r.toString()}")
    val tempMC = this
    tempMC.save(if u == tmpMem then r else u)
    if r != tmpMem then tempMC.redoStack.push(r)
    tempMC
}
