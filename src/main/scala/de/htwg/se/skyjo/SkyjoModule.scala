package  de.htwg.se.skyjo

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import de.htwg.se.skyjo.model.{BoardInterface, CardInterface, DeckInterface, DiscardPileInterface, GameState}
import de.htwg.se.skyjo.model.modelInterfaceImplementation
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface

import de.htwg.se.skyjo.util.Mediator
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.Controller

object SkyjoModule extends AbstractModule with ScalaModule:
  override def configure(): Unit =
    // ----------- BINDINGS ----------------- //
    // CONTROLLER-BINDING //
    bind[(GameState) => ControllerInterface]
      .toInstance((initState) => Controller(initState))

    // BOARD-BINDING //
    bind[(Mediator, Int, Int, Vector[Vector[CardInterface]]) => BoardInterface]
      .toInstance((med, cols, rows, cardBrd) => modelInterfaceImplementation.Board(med,cols,rows,cardBrd))
    // ALT-BOARD-BINDING //
    bind[ControllerInterface => (BoardInterface, DeckInterface)]
      .toInstance(ctrl => modelInterfaceImplementation.Board(ctrl))

    // CARD-BINDING //
    bind[(Int, Boolean, ControllerInterface) => CardInterface]
      .toInstance((value, turned, ctrl) => modelInterfaceImplementation.Card(value, turned, ctrl))
    // ALT-CARD-BINDING //
    bind[(Int, ControllerInterface) => CardInterface]
      .toInstance((value, ctrl) => modelInterfaceImplementation.Card(value, ctrl))

    // DECK //
    bind[(Vector[CardInterface], ControllerInterface, String) => DeckInterface]
      .toInstance((cardDeck, ctrl, headCard) => modelInterfaceImplementation.Deck(cardDeck, ctrl, headCard))
    // ALT-DECK-BINDING //
    bind[(ControllerInterface) => DeckInterface]
      .toInstance((ctrl) => modelInterfaceImplementation.Deck(ctrl))

    // DISCARDPILE //
    bind[(ControllerInterface, String) => DiscardPileInterface]
      .toInstance((ctrl, strDisc) => modelInterfaceImplementation.DiscardPile(ctrl, strDisc))
