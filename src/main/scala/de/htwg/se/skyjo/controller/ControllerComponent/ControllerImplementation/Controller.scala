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
// private val caretaker = new MoveCaretaker()
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

    // 1. Memento für Undo erstellen
    mem = Memento(true, card, 0, card, getDisc, card.isTurned)
    getMementos(getPlIdx).save(mem)

    // 2. Zustand im Controller permanent aktualisieren!
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
      // 1. Wenn es schon ein CardInterface ist -> einfach durchreichen
      case c: CardInterface => c

      // 2. Wenn es ein Integer ist -> neue saubere Karte erstellen
      case a: Int =>
        if (valRange.contains(a)) Card(a, true, this)
        else Card(0, false, this) // Oder Fehler werfen

      // 3. Wenn es ein String ist -> erst konvertieren, dann prüfen
      case b: String =>
        val tryInt = scala.util.Try(b.toInt)
        if (tryInt.isSuccess) {
          Card(tryInt.get, true, this)
        } else {
          // Wenn es ein Text wie "Disc" ist, gib eine versteckte Platzhalter-Karte zurück
          Card(0, false, this)
        }
      // 4. Try-Wrapper -> Rekursiv entpacken
      case scala.util.Success(value) => toCard(value)
      case scala.util.Failure(_)     => Card(0, false, this)

      // 5. Option-Wrapper -> Rekursiv entpacken
      case Some(value) => toCard(value)
      case None        => Card(0, false, this)

      // 6. Interfaces (Deck/Discard)
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

  // NOT IMPL //
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
  // def falseCopy: CardInterface
  // def trueCopy: CardInterface

  // def getCard: CardInterface
  def getDeckCards: Vector[CardInterface] = state.deck.getDeckCards
  def getDiscCard(): Option[CardInterface] =
    state.disc.getDiscCard()
  // def isTurned: Boolean
  def putToDiscardPile(from: Any): (
      DiscardPileInterface,
      DeckInterface
  ) = state.disc.putToDiscardPile(from)
  def remove(amount: Int): Vector[CardInterface] =
    state.deck.remove(amount)
  def remove(): DiscardPileInterface =
    state.disc.remove()
  // def turn: Unit
  def turnUpperCard: String = state.deck.turnUpperCard

  def getBoard: Vector[Vector[CardInterface]] =
    state.boards(state.plIdx).getBoard
  def getGameState: GameState = state

  def reduce(row: Int, col: Int): (BoardInterface, Boolean) =
    state.boards(state.plIdx).reduce(row, col)

  def getPlIdx: Int = state.plIdx
  // def getdrawn: Option[CardInterface] = {
  //   state.drawnCard match {
  //   case Some(ci) => Some(ci)
  //   case None => None
  //   }
  // }
  def currState: State = state.currentState

  // def copy(
  //     med: Mediator = getMediator,
  //     brds: Vector[BoardInterface] = getBrds,
  //     d: DeckInterface = getDeck,
  //     disc: DiscardPileInterface = getDisc,
  //     idx: Int = getPlIdx,
  //     // drawnCard: Option[CardInterface] = getdrawn, flippedPhase: Boolean = getPhase,
  //     state: State = currState
  // ): GameState = {
  //   GameState(med, getMementos, brds, d, disc, idx, state)
  // }

  def getMementos: Vector[MoveCaretaker] = state.mementos

  def assertGameState(newState: GameState): Unit = {
    state = newState
    notifyObservers
  }

  // Die korrigierte copy-Methode
  // Wir nutzen hier die Namen aus der Fehlermeldung: currentState
  def copy(
      med: Mediator = getMediator,
      mems: Vector[MoveCaretaker] = getMementos,
      brds: Vector[BoardInterface] = getBrds,
      d: DeckInterface = getDeck,
      disc: DiscardPileInterface = getDisc,
      idx: Int = getPlIdx,
      anotherState: State =
        currState // Wir nennen den Parameter hier 'anotherState'
  ): GameState = {
    // Hier rufen wir die copy-Methode des GameState-Case-Class auf
    // WICHTIG: Der Parameter im GameState heißt laut deiner Meldung 'currentState'
    state.copy(
      med = med,
      mementos = mems,
      boards = brds,
      deck = d,
      disc = disc,
      plIdx = idx,
      currentState =
        anotherState // Mapping auf den richtigen Namen im GameState
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

//   def setup(): Unit = {
//     var currentDeck = state.deck
//
//     for (i <- 0 until state.boards.size) {
//       val (x, y) = state.boards(i).getSize
//       val tmpMed = this.getMediator
//
//       val (afterBoard, nextDeck) = fillBoard(x, y, currentDeck)
//       currentDeck = nextDeck
//
//       val initSize = x * y
//       val arr = Random.shuffle((0 until initSize).toList)
//       val finalBoard = afterBoard.turnBoardCard(arr(0)).turnBoardCard(arr(1))
//
//       state = state.copy(
//         boards = state.boards.updated(i, finalBoard),
//         deck = currentDeck
//       )
//     }
//     state = state.copy(playerIdx = 0)
//     notifyObservers
//   }
//
//   def executeMove(moveLogic: => GameState): Unit = {
//     caretaker.save(state)
//     state = moveLogic
//     notifyObservers
//   }
//
//   def save(saveState: GameState):Unit = caretaker.save(saveState)
//
//   def undo(): Unit = {
//     caretaker.undo(state) match {
//       case Some(oldState) =>
//         state = oldState
//         notifyObservers
//       case None => println("Nothing to undo!")
//     }
//   }
//   def redo(): Unit = {
//     caretaker.redo(state) match {
//       case Some(oldState) =>
//         state = oldState
//         notifyObservers
//       case None => println("Nothing to undo!")
//     }
//   }
//
//   def turnBoardCard(index: Int): Unit = {
//     val board = state.boards(state.playerIdx)
//
//     val cardToFlip = board.getBoardCard(index)
//
//     if (cardToFlip.isTurned) {
//       println(
//         s"Card at index $index is already face up! Choose a hidden card (#)."
//       )
//     } else {
//       caretaker.save(state)
//       val newBoard = board.turnBoardCard(index)
//
//       state = state.copy(
//         boards = state.boards.updated(state.playerIdx, newBoard),
//         isFlippingPhase = false,
//         playerIdx = (state.playerIdx + 1) % state.boards.size
//       )
//       notifyObservers
//     }
//   }
//
//   def putCardOnBoard(pos: Int): Unit = {
//     executeMove {
//       val board = state.boards(state.playerIdx)
//       val newBoard = board.turnBoardCard(pos)
//
//       state.copy(
//         boards = state.boards.updated(state.playerIdx, newBoard),
//         isFlippingPhase = false,
//         playerIdx = (state.playerIdx + 1) % state.boards.size
//       )
//     }
//     notifyObservers
//   }
//   def getGameState: GameState = state
//
//   def uptGameState(newState: GameState): Unit = {
//     state = newState
//     notifyObservers
//   }
//
//   def drawFromDeck(): Unit = {
//     val (card, newDeck) = state.deck.draw()
//
//     state = state.copy(
//       deck = newDeck,
//       drawnCard = Some(card)
//     )
//     notifyObservers
//   }
//
//   def drawFromDisc(): Unit = {
//     state.disc.getDiscCard() match {
//       case Some(card) => {
//         caretaker.save(state)
//         val newDisc = state.disc.remove()
//
//         state = state.copy(
//           disc = newDisc,
//           drawnCard = Some(card)
//         )
//         notifyObservers
//       }
//       case None => println("DiscardPile cannot be accessed!")
//     }
//
//   }
//
//   def replaceCard(pos: Int): Unit = {
//     state.drawnCard match {
//       case Some(card) =>
//         caretaker.save(state)
//
//         val currentBoard = state.boards(state.playerIdx).asInstanceOf[BoardInterface]
//         val (oldCard, nextBoard) = currentBoard.switch(card, pos)
//
//         val nextDisc =
//           state.disc
//             .putToDiscardPile(oldCard)
//             ._1
//
//         state = state.copy(
//           boards = state.boards.updated(state.playerIdx, nextBoard),
//           disc = nextDisc,
//           drawnCard = None,
//           playerIdx = (state.playerIdx + 1) % state.boards.size
//         )
//         notifyObservers
//       case None => println("You are not holding a card!")
//     }
//   }
//   def SwapHandler(index: Int): Unit = {
//     caretaker.save(state)
//     val board = state.boards(state.playerIdx)
//     val newBoard = board.turnBoardCard(index)
//
//     state = state.copy(
//       boards = state.boards.updated(state.playerIdx, newBoard),
//       isFlippingPhase = false,
//       playerIdx = (state.playerIdx + 1) % state.boards.size
//     )
//     notifyObservers
//   }
//
//   def discardDrawnCard(): Unit = {
//     state.drawnCard.foreach { card =>
//       caretaker.save(state)
//       val (newDisc, _) = state.disc.putToDiscardPile(card)
//       state = state.copy(
//         drawnCard = None,
//         disc = newDisc,
//         isFlippingPhase = true
//       )
//       notifyObservers
//     }
//   }
//
//   def fillBoard(
//       xSize: Int,
//       ySize: Int,
//       d: DeckInterface
//   ): (BoardInterface, DeckInterface) = {
//
//     if (d.getDeckCards.isEmpty) {
//       val newFullDeck = Deck(this)
//       return fillBoard(xSize, ySize, newFullDeck)
//     }
//
//     def drawOne(currentDeck: DeckInterface): (CardInterface, DeckInterface) = {
//       val (card, nextDeck) = currentDeck.draw()
//       (card.falseCopy, nextDeck)
//     }
//
//     def fillRows(
//         currentDeck: DeckInterface,
//         rowsLeft: Int
//     ): (Vector[Vector[CardInterface]], DeckInterface) = {
//       if (rowsLeft == 0) (Vector.empty, currentDeck)
//       else {
//         val (row, deckAfterRow) = fillRow(currentDeck, xSize)
//         val (remainingRows, finalDeck) = fillRows(deckAfterRow, rowsLeft - 1)
//         (row +: remainingRows, finalDeck)
//       }
//     }
//
//     def fillRow(
//         currentDeck: DeckInterface,
//         cardsLeft: Int
//     ): (Vector[CardInterface], DeckInterface) = {
//       if (cardsLeft == 0) (Vector.empty, currentDeck)
//       else {
//         val (card, nextDeck) = drawOne(currentDeck)
//         val (restOfRow, deckAfterRest) = fillRow(nextDeck, cardsLeft - 1)
//         (card +: restOfRow, deckAfterRest)
//       }
//     }
//     val (finalGrid, remainingDeck) = fillRows(d, ySize)
//     (
//       new Board(getMediator, xSize, ySize, finalGrid),
//       remainingDeck
//     )
//   }
//   def getMediator: Mediator = this.getGameState.med
//
//   def toCard(x: Any): CardInterface = {
//     val val1d = (for {j <- -2 to 12} yield j.toString()).toVector
//     x match {
//       case a: Int => Card(a.toInt, true, this)
//       case b: String if val1d.contains(b) =>
//         Card(Integer.parseInt(b), true, this)
//       case d: DeckInterface => d.getCard
//       case disc: DiscardPileInterface => disc.getDiscCard() match {
//         case Some(ci) => ci
//         case None => Card(0, false, this)
//       }
//       case b: String if b == "Deck" || b == "Disc" => Card(0, true, this)
//       case other =>
//         throw new IllegalArgumentException(s"Invalid input:$other with type: ${other.getClass}")
//     }
//   }
//
//   def isCard(c: Any): Boolean = c match {
//     case _: Card => true
//     case _       => false
//   }
//
//   def fullDeck(): Vector[CardInterface] = {
//     val seqCards = Seq.empty[CardInterface]
//     val v1: Vector[CardInterface] =
//       (for { i <- 1 to 10; j <- -1 to 12 } yield toCard(j)).toVector
//     val v2: Vector[CardInterface] = (for {
//       i <- 1 to 5; j <- -2 to 0; if j == -2 || j == 0
//     } yield toCard(j)).toVector
//     val fullDeck: Vector[CardInterface] = v1 ++ v2
//     val diffs: Vector[CardInterface] = fullDeck.diff(seqCards)
//     val shuffled = Random.shuffle(diffs)
//     shuffled
//   }
//
//   // NOT IMPL //
//   def draw(): (
//       CardInterface,
//       DeckInterface
//   ) = state.deck.draw()
//   // def falseCopy: CardInterface
//   // def trueCopy: CardInterface
//
//   // def getCard: CardInterface
//   def getDeckCards: Vector[CardInterface] = state.deck.getDeckCards
//   def getDiscCard(): Option[CardInterface] =
//     state.disc.getDiscCard()
//   // def isTurned: Boolean
//   def putToDiscardPile(from: Any): (
//       DiscardPileInterface,
//       DeckInterface
//   ) = state.disc.putToDiscardPile(from)
//   def remove(amount: Int): Vector[CardInterface] =
//     state.deck.remove(amount)
//   def remove(): DiscardPileInterface =
//     state.disc.remove()
//   // def turn: Unit
//   def turnUpperCard: String = state.deck.turnUpperCard
//
//   def getBoard: Vector[Vector[CardInterface]] = getGameState.boards(getGameState.playerIdx).getBoard
//
//   def reduce(row: Int, col: Int): (BoardInterface, Boolean) =
//     getGameState.boards(getGameState.playerIdx).reduce(row,col)
//
//   def getPldx: Int = state.playerIdx
//   def getdrawn: Option[CardInterface] = {
//     state.drawnCard match {
//     case Some(ci) => Some(ci)
//     case None => None
//     }
//   }
//   def getPhase: Boolean = state.isFlippingPhase
//   def currState: State = state.currentState
//
//   def copy(med: Mediator = getMediator, brds: Vector[BoardInterface] = getBrds, d: DeckInterface = getDeck, disc: DiscardPileInterface = getDisc, idx: Int = getPldx, drawnCard: Option[CardInterface] = getdrawn, flippedPhase: Boolean = getPhase, state: State = currState): GameState = {
//     GameState(med, brds,d,disc,idx,drawnCard,flippedPhase, state)
//   }
//
//   def assertGameState(newState: GameState): Unit = {
//     state = newState
//   }
//
//   def getBrds: Vector[BoardInterface] = state.boards
//   def getDeck: DeckInterface = state.deck
//   def getDisc: DiscardPileInterface = state.disc
//   def getSize: (Int, Int) = getGameState.boards(state.playerIdx).getSize
// }
