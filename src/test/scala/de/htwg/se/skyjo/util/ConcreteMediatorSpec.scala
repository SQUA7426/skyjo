package de.htwg.se.skyjo.util
import de.htwg.se.skyjo.util.{Mediator, ConcreteMediator, Colleague, Handler}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.Controller
import de.htwg.se.skyjo.model.BoardInterface
import de.htwg.se.skyjo.model.BoardImplementation.Board
import de.htwg.se.skyjo.model.BoardImplementation.fillBoard
import de.htwg.se.skyjo.model.DeckImplementation.Deck
import de.htwg.se.skyjo.model.DeckInterface
import de.htwg.se.skyjo.model.DiscardPileImplementation.DiscardPile
import de.htwg.se.skyjo.model.CardImplementation.Card
import de.htwg.se.skyjo.model.CardImplementation.{toCard, len, isCard}
import de.htwg.se.skyjo.model.GameState
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.enablers.Containing
import java.io.ByteArrayInputStream

class ConcreteMediatorSpec extends AnyWordSpec with Matchers {
  "A ConcreteMediator" should:
    val med = new ConcreteMediator()
    val tmpDeck: DeckInterface = Deck(med)
    val tBoard = new Board(med, 3, 4, fillBoard(med, 3, 4, tmpDeck)._1.getBoard)
    val b: Board = tBoard
    val deck: Deck = Deck(med)
    val disc: DiscardPile = new DiscardPile(med, "Disc")
    val plBoards: Vector[Board] = Vector(b)
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

    val state = GameState(plBoards, deck, disc, 0)

    val ctrl = new Controller(state)
    val h: SupportHandler = new SupportHandler(ctrl)
    "A Handler" should:
      "handle input 0" in {
        // val simulatedInput = "1\n1\n0\n"
        // val in = new ByteArrayInputStream(simulatedInput.getBytes())
        // Console.withIn(in) {
        h.handle("0", state)
        // }
      }
      "handle input 1" in {
        // val twoTimesTwoPlBoards = Array(fillBoard(med, 2, 2, deck)._1)
        // val simulatedInput = "1\n1\n3\n1\n1\n2\n1\n1\n2\n1\n1\n1\n1\n1\n0\n"
        // val in = new ByteArrayInputStream(simulatedInput.getBytes())
        // Console.withIn(in) {
        h.handle("1", state)
        // }
      }
      "be unable to handle unrecognized requests" in:
        h.handle("x", state) shouldBe None
}
