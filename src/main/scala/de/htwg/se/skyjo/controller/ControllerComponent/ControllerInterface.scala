package de.htwg.se.skyjo.controller.ControllerComponent

import de.htwg.se.skyjo.model.{BoardInterface, CardInterface, DeckInterface, DiscardPileInterface, GameState, State}
import de.htwg.se.skyjo.util.*

trait ControllerInterface extends Observable {
  // GAME MECHANICS //
  def setup(): Unit
  // def save(saveState: GameState):Unit
  def save(mem: Memento): Unit
  def undo(): Unit
  def redo(): Unit
  // def turnBoardCard(index: Int): Unit
  // def putCardOnBoard(pos: Int): Unit
  def drawFromDeck(): Unit
  def drawFromDisc(): Unit
  // def replaceCard(pos: Int): Unit
  // def SwapHandler(index: Int): Unit

  // def uptGameState(newState: GameState): Unit
  // OUTSIDE FUNCS //
  def getSize: (Int, Int)
  def getBrds: Vector[BoardInterface]
  def getBoard: Vector[Vector[CardInterface]]
  def getMediator: Mediator
  def getMementos: Vector[MoveCaretaker]
  def getGameState: GameState
  def getDeck: DeckInterface
  def getDisc: DiscardPileInterface
  def getDiscCard(): Option[CardInterface]
  def getPlIdx: Int
  def currMemento: MoveCaretaker

  def reduce(row: Int, col: Int): (BoardInterface, Boolean)

  def fillBoard(
      xSize: Int,
      ySize: Int,
      d: DeckInterface
  ): (BoardInterface, DeckInterface)

  def fullDeck(): Vector[CardInterface]

  // def len(x: Any): Int

  def toCard(x: Any): CardInterface

  def isCard(c: Any): Boolean

  // CARD INT //
  // def isTurned: Boolean

  // def trueCopy: CardInterface

  // def falseCopy: CardInterface

  // def turn: Unit

  // DECK INT //

  // def discardDrawnCard(): Unit
  def hasDrawn: Boolean

  def getDeckCards: Vector[CardInterface]

  def turnUpperCard: String

  def remove(amount: Int): Vector[CardInterface]

  def draw(): (CardInterface, DeckInterface)

  // DISC //
  def remove(): DiscardPileInterface


  def putToDiscardPile(from: Any): (DiscardPileInterface, DeckInterface)
  // OLD
  
  def getDrawn: Option[CardInterface]
  // def getdrawn: Option[CardInterface]
  // def getPhase: Boolean
  def currState: State

  def copy(med: Mediator = getMediator, mems: Vector[MoveCaretaker] = getMementos, brds: Vector[BoardInterface] = getBrds, d: DeckInterface = getDeck, disc: DiscardPileInterface = getDisc, idx: Int = getPlIdx,currentState: State = currState): GameState
  // def copy(med: Mediator, brds: Vector[BoardInterface], d: DeckInterface, disc: DiscardPileInterface, idx: Int, drawnCard: Option[CardInterface], flippedPhase: Boolean, state: State): GameState

  def assertGameState(newState: GameState): Unit

  def swapFromMem(c: CardInterface, pos: Int): BoardInterface

}
