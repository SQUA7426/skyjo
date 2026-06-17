package de.htwg.se.skyjo.controller.ControllerComponent

import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{Deck, DiscardPile, Board, Card}
import de.htwg.se.skyjo.util.{Mediator, Memento, MoveCaretaker}
import de.htwg.se.skyjo.model.{GameState, DeckInterface, CardInterface, BoardInterface, DiscardPileInterface}
import de.htwg.se.skyjo.fileIoComponent.fileIoJsonImpl.JsonImpl
import de.htwg.se.skyjo.fileIoComponent.fileIoXmlImpl.XmlImpl

import scala.io.StdIn.{readInt, readLine}
import scala.util.Random
import java.io.ByteArrayInputStream
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scalafx.scene.layout.Pane
import de.htwg.se.skyjo.aView.Gui.{BoardView, fontname}

import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.skyjo.SkyjoModule
import net.codingwell.scalaguice.InjectorExtensions.*
import de.htwg.se.skyjo.util.ConcreteMediator

class ControllerTest extends AnyWordSpec with Matchers {
  "A Controller" when:
    val plCount = 1

    val injector = Guice.createInjector(SkyjoModule(plCount))

    val ctr = injector.getInstance(classOf[ControllerInterface])

    ctr.setup()

    val gs:GameState = ctr.getGameState
    val card8 = ctr.toCard(8)
    "it is working, it" should {
      "covert toCard" in:
        ctr.isCard(gs) shouldBe false
        val invCard = ctr.toCard(100)
        val convDeck = ctr.toCard(ctr.getDeck)
        val convDisc = ctr.toCard(ctr.getDisc)
        val convNone = ctr.toCard(None)
      "get Mediator, GameState, Deck and Discard-Card" in:
        ctr.copy(ctr.getMementos,ctr.getBrds, ctr.getDeck,ctr.getDisc, 0,ctr.currState) shouldBe a[GameState]
        ctr.getMediator shouldBe a[Mediator]
        ctr.getGameState shouldBe a[GameState]
        ctr.getDeck shouldBe a[DeckInterface]
        ctr.getDisc shouldBe a[DiscardPileInterface]
        ctr.getDiscCard()
      "assert a new GameState" in:
        ctr.assertGameState(gs)
      "be able to fill a Board" in:
        val (afterBoard, afterDeck) = ctr.fillBoard(4, 3, ctr.getDeck)
        afterBoard shouldBe a[BoardInterface]
        afterDeck shouldBe a[DeckInterface]

      val reducibleBoard = Board(2,2, Vector(Vector(ctr.toCard(1).falseCopy, ctr.toCard(2)), Vector(ctr.toCard(3).falseCopy, ctr.toCard(2))))
      "get a reduced Board" in:
        ctr.getReducedBrd(reducibleBoard) shouldBe a[(BoardInterface,Int,Int)]
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
        bv.update("") shouldBe true

      "can update a BoardView" in:
        // ----------------------CardView---------------------------//
        bv.manyCards.toString() shouldBe a[String]
        bv.manyCards.map(cv => cv.uptCardView)
        bv.BOARD_INIT()

      "can load and save Xml + upt BoardPane" in:
        ctr.xml_load(bv)
        ctr.xml_save
        bv.syncController
        bv.uptBoardPane(0,0)

      "can gui undo" in:
        ctr.save(mem)
        ctr.guiUndo(reducibleBoard, ctr.getDeck, ctr.getDisc, bv)
      "can gui redo" in:
        ctr.save(mem)
        ctr.undo()
        ctr.redo()
        ctr.guiRedo(reducibleBoard, ctr.getDeck, ctr.getDisc, bv)
    }

    "A GAMESTATE" should:
      "be parsed state toString()" in:
        gs.toString() shouldBe a[String]
      "convert into and from Xml" in:
        val xml_gs = gs.toXml
        val new_gs = gs.fromXml(xml_gs)
      "can Inject FileIO" in:
        val jsonIO = injector.instance[JsonImpl]
        val xmlIO = injector.instance[XmlImpl]

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
}
