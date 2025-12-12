package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.util.{Command,SupportCommand,ConcreteMediator}
import de.htwg.se.skyjo.controller.ControllerComponent.Controller
import de.htwg.se.skyjo.model.{Board,Deck,DiscardPile}
import org.scalatest.matchers.should.Matchers

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.enablers.Containing

class CommandSpec extends AnyWordSpec with Matchers {
  "A Command" should {
    val med = new ConcreteMediator()
    val board = Board(med)._1
    val brdArr = Array(board)
    val deck = Deck(med)
    val disc = new DiscardPile(med,"Disc")
    val cont = new Controller(med,brdArr,deck,disc)
    val cCom = new SupportCommand(cont,board,deck,disc)
    "execute the undo cmd" in:
      cCom.execute("undo")
    "execute the redo cmd" in:
      cCom.execute("redo")
    "execute the help cmd" in:
      cCom.execute("help")
    // "execute the quit cmd" in:
    //   cCom.execute("quit")
    "not execute the x cmd" in:
      cCom.execute("x")
  }
}

