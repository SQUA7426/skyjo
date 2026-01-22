package de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation

import de.htwg.se.skyjo.model.{
  BoardInterface,
  CardInterface,
  DiscardPileInterface,
  DeckInterface,
  State,
  GameState
}
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{Deck, Card, Board}

import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent._
import de.htwg.se.skyjo.util.{Observable, Memento, MoveCaretaker}
import de.htwg.se.skyjo.util.*

import scala.io.StdIn.{readInt, readLine}
import scala.util.Random

import jakarta.inject.Inject
import scala.util.{Try, Success, Failure}

class Controller @Inject() (var state: GameState)
    extends Observable
    with ControllerInterface {

  var mem: Memento = _

  def setup(): Unit =
    var currentDeck = state.deck

    for (i <- 0 until state.boards.size) {
      val (x, y) = state.boards(i).getSize
      val tmpMed = this.getMediator

      val (afterBoard, nextDeck) = fillBoard(x, y, currentDeck)
      currentDeck = nextDeck

      val initSize = x * y
      val arr = Random.shuffle((0 until initSize).toList)
      val finalBoard = afterBoard.turnBoardCard(arr(0)).turnBoardCard(arr(1))
      state = state.copy(
        boards = state.boards.updated(i, finalBoard),
        deck = currentDeck
      )
    }
    state = state.copy(plIdx = 0)
    notifyObservers

  def save(mementoSave: Memento): Unit =
    getMementos(getPlIdx).save(mementoSave)

  def undo(): Unit =
    getMementos(state.plIdx).undo(
      mem,
      getDeck,
      getBrds(getPlIdx),
      getDisc
    ) match {
      case Some(memBoard, memDeck, memDisc) => {
        state = state.copy(
          boards = getBrds.updated(getPlIdx, memBoard),
          deck = memDeck,
          disc = memDisc
        )
        state.currentState.pre =
          if getMementos(getPlIdx).undoStack(getPlIdx)._1 then "DECK"
          else "DISC"
      }
      case None => { println("Couln't UNDO") }
    }
    notifyObservers

  def redo(): Unit =
    getMementos(state.plIdx).redo(
      mem,
      getDeck,
      getBrds(getPlIdx),
      getDisc
    ) match {
      case Some(memBoard, memDeck, memDisc) => {
        state = state.copy(
          boards = getBrds.updated(getPlIdx, memBoard),
          deck = memDeck,
          disc = memDisc
        )
        state.currentState.pre =
          if getMementos(getPlIdx).redoStack(getPlIdx)._1 then "DECK"
          else "DISC"
      }
      case None => { println("Couln't REDO") }
    }
    notifyObservers

  def drawFromDeck(): Unit = {
    val (card, newDeck) = state.deck.draw()
    
    mem = Memento(true, card, 0, card, getDisc, card.isTurned)
    getMementos(getPlIdx).save(mem)

    
    val newState = state.copy(deck = newDeck)
    assertGameState(newState)
  }

  def drawFromDisc(): Unit = {
    getDiscCard() match {
      case Some(card) => {
        mem = new Memento(
          false,
          getDeck.getCard.get,
          0,
          card,
          getDisc,
          card.isTurned
        )
        val newDisc = remove()
        getMementos(getPlIdx).save(mem)
        notifyObservers
      }
      case None => printf("DISC EMPTY")
    }
  }

  def fillBoard(
      xSize: Int,
      ySize: Int,
      d: DeckInterface
  ): (BoardInterface, DeckInterface) = {

    if (d.getDeckCards.isEmpty) {
      val newFullDeck = Deck(this)
      return fillBoard(xSize, ySize, newFullDeck)
    }

    def drawOne(currentDeck: DeckInterface): (CardInterface, DeckInterface) = {
      val (card, nextDeck) = currentDeck.draw()
      (card.falseCopy, nextDeck)
    }

    def fillRows(
        currentDeck: DeckInterface,
        rowsLeft: Int
    ): (Vector[Vector[CardInterface]], DeckInterface) = {
      if (rowsLeft == 0) (Vector.empty, currentDeck)
      else {
        val (row, deckAfterRow) = fillRow(currentDeck, xSize)
        val (remainingRows, finalDeck) = fillRows(deckAfterRow, rowsLeft - 1)
        (row +: remainingRows, finalDeck)
      }
    }

    def fillRow(
        currentDeck: DeckInterface,
        cardsLeft: Int
    ): (Vector[CardInterface], DeckInterface) = {
      if (cardsLeft == 0) (Vector.empty, currentDeck)
      else {
        val (card, nextDeck) = drawOne(currentDeck)
        val (restOfRow, deckAfterRest) = fillRow(nextDeck, cardsLeft - 1)
        (card +: restOfRow, deckAfterRest)
      }
    }
    val (finalGrid, remainingDeck) = fillRows(d, ySize)
    (
      new Board(getMediator, xSize, ySize, finalGrid),
      remainingDeck
    )
  }
  def getMediator: Mediator = this.getGameState.med

  def toCard(x: Any): CardInterface = {
    val valRange = (-2 to 12).toSet

    x match {
      case c: CardInterface => c

      case a: Int =>
        if (valRange.contains(a)) Card(a, true, this)
        else Card(0, false, this) 

      case b: String =>
        val tryInt = scala.util.Try(b.toInt)
        if (tryInt.isSuccess) {
          Card(tryInt.get, true, this)
        } else {
          Card(0, false, this)
        }
      case scala.util.Success(value) => toCard(value)
      case scala.util.Failure(_)     => Card(0, false, this)

      case Some(value) => toCard(value)
      case None        => Card(0, false, this)

      case d: DeckInterface =>
        d.getCard.map(toCard).getOrElse(Card(0, false, this))

      case disc: DiscardPileInterface =>
        disc.getDiscCard().map(toCard).getOrElse(Card(0, false, this))

      case _ => Card(0, false, this)
    }
  }
  def isCard(c: Any): Boolean = c match {
    case _: CardInterface => true
    case _                => false
  }

  def fullDeck(): Vector[CardInterface] = {
    val seqCards = Seq.empty[CardInterface]
    val v1: Vector[CardInterface] =
      (for { i <- 1 to 10; j <- -1 to 12 } yield toCard(j)).toVector
    val v2: Vector[CardInterface] = (for {
      i <- 1 to 5; j <- -2 to 0; if j == -2 || j == 0
    } yield toCard(j)).toVector
    val fullDeck: Vector[CardInterface] = v1 ++ v2
    val diffs: Vector[CardInterface] = fullDeck.diff(seqCards)
    val shuffled = Random.shuffle(diffs)
    shuffled
  }
  
  def getDrawn: Option[CardInterface] =
    currMemento.undoStack
      .lift(getPlIdx)
      .map(_._2)
  def hasDrawn: Boolean =
    currMemento.undoStack
      .lift(getPlIdx)
      .exists(_._2.isVal)
  def draw(): (
      CardInterface,
      DeckInterface
  ) = state.deck.draw()
  
  def getDeckCards: Vector[CardInterface] = state.deck.getDeckCards
  def getDiscCard(): Option[CardInterface] =
    state.disc.getDiscCard()
  
  def putToDiscardPile(from: Any): (
      DiscardPileInterface,
      DeckInterface
  ) = state.disc.putToDiscardPile(from)
  def remove(amount: Int): Vector[CardInterface] =
    state.deck.remove(amount)
  def remove(): DiscardPileInterface =
    state.disc.remove()
  
  def turnUpperCard: String = state.deck.turnUpperCard

  def getBoard: Vector[Vector[CardInterface]] =
    state.boards(state.plIdx).getBoard
  def getGameState: GameState = state

  def reduce(row: Int, col: Int): (BoardInterface, Boolean) =
    state.boards(state.plIdx).reduce(row, col)

  def getPlIdx: Int = state.plIdx
  
  def currState: State = state.currentState

  def getMementos: Vector[MoveCaretaker] = state.mementos

  def assertGameState(newState: GameState): Unit = {
    state = newState
    notifyObservers
  }

  def copy(
      med: Mediator = getMediator,
      mems: Vector[MoveCaretaker] = getMementos,
      brds: Vector[BoardInterface] = getBrds,
      d: DeckInterface = getDeck,
      disc: DiscardPileInterface = getDisc,
      idx: Int = getPlIdx,
      anotherState: State =
        currState 
  ): GameState = {
    state.copy(
      med = med,
      mementos = mems,
      boards = brds,
      deck = d,
      disc = disc,
      plIdx = idx,
      currentState =
        anotherState 
    )
  }

  def getBrds: Vector[BoardInterface] = state.boards
  def getDeck: DeckInterface = state.deck
  def getDisc: DiscardPileInterface = state.disc
  def getSize: (Int, Int) = state.boards(state.plIdx).getSize
  def currMemento: MoveCaretaker = getMementos(state.plIdx)

  def swapFromMem(c: CardInterface, pos: Int): BoardInterface =
    val b: BoardInterface = getBrds(state.plIdx).swapFromMem(c, pos)
    state = state.copy(boards = state.boards.updated(getPlIdx, b))
    notifyObservers
    b

}
