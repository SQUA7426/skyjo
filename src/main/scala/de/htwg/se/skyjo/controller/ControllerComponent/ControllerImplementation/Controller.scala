package de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation

import de.htwg.se.skyjo.model.{
  BoardInterface,
  CardInterface,
  DiscardPileInterface,
  DeckInterface,
  State,
  GameState
}
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{
  Deck,
  Card,
  Board,
  DiscardPile
}

import de.htwg.se.skyjo.aView.Gui.Gui
import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent._
import de.htwg.se.skyjo.util.{Observable, Memento, MoveCaretaker}
import de.htwg.se.skyjo.util.*

import de.htwg.se.skyjo.util.utilComponent.{SupportCommand, SupportHandler}

import scala.io.StdIn.{readInt, readLine}
import scala.util.Random

import scala.util.{Try, Success, Failure}
import de.htwg.se.skyjo.aView.Gui.{BoardView, fontname}

import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.skyjo.SkyjoModule
import net.codingwell.scalaguice.InjectorExtensions.*
import com.google.inject.name.Named

import de.htwg.se.skyjo.fileIoComponent.FileIOInterface
import de.htwg.se.skyjo.fileIoComponent.fileIoJsonImpl.JsonImpl
import de.htwg.se.skyjo.fileIoComponent.fileIoXmlImpl.XmlImpl

class Controller @Inject() (
    var state: GameState,
    @Named("plCount") plCount: Int,
    val med: Mediator
) extends Observable
    with ControllerInterface:

  var mem: Memento = _

  private var pendingDeckCard: Option[CardInterface] = None
  private var pendingDiscBefore: Option[DiscardPileInterface] = None

  // GAME MECHANICS //
  def setup(): Unit =
    val deck = new Deck(fullDeck())
    val disc = new DiscardPile()

    val plMoveC = Vector.fill(plCount)(new MoveCaretaker(this))
    val plBoards = Vector.fill(plCount)(new Board(4, 3, Vector.empty))

    this.state = new GameState(plMoveC, plBoards, deck, disc, 0, State.BEGIN)
    this.state = this.state.copy(
      deck = Deck(this),
      disc = DiscardPile()
    )
    var currentDeck = getGameState.deck

    for (i <- 0 until getBrds.size) {
      val (x, y) = getSize
      // val tmpMed = getMediator

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
    assertGameState(state)
    // notifyObservers

  def save(mementoSave: Memento): Unit =
    getMementos(getPlIdx).save(mementoSave)
    mem = mementoSave

  def undo(): Unit =
    val mv = getMementos(getPlIdx)
    if mv.undoStack.isEmpty then { println("Empty undoStack!"); return }

    val mementoToUndo = mv.undoStack.top
    mv.undo(mementoToUndo, getDeck, getBrds(getPlIdx), getDisc) match {
      case Some(memBoard, memDeck, memDisc) =>
        state = state.copy(
          boards = getBrds.updated(getPlIdx, memBoard),
          deck = memDeck,
          disc = memDisc
        )
        // pre nur setzen wenn noch etwas im undoStack ist
        if mv.undoStack.nonEmpty then
          state.currentState.pre = mv.undoStack.top.fromDeck match
            case 0 => "DECK"
            case 1 => "DISC"
            case _ => "SWITCH"
        else state.currentState.pre = "BEGIN"
      case None => println("Couldn't UNDO")
    }
    notifyObservers

  def redo(): Unit =
    val mv = getMementos(getPlIdx)
    if mv.redoStack.isEmpty then { println("Empty redoStack"); return }

    val mementoToRedo = mv.redoStack.top
    mv.redo(mementoToRedo, getDeck, getBrds(getPlIdx), getDisc) match {
      case Some(memBoard, memDeck, memDisc) =>
        state = state.copy(
          boards = getBrds.updated(getPlIdx, memBoard),
          deck = memDeck,
          disc = memDisc
        )
        // redoStack.last NICHT aufrufen — nach redo() kann der Stack leer sein
        // pre ist nach Redo immer BEGIN (Zug wurde wiederholt, Spieler ist fertig)
        state.currentState.pre = "BEGIN"
      case None => println("Couldn't REDO")
    }
    notifyObservers

  // GAMESTATE MECHANICS //
  def getGameState: GameState = state
  def assertGameState(newState: GameState): Unit =
    state = newState
    notifyObservers

  // MEDIATOR //
  def getMediator: Mediator = med

  // Memento //
  def getMementos: Vector[MoveCaretaker] = state.mementos
  def currMemento: MoveCaretaker = getMementos(getPlIdx)
  def hasDrawn: Boolean =
    currMemento.undoStack
      .lift(getPlIdx)
      .exists(_._2.isVal)

  def getDrawn: Option[CardInterface] =
    currMemento.undoStack
      .lift(getPlIdx)
      .map(_._2)

  // BOARD //
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
      val (card, nextDeck) = currentDeck.draw(this)
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
      new Board(xSize, ySize, finalGrid),
      remainingDeck
    )
  }

  def getSize: (Int, Int) = getBrds(getPlIdx).getSize
  def turnUpperCard: String = state.deck.turnUpperCard
  def reduce(row: Int, col: Int): (BoardInterface, Boolean, Int, Int) =
    getBrds(getPlIdx).reduce(row, col)
  def swapFromMem(c: CardInterface, pos: Int): BoardInterface =
    val b: BoardInterface = getBrds(state.plIdx).swapFromMem(c, pos)
    state = state.copy(boards = getBrds.updated(getPlIdx, b))
    notifyObservers
    b

  // CTRL - BOARD //
  def getBrds: Vector[BoardInterface] = state.boards
  def getBoard: Vector[Vector[CardInterface]] =
    state.boards(state.plIdx).getBoard

  def getReducedBrd(updatedBoard: BoardInterface): (BoardInterface, Int, Int) =
    val (x, y) = updatedBoard.getSize
    val reducedBoards: Array[(BoardInterface, Boolean, Int, Int)] = new Array(
      x + y
    )
    // REDUCE through all rows
    for i <- 0 until x do reducedBoards(i) = updatedBoard.reduce(-1, i)
    // REDUCE through all cols
    for j <- 0 until y do reducedBoards(j + x) = updatedBoard.reduce(j, -1)
    val r = reducedBoards.map(_._2).exists(_ == true)
    val endBoard: (BoardInterface, Int, Int) = if r == true then
      val allUpdatedBrds =
        reducedBoards
          .filter((brds, bools, row, col) => bools == true)
          .map((brd, bool, row, col) => (brd, row, col))
          .toArray
      allUpdatedBrds(0)
    else (updatedBoard, -1, -1)
    endBoard

  def reduceCurrentBoard(): Unit = {
    val board = getBrds(getPlIdx)
    val (reduced, row, col) = getReducedBrd(board)
    val newState = state.copy(
      boards = getBrds.updated(getPlIdx, reduced)
    )
    assertGameState(newState)
  }

  // DECK //
  def getDeck: DeckInterface = state.deck
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

  def getDeckCards: Vector[CardInterface] = state.deck.getDeckCards
  def remove(amount: Int): Vector[CardInterface] =
    state.deck.remove(amount)
  def draw(): (
      CardInterface,
      DeckInterface
  ) = state.deck.draw(this)

  def drawFromDeck(pos: Int): Unit = {
    val (card, newDeck) = state.deck.draw(this)
    val (swCard, tmpBrd) = getBrds(getPlIdx).switch(card, pos)
    val (reduced_brd, _, _) = getReducedBrd(tmpBrd)
    val currPlayer = getPlIdx
    val newDisc = putToDiscardPile(swCard)._1

    mem = Memento(0, card, pos, swCard, getDisc, swCard.isTurned)
    save(mem)

    val newState = state.copy(
      boards = getBrds.updated(currPlayer, reduced_brd),
      deck = newDeck,
      disc = newDisc,
      plIdx = (currPlayer + 1) % getBrds.size,
      currentState = currState.reset()
    )
    assertGameState(newState)
  }

  // DISCARDPILE //
  def putToDiscardPile(from: Any): (
      DiscardPileInterface,
      DeckInterface
  ) = state.disc.putToDiscardPile(from, this)
  def remove(): DiscardPileInterface =
    state.disc.remove()

  // CTR - DISCARDPILE //
  def getDisc: DiscardPileInterface = state.disc
  def getDiscCard(): Option[CardInterface] =
    state.disc.getDiscCard(this)

  def drawFromDisc(pos: Int): Unit = {
    getDiscCard() match {
      case Some(card) => {
        mem = Memento(
          1,
          card,
          pos,
          getBrds(getPlIdx).getBoardCard(
            pos
          ),
          getDisc,
          getBrds(getPlIdx).getBoardCard(pos).isTurned
        )
        save(mem)

        val (newCard, newBrd) = getBrds(getPlIdx).switch(card, pos)
        val (reduced_brd, _, _) = getReducedBrd(newBrd)
        val newDisc = new DiscardPile(
          newCard.getValue.toString,
          true
        )
        val currPlayer = getPlIdx
        val newState = state.copy(
          boards = getBrds.updated(currPlayer, reduced_brd),
          disc = newDisc,
          plIdx = (currPlayer + 1) % getBrds.size,
          currentState = currState.reset()
        )
        assertGameState(newState)
      }
      case None => println("DISC EMPTY"); assertGameState(getGameState)
    }
  }

  def switchDeckDisc(
      gs: GameState,
      b: BoardInterface,
      tmpDeck: DeckInterface,
      idx: Int
  ): Unit =
    val (oldCard, tmpBrd) = b.switch(b.getBoardCard(idx), idx)
    val newBrd = getReducedBrd(tmpBrd)._1
    val gottenMem = getMementos(getPlIdx).undoStack(0)
    val uptMem = gottenMem.copy(
      boardIndex = idx,
      takenCard = Card(Integer.parseInt(tmpDeck.toString())),
      replacedCard = oldCard
    )
    save(uptMem)
    println(currMemento.undoStack.toString())
    val newGameState = gs.copy(
      boards = getBrds.updated(getPlIdx, newBrd),
      currentState = currState.reset()
    )

    println(s"Controller switchDeckDisc: uptMem:\n${uptMem}\n");

    assertGameState(newGameState)

  def tuiSwitch(gs: GameState, tmpDeck: DeckInterface): Unit =
    printf(">> turn Position: ")
    val pos2 = readLine()
    val idx = Try(Integer.parseInt(pos2)).getOrElse(0)
    println(s"Controller tuiSwitch: pos2: ${idx}");
    val newGS =
      switchDeckDisc(gs, getBrds(getPlIdx), tmpDeck, idx)

  def tuiNotSwitch(input: String, pos: String): Unit =
    val pos2 = if pos == "" then "0" else pos
    val h = SupportHandler(
      this,
      getBrds(getPlIdx),
      getDeck,
      getDisc
    )
    val return_H = h.handle(input, pos2.toInt)
    return_H match {
      case Success(gs) =>
        val newBrd = getReducedBrd(gs.boards(getPlIdx))._1
        val copyGameState = gs.copy(
          boards = gs.boards.updated(getPlIdx, newBrd)
        )
        assertGameState(copyGameState)
      case Failure(exception) => assertGameState(getGameState)
    }

  // PLAYER //
  def getPlIdx: Int = state.plIdx
  def nextPlayer: Unit = copy_state(idx = (getPlIdx + 1) % getBrds.size)

  // STATE //
  def currState: State = state.currentState

  // FILEIO //
  val injector = Guice.createInjector(SkyjoModule(getBrds.size))
  val xmlFileName = "game_state_data.xml"
  val jsonFileName = "game_state_data.json"

  def syncControllerGui(b: BoardView): Unit =
    b.termBoard = getGameState.boards(getPlIdx)
    b.aDeck = getDeck
    b.aDisc = getDisc

    b.manyCards = b.BOARD_INIT(false)
    b.vDeck.cCard = toCard(b.aDeck.turnUpperCard)
    b.vDiscard.cCard = getDiscCard().getOrElse(toCard(0).falseCopy)
    b.vDiscard.turned = getDiscCard().isDefined

  def xml_save: Unit = {
    val xml_IO = injector.instance[XmlImpl]
    xml_IO.save(getGameState, xmlFileName)
  }
  def json_save: Unit = {
    val json_IO = injector.instance[JsonImpl]
    json_IO.save(getGameState, jsonFileName)
  }

  def xml_load(b: Any): Unit = {
    val xml_IO = injector.instance[XmlImpl]

    val newState = xml_IO.load(xmlFileName)

    require(newState.deck != null, "Loaded GameState has null deck!")
    require(newState.disc != null, "Loaded GameState has null disc!")

    val cleanState = newState.copy(currentState = State.BEGIN)

    assertGameState(cleanState)

    // view
    b match {
      case bv: BoardView => syncControllerGui(bv)
      case _             => {}
    }
  }
  def json_load(b: Any): Unit = {
    val json_IO = injector.instance[JsonImpl]
    val newState = json_IO.load(jsonFileName)

    val cleanState = newState.copy(currentState = State.BEGIN)

    assertGameState(cleanState)

    b match {
      case bv: BoardView => syncControllerGui(bv)
      case _             => {}
    }
  }

  // OUTSIDE FUNCTIONS //

  def toCard(x: Any): CardInterface = {
    val valRange = (-2 to 12).toSet

    x match {
      case c: CardInterface => c

      case a: Int =>
        if (valRange.contains(a)) Card(a, true)
        else Card(0, false)

      case b: String =>
        val tryInt = scala.util.Try(b.toInt)
        if (tryInt.isSuccess) {
          Card(tryInt.get, true)
        } else {
          Card(0, false)
        }
      case scala.util.Success(value) => toCard(value)
      case scala.util.Failure(_)     => Card(0, false)

      case Some(value) => toCard(value)
      case None        => Card(0, false)

      case d: DeckInterface =>
        d.getCard.map(toCard).getOrElse(Card(0, false))

      case disc: DiscardPileInterface =>
        disc.getDiscCard(this).map(toCard).getOrElse(Card(0, false))

      case _ => Card(0, false)
    }
  }

  def isCard(c: Any): Boolean = c match {
    case _: CardInterface => true
    case _                => false
  }

  def copy_state(
      // med: Mediator = getMediator,
      mems: Vector[MoveCaretaker] = getMementos,
      brds: Vector[BoardInterface] = getBrds,
      d: DeckInterface = getDeck,
      disc: DiscardPileInterface = getDisc,
      idx: Int = getPlIdx,
      anotherState: State = currState
  ): GameState = {
    state.copy(
      // med = med,
      mementos = mems,
      boards = brds,
      deck = d,
      disc = disc,
      plIdx = idx,
      currentState = anotherState
    )
  }

  // GUI //
  def guiUndo(
      resBoard: BoardInterface,
      resDeck: DeckInterface,
      resDisc: DiscardPileInterface,
      b: BoardView
  ): Unit =
    b.termBoard = resBoard
    b.aDeck = resDeck
    b.aDisc = resDisc

    assertGameState(
      getGameState.copy(
        boards = getBrds.updated(getPlIdx, resBoard),
        deck = resDeck,
        disc = resDisc
      )
    )

    b.manyCards = b.BOARD_INIT(false)
    b.vDeck.cCard = toCard(b.aDeck.turnUpperCard)
    b.vDiscard.cCard =
      toCard(state.disc, if state.disc.pre == "Disc" then true else false)

  def guiRedo(
      resBoard: BoardInterface,
      resDeck: DeckInterface,
      resDisc: DiscardPileInterface,
      b: BoardView
  ): Unit =
    val lDisc = currMemento.undoStack(0).lastDisc

    b.termBoard = resBoard
    b.aDeck = resDeck
    b.aDisc = lDisc
    assertGameState(
      getGameState.copy(
        boards = getBrds.updated(getPlIdx, resBoard),
        deck = resDeck,
        disc = resDisc
      )
    )

  def guiPreviewDeckCard(): Unit = {
    println("Controller.guiPreviewDeckCard aufgerufen")
    val (card, newDeck) = state.deck.draw(this)
    val s = State.MID
    s.pre = "DECK"
    val newState = state.copy(
      deck = newDeck,
      previewDeckCard = Some(card),
      currentState = s
    )
    assertGameState(newState)
  }

  def guiConfirmDeckSwitch(pos: Int): Unit = {
    state.previewDeckCard match {
      case Some(card) =>
        println(
          s"\nguiConfirmDeckSwitch CARD => ${card} ; turned: ${card.isTurned}"
        )
        val (swCard, tmpBrd) = getBrds(getPlIdx).switch(card, pos)
        println(s"swCard: ${swCard}; turned: ${swCard.isTurned}")
        val (reduced_brd, _, _) = getReducedBrd(tmpBrd)
        mem = Memento(0, card, pos, swCard, getDisc, swCard.isTurned)

        save(mem)

        val currPlayer = getPlIdx
        val newDisc = putToDiscardPile(swCard)._1
        val newState = state.copy(
          boards = getBrds.updated(currPlayer, reduced_brd),
          disc = newDisc,
          previewDeckCard = None,
          plIdx = (currPlayer + 1) % getBrds.size,
          currentState = currState.reset()
        )
        assertGameState(newState)
      case None =>
        println("Keine Preview-Deckkarte vorhanden.")
    }
  }

  def guiConfirmDeckToDiscAndTurn(pos: Int): Unit = {
    state.previewDeckCard match {
      case Some(card) =>
        val currPlayer = getPlIdx
        val board = getBrds(currPlayer)

        val boardCardBefore = board.getBoardCard(pos)

        val newDisc = putToDiscardPile(card)._1
        val newBoard = board.turnBoardCard(pos)
        val (reduced_brd, _, _) = getReducedBrd(newBoard)

        mem = Memento(
          2,
          card,
          pos,
          boardCardBefore,
          getDisc,
          boardCardBefore.isTurned
        )
        save(mem)

        val e = State.END
        e.pre = "DISC"

        val newState = state.copy(
          boards = getBrds.updated(currPlayer, reduced_brd),
          disc = newDisc,
          previewDeckCard = None,
          plIdx = (currPlayer + 1) % getBrds.size,
          currentState = e
        )
        assertGameState(newState)

      case None =>
        println("Keine Preview-Deckkarte vorhanden.")
    }
  }

  override def guiTurnBrdCard(pos: Int): Unit = {
    val currPlayer = getPlIdx
    val board = getBrds(currPlayer)
    val boardCardBefore = board.getBoardCard(pos)
    val newBoard = board.turnBoardCard(pos)
    val (reduced_brd, _, _) = getReducedBrd(newBoard)

    pendingDeckCard match {
      case Some(deckCard) =>
        mem = Memento(
          2,
          deckCard,
          pos,
          boardCardBefore,
          pendingDiscBefore.getOrElse(getDisc),
          boardCardBefore.isTurned
        )
        save(mem)
        pendingDeckCard = None
        pendingDiscBefore = None
      case None => ()
    }

    val newState = state.copy(
      boards = getBrds.updated(currPlayer, reduced_brd),
      plIdx = (currPlayer + 1) % getBrds.size,
      currentState = currState.reset(),
      previewDeckCard = None
    )
    assertGameState(newState)
  }

  def guiSelectDisc(): Unit = {
    val s = State.MID
    s.pre = "DISC"
    val newState = state.copy(currentState = s)
    assertGameState(newState)
  }

  def guiDeckToDisc(): Unit = {
    state.previewDeckCard match {
      case Some(card) =>
        pendingDeckCard = Some(card)
        pendingDiscBefore = Some(getDisc)

        val newDisc = DiscardPile(card.getValue.toString, true)
        val e = State.END
        e.pre = "DISC_TURN"

        val newState = state.copy(
          disc = newDisc,
          previewDeckCard = None,
          currentState = e
        )
        assertGameState(newState)

      case None => println("Keine Preview-Deckkarte vorhanden.")
    }
  }
