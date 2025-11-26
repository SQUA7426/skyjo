package de.htwg.se.skyjo.aView

import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.model.{Board, Deck, DiscardPile, Card, fillDeck, fillBoard}
import de.htwg.se.skyjo.controller.ControllerComponent.Controller

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import java.io.ByteArrayInputStream
import de.htwg.se.skyjo.model.fillBoard

class TuiTest extends AnyWordSpec with Matchers {
  "A Tui " when:
    val ctrl = new Controller()
    val tui = Tui(ctrl)
    val d = Deck(fillDeck(Seq.empty[Card]), "Deck")
    val b = fillBoard(4,3,d)._1
    val disc = DiscardPile("-1")
    "an Input Request is done, it" should:
      "do an Input Request to the Board" in:
        tui.inputRequest(b, disc.toString()) shouldBe ("Which BoardCard [0-11] do you want to switch with -1?")
      "do an Input Request to the Deck" in:
        tui.inputRequestDeck(d.turnUpperCard().toString())
      "do an Input Request to the Board, when turning a Card" in:
        tui.cardTurnRq(b) shouldBe ("Which BoardCard [0-11] do you want to turn around?")
      "do print a Player when a turn begins" in:
        tui.turnOfPlayer(3)
      "announce if someone finished" in:
        tui.finishedConf()
      "printout a turn the right way" in:
        val simulatedInput = "5\n0\n22\n1\n"
          val in = new ByteArrayInputStream(simulatedInput.getBytes())

          Console.withIn(in) {
            val (afterBoards, afterDeck, afterDisc) = tui.turn(b,d,disc)
            afterBoards shouldBe a[Board]
          }
}
