package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
import de.htwg.se.skyjo.model.CardImplementation.*
import de.htwg.se.skyjo.model.DeckImplementation.*
import de.htwg.se.skyjo.model.BoardImplementation.*
import de.htwg.se.skyjo.model.DiscardPileImplementation.*
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.GameState
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.enablers.Containing
import java.io.ByteArrayInputStream

class ConcreteMediatorSpec extends AnyWordSpec with Matchers {
  "A ConcreteMediator" should:
    val plCount = 1
    val med = new ConcreteMediator()

    val tempState = new GameState(med, Vector.empty, null, null, 0, None)
    val ctr: Controller = new Controller(tempState)

    val deck = new Deck(ctr.fullDeck(), ctr)
    val disc = new DiscardPile(ctr)

    val plBoards = Vector.fill(plCount)(new Board(med, 4, 3, Vector.empty))

    ctr.state = new GameState(med, plBoards, deck, disc, 0, Some(Card(9, ctr)))
    ctr.setup()
    val simpleCard: Card = new Card(3, true, ctr)
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

    val h: SupportHandler = new SupportHandler(ctr)
    "A Handler" should:
      "handle input 0" in {
        h.handle("0", ctr.state)
      }
      "handle input 1" in {
        h.handle("1", ctr.state)
      }
      "handle input undo" in {
        h.handle("undo", ctr.state)
      }
      "handle input redo" in {
        h.handle("redo", ctr.state)
      }
      "handle input s for putting DeckCard onto Discard" in {
        h.handle("s", ctr.state)
      }

      "be unable to handle unrecognized requests" in:
        h.handle("x", ctr.state) shouldBe None
}
