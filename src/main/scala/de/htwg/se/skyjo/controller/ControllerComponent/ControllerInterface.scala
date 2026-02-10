package de.htwg.se.skyjo.controller.ControllerComponent

import de.htwg.se.skyjo.model.{BoardInterface, CardInterface, DeckInterface, DiscardPileInterface, GameState, State}
import de.htwg.se.skyjo.util.*

trait ControllerInterface extends Observable:

  // GAME MECHANICS //
  def setup(): Unit

  def save(mem: Memento): Unit
  def undo(): Unit
  def redo(): Unit

  // GAMESTATE MECHANICS //
  def getGameState: GameState
  def assertGameState(newState: GameState): Unit

  // MEDIATOR //
  def getMediator: Mediator

  // Memento //
  def getMementos: Vector[MoveCaretaker]
  def currMemento: MoveCaretaker
  def hasDrawn: Boolean
  def getDrawn: Option[CardInterface]

  // BOARD //
  def fillBoard(
      xSize: Int,
      ySize: Int,
      d: DeckInterface
  ): (BoardInterface, DeckInterface)
  def getSize: (Int, Int)
  def turnUpperCard: String
  def reduce(row: Int, col: Int): (BoardInterface, Boolean, Int, Int)
  def swapFromMem(c: CardInterface, pos: Int): BoardInterface

  // CTRL - BOARD //
  def getBrds: Vector[BoardInterface]
  def getBoard: Vector[Vector[CardInterface]]
  def getReducedBrd(updatedBoard: BoardInterface): (BoardInterface, Int, Int)

  // DECK //
  def getDeck: DeckInterface
  def fullDeck(): Vector[CardInterface]
  def getDeckCards: Vector[CardInterface]
  def remove(amount: Int): Vector[CardInterface]
  def draw(): (CardInterface, DeckInterface)

  def drawFromDeck(pos: Int): GameState

  // DISCARDPILE //
  def putToDiscardPile(from: Any): (DiscardPileInterface, DeckInterface)
  def remove(): DiscardPileInterface

  // CTR - DISCARDPILE //
  def getDisc: DiscardPileInterface
  def getDiscCard(): Option[CardInterface]
  def drawFromDisc(pos: Int): GameState

  // PLAYER //
  def getPlIdx: Int
  def nextPlayer: Unit

  // STATE //
  def currState: State

  // OUTSIDE FUNCTIONS //
  def toCard(x: Any): CardInterface

  def isCard(c: Any): Boolean

  def copy(med: Mediator = getMediator, mems: Vector[MoveCaretaker] = getMementos, brds: Vector[BoardInterface] = getBrds, d: DeckInterface = getDeck, disc: DiscardPileInterface = getDisc, idx: Int = getPlIdx,currentState: State = currState): GameState
