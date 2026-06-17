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
    val s = (s"Card is Taken From ${if fromDeck == 0 then "Deck" else if fromDeck == 1 then "Disc" else  "Switch"}\n")
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
    // undoStack.clear()
    if !undoStack.isEmpty then undoStack.pop()
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
    if (memento.fromDeck == 0) {
      println("Memento fromDeck");

      val newBoard: BoardInterface =
        board.swapFromMem(memento.replacedCard, memento.boardIndex)
      val tempV: Vector[CardInterface] = memento.takenCard +: deck.getDeckCards
      val updtDeck = new Deck(tempV, memento.takenCard.toString())

      redoStack.push(memento)
      if !undoStack.isEmpty then undoStack.pop()
      // println(redoStack)

      Some(newBoard, deck, memento.lastDisc)
    } else if (memento.fromDeck == 1) { // FromDisc
      println("Memento fromDisc");

      val newBoard: BoardInterface =
        board.swapFromMem(memento.takenCard, memento.boardIndex)

      // println(s"New Board:\n${newBoard}\n");

      val (disc2, updtDeck) =
        disc.putToDiscardPile(memento.replacedCard.toString(), ctrl)

      val redo_disc = disc.putToDiscardPile(memento.takenCard.toString, ctrl)._1
      // println(s"redo_disc:${redo_disc}");

      redoStack.push(memento.copy(takenCard = memento.replacedCard, replacedCard = memento.takenCard, lastDisc = redo_disc))
      if !undoStack.isEmpty then undoStack.pop()

      // println(redoStack)
      Some(newBoard, updtDeck, disc2)
    } else {
      println("Memento switch");

      println(memento);

      val newBoard = board.swapFromMem(ctrl.toCard(memento.replacedCard, !memento.replacedCard.isTurned),memento.boardIndex);

      // println(s"New Board:\n${newBoard}\n");

      val deck2 = new Deck(deck.getDeckCards :+ memento.takenCard);
      val disc2 = memento.lastDisc;

      // println(s"deck2: ${deck2.getCard} ; disc: ${disc2}");

      val redo_disc = new DiscardPile(memento.replacedCard.toString(), true);

      redoStack.push(memento.copy(
        2,
        memento.takenCard,
        memento.boardIndex,
        memento.replacedCard,
        redo_disc,
        ))

      // println(s"\nMemento switch redoStack:");
      // println(redoStack);

      Some(newBoard, deck2, disc2)
    }
  }
  def redo(
      memento: Memento,
      deck: DeckInterface,
      board: BoardInterface,
      disc: DiscardPileInterface
  ): Option[(BoardInterface, DeckInterface, DiscardPileInterface)] = {

    if (memento.fromDeck == 0) {
      val newBoard: BoardInterface =
        board.swapFromMem(memento.takenCard, memento.boardIndex);

      val updtDeck = deck
      val (uptTaken, uptReplaced) = (memento.replacedCard, memento.takenCard)
      val tmpDisc = new DiscardPile(memento.replacedCard.toString())
      val tmpMemento = Memento(
        0,
        uptTaken,
        memento.boardIndex,
        uptReplaced,
        memento.lastDisc,
        memento.lastDisc.isTurned
      )
      undoStack.push(tmpMemento)
      // if !redoStack.isEmpty then redoStack.pop()
      redoStack.push(tmpMemento)
      // println(undoStack)

      Some((newBoard, updtDeck, tmpDisc))

    } else if (memento.fromDeck == 1) {

      val newBoard: BoardInterface = board.swapFromMem(
        ctrl.toCard(memento.replacedCard),
        memento.boardIndex
      );

      // println(s"New Board:\n${newBoard}\n");

      val disc2: DiscardPileInterface =
        disc.putToDiscardPile(memento.takenCard.toString(), ctrl)._1

      // println(s"in redo fromDisc - takenCard: ${memento.takenCard}");

      val updtDeck = deck
      val tmpMemento = Memento(
        1,
        ctrl.toCard(disc),
        memento.boardIndex,
        memento.replacedCard,
        memento.lastDisc,
        memento.lastDisc.isTurned
      )
      undoStack.push(tmpMemento)

      if !redoStack.isEmpty then redoStack.pop()
      redoStack.push(tmpMemento)

      // println(undoStack)
      Some((newBoard, updtDeck, disc2))
    } else {
      println("Memento Redo:");
      println(memento);

      val newBoard = board.swapFromMem(memento.replacedCard, memento.boardIndex);
      println(s"New Board:\n${newBoard}\n");

      val (disc2, deck2) = memento.lastDisc.putToDiscardPile(memento.takenCard, ctrl);

      println(s"deck2: ${deck2.getCard} ; disc: ${disc2}");

      val tmpMemento = Memento(
        2,
        memento.replacedCard,
        memento.boardIndex,
        memento.takenCard,
        memento.lastDisc,
        memento.lastDisc.isTurned
      )
      undoStack.push(tmpMemento)
      if !redoStack.isEmpty then redoStack.pop()
      redoStack.push(tmpMemento)

      Some((newBoard, deck2, disc2))
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
        (stackXml \ "takenCard" \ "turned")
      )
      val idx = (stackXml \ "boardIndex").text.toInt
      val replaced: CardInterface = ctrl.toCard(
        (stackXml \ "replacedCard" \ "value"),
        (stackXml \ "replacedCard" \ "turned")
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
