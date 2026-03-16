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

import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.skyjo.SkyjoModule
import net.codingwell.scalaguice.InjectorExtensions.*

class ControllerTest extends AnyWordSpec with Matchers {
  "A Controller" when:
    val plCount = 1

    val injector = Guice.createInjector(SkyjoModule(plCount))

    val ctr = injector.getInstance(classOf[ControllerInterface])
    val gs_cp = ctr.copy()

    ctr.setup()

    val gs:GameState = ctr.getGameState
    val card8 = ctr.toCard(9)
    "it is working, it" should {
      "get Mediator, GameState, Deck and Discard-Card" in:
        ctr.getMediator shouldBe a[Mediator]
        ctr.getGameState shouldBe a[GameState]
        ctr.getDeck shouldBe a[DeckInterface]
        ctr.getDisc shouldBe a[DiscardPileInterface]
        ctr.getDiscCard()
      "be able to fill a Board" in:
        val (afterBoard, afterDeck) = ctr.fillBoard(4, 3, ctr.getDeck)
        afterBoard shouldBe a[BoardInterface]
        afterDeck shouldBe a[DeckInterface]
      "execute save" in:
        val mem: Memento = Memento(true,card8,0,card8,ctr.getDisc,false)
        ctr.save(mem)
        // "execute undo and redo" in:
        // val d = Deck(ctr.getDeck.remove(1), "Deck")
        // val di = DiscardPile(d.getCard.get.toString(), true)
        // ctr.undo()
        // ctr.redo()
      "draw from Deck and DiscardPile" in:
        ctr.draw()
      "remove a Card From Disc" in:
        ctr.remove()
      "remove a Card From Deck" in:
        ctr.remove(1)
      "be able to turn Deck UpperCard" in:
        ctr.turnUpperCard shouldBe a[String]

      "execute a fullDeck()" in:
        val fullDeck = ctr.fullDeck()
        fullDeck.length shouldBe 150
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
