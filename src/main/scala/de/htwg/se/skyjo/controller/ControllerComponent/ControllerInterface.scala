package de.htwg.se.skyjo.controller.ControllerComponent

import de.htwg.se.skyjo.model.{
  BoardInterface,
  CardInterface,
  DeckInterface,
  DiscardPileInterface
}
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.GameState
import de.htwg.se.skyjo.model.State

trait ControllerInterface extends Observable {
  // GAME MECHANICS //
  def uptGameState(newState: GameState): Unit
  def setup(): Unit
  def save(saveState: GameState):Unit
  def undo(): Unit
  def redo(): Unit
  def turnBoardCard(index: Int): Unit
  def putCardOnBoard(pos: Int): Unit
  
  def getMediator: Mediator
  def getGameState: GameState
  def getBrds: Vector[BoardInterface]
  def getADeck: DeckInterface
  def getDisc: DiscardPileInterface
  def getPldx: Int
  def getdrawn: Option[CardInterface]
  def getPhase: Boolean
  def currState: State

  def copy(med: Mediator, brds: Vector[BoardInterface], d: DeckInterface, disc: DiscardPileInterface, idx: Int, drawnCard: Option[CardInterface], flippedPhase: Boolean, state: State): GameState

  def assertGameState(newState: GameState): Unit

  def drawFromDeck(): Unit
  def drawFromDisc(): Unit
  def replaceCard(pos: Int): Unit
  def SwapHandler(index: Int): Unit

  // OUTSIDE FUNCS //
  def getBoard: Vector[Vector[CardInterface]]

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
  // def getCard: CardInterface

  def discardDrawnCard(): Unit

  def getDeck: Vector[CardInterface]

  def turnUpperCard: String

  def remove(amount: Int): Vector[CardInterface]

  def draw(): (CardInterface, DeckInterface)

  // DISC //
  def remove(): DiscardPileInterface

  def getDiscCard(): Option[CardInterface]

  def putToDiscardPile(from: Any): (DiscardPileInterface, DeckInterface)
}
