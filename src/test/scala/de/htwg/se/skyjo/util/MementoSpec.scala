package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.util.{Mediator,ConcreteMediator,Colleague,Handler, MoveCaretaker,Memento}
import de.htwg.se.skyjo.controller.ControllerComponent.Controller
import de.htwg.se.skyjo.model.{Board,Deck,DiscardPile,Card}
import de.htwg.se.skyjo.model.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.enablers.Containing

class MementoSpec extends AnyWordSpec with Matchers {
  "A MoveCaretaker" should {
    val med = new ConcreteMediator
    val disc = new DiscardPile(med,"4")
    val tempB = Board(med)
    val b = tempB._1
    val deck = tempB._2
    val c1 = Card(med,1)
    val c2 = Card(med,2)
    val mem = new Memento(true,c1,1,c2,disc, false)
    val mc = new MoveCaretaker(med)
    mc.save(mem)
    "conv toString()" in:
      mc.setTrue()
      mc.memAct shouldBe true
      mc.setFalse()
      mc.memAct shouldBe false
      mc.toString() shouldBe a[String]
      mc.checkMemAct() shouldBe mc.memAct
      mc.getNewDeck() shouldBe a[Deck]
    "undo" in:
      mc.undo(mem,deck,b,disc) match {
        case Some(undoB,undoD,undoDi) => {
          undoB shouldBe a[Board]
          undoD shouldBe a[Deck]
          undoDi shouldBe a[DiscardPile]
        }
        case _ => None
      }

    "redo" in:
      mc.redo(mem,deck,b,disc) match {
        case Some(undoB,undoD,undoDi) => {
          undoB shouldBe a[Board]
          undoD shouldBe a[Deck]
          undoDi shouldBe a[DiscardPile]
        }
        case _ => None
      }
  }
}
