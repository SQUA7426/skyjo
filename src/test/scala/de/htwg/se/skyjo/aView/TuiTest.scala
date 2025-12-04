package de.htwg.se.skyjo.aView

import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.model.{Board, Deck, DiscardPile, Card,fillBoard}
import de.htwg.se.skyjo.controller.ControllerComponent.Controller
import de.htwg.se.skyjo.model.fillBoard
import de.htwg.se.skyjo.util.ConcreteMediator

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import java.io.ByteArrayInputStream

class TuiTest extends AnyWordSpec with Matchers {
  "A Tui " when:
    val med = new ConcreteMediator
    val bTemp = Board(med)
    val b = bTemp._1
    val brdArr = Array(b)
    val d = bTemp._2
    val disc = DiscardPile(med,"-1")
    val ctrl = new Controller(med,brdArr,d,disc)
    val tui = Tui(ctrl)
    "an Input Request is done, it" should:
      "do an Input Request to the Board" in:
        tui.inputRequest(b, disc.toString()) shouldBe ("Which BoardCard [0-11] do you want to switch with -1?")
      "do an Input Request to the Deck" in:
        tui.inputRequestDeck(d.turnUpperCard().toString())
      "do print a Player when a turn begins" in:
        tui.turnOfPlayer(3)
      "announce if someone finished" in:
        tui.finishedConf()
}
