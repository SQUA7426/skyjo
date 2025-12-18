package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.util.{Command, SupportCommand, ConcreteMediator}
import de.htwg.se.skyjo.controller.ControllerComponent.Controller
import de.htwg.se.skyjo.model.{Board, Deck, DiscardPile, Card, fillBoard}
import org.scalatest.matchers.should.Matchers

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.enablers.Containing
import java.io.ByteArrayInputStream

class CommandSpec extends AnyWordSpec with Matchers {
  "A Command" should {
    val med = new ConcreteMediator()
    val board = new Board(
      med,
      2,
      1,
      Vector(Vector(Card(med, 1).falseCopy(), Card(med, 2)))
    )
    val brdArr = Array(board)
    val deck = Deck(med)

    val disc = new DiscardPile(med, "Disc")

    // val twoTimesTwoPlBoards = Array(new Board(med,2,2,Vector(Vector(Card(med,1).falseCopy(),Card(med,2).trueCopy()),Vector(Card(med,3).trueCopy(),Card(med,4).trueCopy()))))
    val twoTimesTwoPlBoards = Array(fillBoard(med, 2, 2, deck)._1)

    val cont = new Controller(med, brdArr, deck, disc)

    val cCom = new SupportCommand(cont, cont.disBoards(0), deck, disc)
    // ---------------------INPUT -----------------------------------------//
    //GEHT
    "execute the undo cmd" in {
      // val twoTimesTwoPlBoards = Array(fillBoard(med, 2, 2, deck)._1)
      val simulatedInput = "1\n1\n3\n1\n1\n2\nundo\n1\n1\n1\n1\n1\n0\n"
      val in = new ByteArrayInputStream(simulatedInput.getBytes())
      Console.withIn(in) {
        val gl = cont.gameLoop(1, twoTimesTwoPlBoards, deck, disc)
      }
    }
    //GEHT
    "execute the redo cmd" in {
    //   val twoTimesTwoPlBoards = Array(fillBoard(med, 2, 2, deck)._1)
      val simulatedInput = "1\n1\n3\n0\n2\nredo\n1\n1\n1\n1\n1\n0\n"
      val in = new ByteArrayInputStream(simulatedInput.getBytes())
      Console.withIn(in) {
        val gl = cont.gameLoop(1, twoTimesTwoPlBoards, deck, disc,2)
      }
    }
    "execute the undo cmd2" in {
      // val twoTimesTwoPlBoards = Array(fillBoard(med, 2, 2, deck)._1)
      val simulatedInput = "1\n1\n3\n0\n2\nundo\n1\n1\n1\n1\n1\n0\n"
      val in = new ByteArrayInputStream(simulatedInput.getBytes())
      Console.withIn(in) {
        val gl = cont.gameLoop(1, twoTimesTwoPlBoards, deck, disc,2)
      }
    }
    //GEHT
    "execute the redo cmd2" in {
    //   val twoTimesTwoPlBoards = Array(fillBoard(med, 2, 2, deck)._1)
      val simulatedInput = "1\n1\n3\n1\n1\n2\nredo\n1\n1\n1\n1\n1\n0\n"
      val in = new ByteArrayInputStream(simulatedInput.getBytes())
      Console.withIn(in) {
        val gl = cont.gameLoop(1, twoTimesTwoPlBoards, deck, disc,2)
      }
    }


    "execute the help cmd" in:
      cCom.execute("help")
    // "execute the quit cmd" in:
    //   cCom.execute("quit") shouldBe None
    "not execute the x cmd" in:
      cCom.execute("x")
  }
}
