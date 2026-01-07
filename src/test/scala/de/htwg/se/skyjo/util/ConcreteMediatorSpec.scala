package de.htwg.se.skyjo.util
import de.htwg.se.skyjo.util.{Mediator, ConcreteMediator, Colleague, Handler}
import de.htwg.se.skyjo.controller.ControllerComponent.Controller
import de.htwg.se.skyjo.model.{Board, Deck, DiscardPile}
import de.htwg.se.skyjo.model.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.enablers.Containing
import java.io.ByteArrayInputStream

class ConcreteMediatorSpec extends AnyWordSpec with Matchers {
  "A ConcreteMediator" should:
    val med = new ConcreteMediator()
    val tBoard = Board(med)
    val b: Board = tBoard._1
    val deck: Deck = tBoard._2
    val disc: DiscardPile = new DiscardPile(med, "Disc")
    val plBoards: Array[Board] = Array(b)
    val simpleCard = new Card(med, 3, true)
    "add Colleagues" in:
      med.add(deck)
      med.add(disc)
      med.add(simpleCard)
      med.add(plBoards(0))
    "do it's request" in:
      med.requestCardFromDeck(deck)
      med.requestGetUpperCard(plBoards(0))
      med.requestPutToDisc(plBoards(0))
      med.requestRmUpperCard(disc)

      deck.send("REQUEST GET UPPERCARD")
      plBoards(0).send("REQUEST PUT TO DISCARDPILE")
      disc.send("REQUEST CARD FROM DECK")
      simpleCard.send("REQUEST FROM CARD")
    "remove a Colleague" in:
      med.remove(plBoards(0))

    val ctrl = new Controller(med, plBoards, deck, disc)
    val h: Handler = new DeckHandler(ctrl, b, deck, disc)
    "A Handler" should:
      "handle input 1" in {
        val simulatedInput = "1\n1\n0\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())
        Console.withIn(in) {
          h.handle("1")
        }
      }
      "handle input 2" in {
        val twoTimesTwoPlBoards = Array(fillBoard(med, 2, 2, deck)._1)
        val simulatedInput = "1\n1\n3\n1\n1\n2\n1\n1\n2\n1\n1\n1\n1\n1\n0\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())
        Console.withIn(in) {
          val gl = ctrl.gameLoop(1, twoTimesTwoPlBoards, deck, disc)
        }
      }
      "be unable to handle unrecognized requests" in:
        h.handle("x") shouldBe None
}
