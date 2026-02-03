package de.htwg.se.skyjo.controller.ControllerComponent

import de.htwg.se.skyjo.model.{BoardInterface, CardInterface, DeckInterface, DiscardPileInterface, GameState, State}
import de.htwg.se.skyjo.util.*

trait ControllerInterface extends Observable {
  // GAME MECHANICS //
  def setup(): Unit
  def save(mem: Memento): Unit
  def undo(): Unit
  def redo(): Unit
  def drawFromDeck(pos: Int): GameState
  def drawFromDisc(pos: Int): GameState

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

  // BOARD //
  def reduce(row: Int, col: Int): (BoardInterface, Boolean)

  def fillBoard(
      xSize: Int,
      ySize: Int,
      d: DeckInterface
  ): (BoardInterface, DeckInterface)

  def fullDeck(): Vector[CardInterface]

  def toCard(x: Any): CardInterface

  def isCard(c: Any): Boolean

  def hasDrawn: Boolean

  def getDeckCards: Vector[CardInterface]

  def turnUpperCard: String

  def remove(amount: Int): Vector[CardInterface]

  def draw(): (CardInterface, DeckInterface)

  def remove(): DiscardPileInterface

  def putToDiscardPile(from: Any): (DiscardPileInterface, DeckInterface)
  
  def getDrawn: Option[CardInterface]

  def currState: State

  def copy(med: Mediator = getMediator, mems: Vector[MoveCaretaker] = getMementos, brds: Vector[BoardInterface] = getBrds, d: DeckInterface = getDeck, disc: DiscardPileInterface = getDisc, idx: Int = getPlIdx,currentState: State = currState): GameState

  def assertGameState(newState: GameState): Unit

  def swapFromMem(c: CardInterface, pos: Int): BoardInterface

  def getReducedBrd(updatedBoard: BoardInterface): BoardInterface
}
