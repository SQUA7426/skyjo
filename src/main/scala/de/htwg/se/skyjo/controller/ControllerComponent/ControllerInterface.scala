package de.htwg.se.skyjo.controller.ControllerComponent

import de.htwg.se.skyjo.model.{BoardInterface, CardInterface, DeckInterface, DiscardPileInterface, GameState, State}
import de.htwg.se.skyjo.util.*

import de.htwg.se.skyjo.aView.Gui.{BoardView, fontname}

trait ControllerInterface extends Observable:
  val path = "saves/"

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
  def reduceCurrentBoard(): Unit
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

  def drawFromDeck(pos: Int): Unit

  // DISCARDPILE //
  def putToDiscardPile(from: Any): (DiscardPileInterface, DeckInterface)
  def remove(): DiscardPileInterface

  def switchDeckDisc(gs: GameState, b:BoardInterface, tmpDeck: DeckInterface, idx: Int): Unit
  def tuiSwitch(gs: GameState, tmpDeck: DeckInterface): Unit
  def tuiNotSwitch(input: String, pos: String): Unit

  // CTR - DISCARDPILE //
  def getDisc: DiscardPileInterface
  def getDiscCard(): Option[CardInterface]
  def drawFromDisc(pos: Int): Unit

  // PLAYER //
  def getPlIdx: Int
  def nextPlayer: Unit

  // STATE //
  def currState: State

  // GUI //
  def guiUndo(resBoard: BoardInterface, resDeck: DeckInterface, resDisc: DiscardPileInterface, b: BoardView): Unit

  def guiRedo(resBoard: BoardInterface, resDeck: DeckInterface, resDisc: DiscardPileInterface, b: BoardView): Unit

  def guiPreviewDeckCard(): Unit
  def guiConfirmDeckSwitch(pos: Int): Unit
  def guiConfirmDeckToDiscAndTurn(pos: Int): Unit

  def guiTurnBrdCard(pos: Int): Unit

  def guiSelectDisc(): Unit

  def guiDeckToDisc(): Unit

  // FILEIO //
  def syncControllerGui(b: BoardView): Unit

  def xml_save: Unit
  def json_save: Unit

  def xml_load(b: Any): Unit
  def json_load(b: Any): Unit

  // OUTSIDE FUNCTIONS //
  def toCard(x: Any): CardInterface
  def toCard(x: Any, turned: Boolean): CardInterface =
    val base = toCard(x)
    if turned then base.trueCopy else base.falseCopy


  def isCard(c: Any): Boolean

  def copy(mems: Vector[MoveCaretaker] = getMementos, brds: Vector[BoardInterface] = getBrds, d: DeckInterface = getDeck, disc: DiscardPileInterface = getDisc, idx: Int = getPlIdx,currentState: State = currState): GameState
