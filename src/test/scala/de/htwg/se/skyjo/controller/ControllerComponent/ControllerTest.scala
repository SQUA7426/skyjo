package de.htwg.se.skyjo.controller.ControllerComponent

<<<<<<< HEAD
import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
import de.htwg.se.skyjo.model.CardImplementation.*
import de.htwg.se.skyjo.model.DeckImplementation.*
import de.htwg.se.skyjo.model.BoardImplementation.*
import de.htwg.se.skyjo.model.DiscardPileImplementation.*
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.{GameState, DeckInterface, CardInterface, BoardInterface, DiscardPileInterface}
=======
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{Deck, DiscardPile, Board, Card}
import de.htwg.se.skyjo.util.{Mediator, Memento, MoveCaretaker}
import de.htwg.se.skyjo.model.{GameState, DeckInterface, CardInterface, BoardInterface, DiscardPileInterface, State}
import de.htwg.se.skyjo.fileIoComponent.fileIoJsonImpl.JsonImpl
import de.htwg.se.skyjo.fileIoComponent.fileIoXmlImpl.XmlImpl
>>>>>>> origin/docker

import scala.io.StdIn.{readInt, readLine}
import scala.util.Random
import java.io.ByteArrayInputStream
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
<<<<<<< HEAD
=======

import scalafx.scene.layout.Pane
import de.htwg.se.skyjo.aView.Gui.{BoardView, fontname}

import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.skyjo.SkyjoModule
import net.codingwell.scalaguice.InjectorExtensions.*
import de.htwg.se.skyjo.util.ConcreteMediator
>>>>>>> origin/docker

class ControllerTest extends AnyWordSpec with Matchers {
  "A Controller" when:
    val plCount = 1
<<<<<<< HEAD
    val med = new ConcreteMediator()

    val tempState = new GameState(med, Vector.empty, null, null, 0, None)
    val ctr = new Controller(tempState)

    val deck = new Deck(ctr.fullDeck(), ctr)
    val disc = new DiscardPile(ctr)

    val plBoards = Vector.fill(plCount)(new Board(med, 4, 3, Vector.empty))

    ctr.state = new GameState(med, plBoards, deck, disc, 0, None)

    // setup()
    ctr.setup()

    val tui = new Tui(ctr)

    val state:GameState = ctr.getGameState
    val card8 = Card(9,ctr)
    val anotherState = state.copy(
      drawnCard = Some(card8)
      )
    "it is working, it" should {
      "get Mediator, GameState, Deck and Discard-Card" in:
        ctr.getMediator shouldBe a[Mediator]
        ctr.getGameState shouldBe a[GameState]
        ctr.getDeck shouldBe a[Vector[CardInterface]]
        ctr.getDiscCard() shouldBe a[None.type]
      "be able to fill a Board" in:
        val (afterBoard, afterDeck) = ctr.fillBoard(4, 3, ctr.state.deck)
        afterBoard shouldBe a[BoardInterface]
        afterDeck shouldBe a[DeckInterface]
      val oldState = ctr.state
      "execute save, undo and redo" in:
        ctr.save(oldState)
        ctr.undo()
        ctr.redo()
      "execute a move and update GAMESTATE" in:
        ctr.executeMove(oldState)
        ctr.uptGameState(oldState)
      "draw from Deck and DiscardPile" in:
        ctr.drawFromDeck()
        ctr.drawFromDisc()
        ctr.draw()
      "replaceCard on Board" in:
        ctr.replaceCard(0)
      "execute a swap between Deck and DiscardPile" in:
        ctr.discardDrawnCard()
      "execute a SwapHandler()" in:
        ctr.SwapHandler(0)
      "remove a Card From Disc" in:
        ctr.remove()
      "remove a Card From Deck" in:
        ctr.remove(1)
      "be able to turn Deck UpperCard" in:
        ctr.turnUpperCard shouldBe a[String]

      "execute a fullDeck()" in:
        val fullDeck = ctr.fullDeck()
        fullDeck.length shouldBe 150

      ctr.state = anotherState
      "be able to putCardOnBoard" in
        ctr.putCardOnBoard(0)
      "be able to turnBoardCard" in:
        ctr.turnBoardCard(0)
        ctr.turnBoardCard(1)
        ctr.turnBoardCard(2)
        ctr.turnBoardCard(3)
        ctr.turnBoardCard(4)
        ctr.turnBoardCard(5)
        ctr.turnBoardCard(6)
        ctr.turnBoardCard(7)
        ctr.turnBoardCard(8)
        ctr.turnBoardCard(9)
        ctr.turnBoardCard(10)
        ctr.turnBoardCard(11)
    }

    "A GAMESTATE" should:
      "have a drawnCard" in:
        state.drawnCard shouldBe None
      "be parsed state toString()" in:
        state.toString() shouldBe a[String]
      "be parsed anotherState toString()" in:
        anotherState.toString() shouldBe a[String]
=======

    val injector = Guice.createInjector(SkyjoModule(plCount))

    val ctr = injector.getInstance(classOf[ControllerInterface])

    ctr.setup()

    val gs:GameState = ctr.getGameState
    val card8 = ctr.toCard(8)
    "it is working, it" should {
      "covert toCard" in:
        val gs_card = ctr.isCard("")
        val invCard = ctr.toCard(100)
        val convDeck = ctr.toCard(ctr.getDeck)
        val convDisc = ctr.toCard(ctr.getDisc)
        val convNone = ctr.toCard(None)
        val true_toCard = ctr.toCard(1, turned = true)
        val false_toCard = ctr.toCard(1, turned = false)
      "get Mediator, GameState, Deck and Discard-Card" in:
        ctr.copy_state(ctr.getMementos,ctr.getBrds, ctr.getDeck,ctr.getDisc, 0,ctr.currState) shouldBe a[GameState]
        ctr.getMediator shouldBe a[Mediator]
        ctr.getGameState shouldBe a[GameState]
        ctr.getDeck shouldBe a[DeckInterface]
        ctr.getDisc shouldBe a[DiscardPileInterface]
        ctr.getDiscCard()
      "assert a new GameState" in:
        ctr.assertGameState(ctr.getGameState)
      "be able to fill a Board" in:
        val (afterBoard, afterDeck) = ctr.fillBoard(4, 3, ctr.getDeck)
        afterBoard shouldBe a[BoardInterface]
        afterDeck shouldBe a[DeckInterface]

      val reducibleBoard = Board(2,2, Vector(Vector(ctr.toCard(1).falseCopy, ctr.toCard(2)), Vector(ctr.toCard(3).falseCopy, ctr.toCard(2))))
      "get a reduced Board" in:
        ctr.reduceCurrentBoard()
        ctr.swapFromMem(ctr.toCard(1),0)
        // ctr.getReducedBrd(reducibleBoard) shouldBe a[(BoardInterface,Int,Int)]
      "hasdrawn is a Boolean" in:
        ctr.hasDrawn shouldBe a[Boolean]

      val mem: Memento = Memento(0,card8,0,card8,ctr.getDisc,false)
      "execute save" in:
        mem.toString() shouldBe a[String]
        val json_mem = mem.toJson
        ctr.save(mem)
      // "execute undo" in:
      //   ctr.undo()
      "execute redo" in:
        ctr.redo()
      val mem2 = mem.copy(fromDeck = 1)
      val ctr2 = ctr
      // val gs = ctr.copy(Vector.empty, Vector.empty, Deck(ctr), DiscardPile("Disc"), 0, State.BEGIN)
      val gs2 = ctr.copy_state(mems = Vector.empty, brds = Vector.empty, d = Deck(Vector.empty, "Deck"), disc = DiscardPile("Disc", false), idx = 0, currentState = State.BEGIN)
      "execute undo2" in:
        ctr2.currMemento.undoStack.push(mem)
        ctr2.undo()
        ctr2.currMemento.undoStack.push(mem2)
        ctr2.undo()
      "execute redo2" in:
        ctr2.currMemento.redoStack.push(mem)
        ctr2.redo()
        ctr2.currMemento.undoStack.push(mem2)
        ctr2.undo()
      "switch Deck and disc" in:
        val temp_deck = Deck(ctr.getDeckCards, ctr.turnUpperCard)
        ctr2.save(mem2)
        val another_gs = ctr2.switchDeckDisc(ctr2.getGameState, ctr2.getBrds(0), temp_deck, 0)
        val ctr3 = Controller(ctr2.getGameState.copy(deck = temp_deck ),0, injector.getInstance(classOf[ConcreteMediator]))
        ctr3.draw()
      "move to next player turn" in:
        ctr2.nextPlayer
      val mem3 = mem.copy(fromDeck = 2)
      "execute memento fromDeck == 2" in:
        ctr2.currMemento.redoStack.push(mem)
        ctr2.redo()
        ctr2.currMemento.undoStack.push(mem3)
        ctr2.undo()
        ctr2.currMemento.redoStack.push(mem)
        ctr2.redo()
        ctr2.currMemento.undoStack.push(mem3)
        ctr2.undo()


      "draw from Deck and DiscardPile" in:
        ctr.draw()
      "remove a Card From Disc" in:
        ctr.remove()
      "draw fromDisc" in:
        ctr.drawFromDisc(0)
      "remove a Card From Deck" in:
        ctr.remove(1)
      "be able to turn Deck UpperCard" in:
        ctr.turnUpperCard shouldBe a[String]

      "execute a fullDeck()" in:
        val fullDeck = ctr.fullDeck()
        fullDeck.length shouldBe 150

      //---------------------------- FILEIO -------------------------------//
      val boardPane = new Pane()
      val bv = new BoardView(ctr, boardPane)
      "can load and save Json" in:
        ctr.json_load(bv)
        ctr.json_save
        bv.syncBoard(reducibleBoard)
        // bv.update("") shouldBe true

      "can update a BoardView" in:
        // ----------------------CardView---------------------------//
        bv.manyCards.toString() shouldBe a[String]
        bv.manyCards.map(cv => cv.uptCardView)
        bv.BOARD_INIT()

      "can load and save Xml + upt BoardPane" in:
        ctr.xml_load(bv)
        ctr.xml_save
        // bv.syncController
        // bv.uptBoardPane(0,0)

      "can gui undo" in:
        ctr.save(mem)
        ctr.guiUndo(reducibleBoard, ctr.getDeck, ctr.getDisc, bv)
      "can gui redo" in:
        ctr.save(mem)
        ctr.undo()
        ctr.redo()
        ctr.guiRedo(reducibleBoard, ctr.getDeck, ctr.getDisc, bv)

      "others" in:
        ctr.guiPreviewDeckCard()
        ctr.guiConfirmDeckSwitch(0)
        ctr.guiConfirmDeckToDiscAndTurn(0)

        ctr.guiTurnBrdCard(0)

        ctr.guiSelectDisc()

        ctr.guiDeckToDisc()
    }

    "A GAMESTATE" should:
      "be parsed state toString()" in:
        gs.toString() shouldBe a[String]
      "convert into and from Xml" in:
        val xml_gs = gs.toXml
        val new_gs = gs.fromXml(xml_gs)
      "can Inject FileIO" in:
        val jsonIO = injector.instance[JsonImpl]
        // jsonIO.load("")

        val xmlIO = injector.instance[XmlImpl]
        // xmlIO.load("")

    "A State " should:
      val cs = gs.currentState
      "be parsed into String" in:
        cs.getStr shouldBe a[String]
      "iterate trough States" in:
        val mid_state = cs.nextState()
        val end_state = mid_state.nextState()
      "reset()" in:
        val r = cs.reset()
      "convert into (xml, json) and from (xml)" in:
        val xml_cs = cs.toXml
        val json_cs = cs.toJson
        val new_cs = cs.fromXml(xml_cs)
>>>>>>> origin/docker
}
