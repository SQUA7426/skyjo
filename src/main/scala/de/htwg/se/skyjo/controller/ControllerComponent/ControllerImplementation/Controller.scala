package de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation

import de.htwg.se.skyjo.model.DeckInterface
import de.htwg.se.skyjo.model.DeckImplementation.*
import de.htwg.se.skyjo.model.DiscardPileImplementation.DiscardPile
import de.htwg.se.skyjo.model.CardImplementation.*
import de.htwg.se.skyjo.model.CardInterface
import de.htwg.se.skyjo.model.BoardInterface
import de.htwg.se.skyjo.model.BoardImplementation.*
import de.htwg.se.skyjo.model.GameState
import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent._
import de.htwg.se.skyjo.util.{Observable, Memento, MoveCaretaker}
import de.htwg.se.skyjo.util.*

import scala.io.StdIn.{readInt, readLine}
import scala.util.Random
import de.htwg.se.skyjo.model.State
import de.htwg.se.skyjo.model.DiscardPileInterface

class Controller(var state: GameState) extends Observable with ControllerInterface {
  private val caretaker = new MoveCaretaker()

  def setup(): Unit = {
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
    state = state.copy(playerIdx = 0)
    notifyObservers
  }

  def executeMove(moveLogic: => GameState): Unit = {
    caretaker.save(state)
    state = moveLogic
    notifyObservers
  }

  def save(saveState: GameState):Unit = caretaker.save(saveState)

  def undo(): Unit = {
    caretaker.undo(state) match {
      case Some(oldState) =>
        state = oldState
        notifyObservers
      case None => println("Nothing to undo!")
    }
  }
  def redo(): Unit = {
    caretaker.redo(state) match {
      case Some(oldState) =>
        state = oldState
        notifyObservers
      case None => println("Nothing to undo!")
    }
  }

  def turnBoardCard(index: Int): Unit = {
    val board = state.boards(state.playerIdx)

    val cardToFlip = board.getBoardCard(index)

    if (cardToFlip.isTurned) {
      println(
        s"Card at index $index is already face up! Choose a hidden card (#)."
      )
    } else {
      caretaker.save(state)
      val newBoard = board.turnBoardCard(index)

      state = state.copy(
        boards = state.boards.updated(state.playerIdx, newBoard),
        isFlippingPhase = false,
        playerIdx = (state.playerIdx + 1) % state.boards.size
      )
      notifyObservers
    }
  }

  def putCardOnBoard(pos: Int): Unit = {
    executeMove {
      val board = state.boards(state.playerIdx)
      val newBoard = board.turnBoardCard(pos)

      state.copy(
        boards = state.boards.updated(state.playerIdx, newBoard),
        isFlippingPhase = false,
        playerIdx = (state.playerIdx + 1) % state.boards.size
      )
    }
    notifyObservers
  }
  def getGameState: GameState = state

  def getBrds = state.boards
  def getADeck = state.deck
  def getDisc = state.disc
  def getPldx: Int = state.playerIdx
  def getdrawn: Option[CardInterface] = {
    state.drawnCard match {
    case Some(ci) => Some(ci)
    case None => None
    }
  }
  def getPhase: Boolean = state.isFlippingPhase
  def currState: State = state.currentState

  def copy(med: Mediator = getMediator, brds: Vector[BoardInterface] = getBrds, d: DeckInterface = getADeck, disc: DiscardPileInterface = getDisc, idx: Int = getPldx, drawnCard: Option[CardInterface] = getdrawn, flippedPhase: Boolean = getPhase, state: State = currState): GameState = {
    GameState(med, brds,d,disc,idx,drawnCard,flippedPhase, state)
  }

  def assertGameState(newState: GameState): Unit = {
    state = newState
  }

  def uptGameState(newState: GameState): Unit = {
    state = newState
    notifyObservers
  }

  def drawFromDeck(): Unit = {
    val (card, newDeck) = state.deck.draw()

    state = state.copy(
      deck = newDeck,
      drawnCard = Some(card)
    )
    notifyObservers
  }

  def drawFromDisc(): Unit = {
    state.disc.getDiscCard() match {
      case Some(card) => {
        caretaker.save(state)
        val newDisc = state.disc.remove()

        state = state.copy(
          disc = newDisc,
          drawnCard = Some(card)
        )
        notifyObservers
      }
      case None => println("DiscardPile cannot be accessed!")
    }

  }

  def replaceCard(pos: Int): Unit = {
    state.drawnCard match {
      case Some(card) =>
        caretaker.save(state)

        val currentBoard = state.boards(state.playerIdx).asInstanceOf[Board]
        val (oldCard, nextBoard) = currentBoard.switch(card, pos)

        val nextDisc =
          state.disc
            .putToDiscardPile(oldCard)
            ._1

        state = state.copy(
          boards = state.boards.updated(state.playerIdx, nextBoard),
          disc = nextDisc,
          drawnCard = None,
          playerIdx = (state.playerIdx + 1) % state.boards.size
        )
        notifyObservers
      case None => println("You are not holding a card!")
    }
  }
  def SwapHandler(index: Int): Unit = {
    caretaker.save(state)
    val board = state.boards(state.playerIdx)
    val newBoard = board.turnBoardCard(index)

    state = state.copy(
      boards = state.boards.updated(state.playerIdx, newBoard),
      isFlippingPhase = false,
      playerIdx = (state.playerIdx + 1) % state.boards.size
    )
    notifyObservers
  }

  def discardDrawnCard(): Unit = {
    state.drawnCard.foreach { card =>
      caretaker.save(state)
      val (newDisc, _) = state.disc.putToDiscardPile(card)
      state = state.copy(
        drawnCard = None,
        disc = newDisc,
        isFlippingPhase = true
      )
      notifyObservers
    }
  }

  def fillBoard(
      xSize: Int,
      ySize: Int,
      d: DeckInterface
  ): (BoardInterface, DeckInterface) = {

    if (d.getDeck.isEmpty) {
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
    val val1d =
      (for { j <- -2 to 12 } yield j.toString()).toVector
    x match {
      case a: Int => Card(a.toInt, true, this)
      case b: String if val1d.contains(b) =>
        Card(Integer.parseInt(b), true, this)
      case d: Deck => Card(Integer.parseInt(d.toString()), true, this)
      case disc: DiscardPile => Card(Integer.parseInt(disc.toString()), this)
      case other =>
        throw new IllegalArgumentException(s"Invalid input:$other with type: ${other.getClass}")
    }
  }

  def isCard(c: Any): Boolean = c match {
    case _: Card => true
    case _       => false
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

  // NOT IMPL //
  def draw(): (
      de.htwg.se.skyjo.model.CardInterface,
      de.htwg.se.skyjo.model.DeckInterface
  ) = state.deck.draw()
  // def falseCopy: de.htwg.se.skyjo.model.CardInterface
  // def trueCopy: de.htwg.se.skyjo.model.CardInterface

  // def getCard: de.htwg.se.skyjo.model.CardInterface
  def getDeck: Vector[de.htwg.se.skyjo.model.CardInterface] = state.deck.getDeck
  def getDiscCard(): Option[de.htwg.se.skyjo.model.CardInterface] =
    state.disc.getDiscCard()
  // def isTurned: Boolean
  def putToDiscardPile(from: Any): (
      de.htwg.se.skyjo.model.DiscardPileInterface,
      de.htwg.se.skyjo.model.DeckInterface
  ) = state.disc.putToDiscardPile(from)
  def remove(amount: Int): Vector[de.htwg.se.skyjo.model.CardInterface] =
    state.deck.remove(amount)
  def remove(): de.htwg.se.skyjo.model.DiscardPileInterface =
    state.disc.remove()
  // def turn: Unit
  def turnUpperCard: String = state.deck.turnUpperCard

  def getBoard: Vector[Vector[CardInterface]] = getGameState.boards(getGameState.playerIdx).getBoard

  def reduce(row: Int, col: Int): (BoardInterface, Boolean) =
    getGameState.boards(getGameState.playerIdx).reduce(row,col)
}
