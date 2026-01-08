package de.htwg.se.skyjo.util
import de.htwg.se.skyjo.controller.ControllerComponent.*
import de.htwg.se.skyjo.model.DeckImplementation.*
import de.htwg.se.skyjo.model.DiscardPileImplementation.*
import de.htwg.se.skyjo.model.BoardImplementation.*
import scala.collection.mutable.Stack
import de.htwg.se.skyjo.model.GameState
trait Handler {
  val next: Option[Handler]
  def handle(request: String, state: GameState): Option[GameState]
}

class UndoHandler(ctrl: ControllerInterface, override val next: Option[Handler]) extends Handler {
  override def handle(request: String, state: GameState): Option[GameState] =
    if (request == "undo") { ctrl.undo(); Some(ctrl.getGameState) }
    else next.flatMap(_.handle(request, state))
}

class FlipHandler(ctrl: ControllerInterface, override val next: Option[Handler]) extends Handler {
  override def handle(request: String, state: GameState): Option[GameState] =
    if (state.isFlippingPhase && request.matches("\\d+")) {
      ctrl.turnBoardCard(request.toInt)
      Some(ctrl.getGameState)
    } else next.flatMap(_.handle(request, state))
}

class DiscardActionHandler(ctrl: ControllerInterface, override val next: Option[Handler]) extends Handler {
  override def handle(request: String, state: GameState): Option[GameState] =
    if (request == "s" && state.drawnCard.isDefined) {
      ctrl.discardDrawnCard()
      Some(ctrl.getGameState)
    } else next.flatMap(_.handle(request, state))
}

class SwapHandler(ctrl: ControllerInterface, override val next: Option[Handler]) extends Handler {
  override def handle(request: String, state: GameState): Option[GameState] =
    if (state.drawnCard.isDefined && request.matches("\\d+")) {
      ctrl.replaceCard(request.toInt)
      Some(ctrl.getGameState)
    } else next.flatMap(_.handle(request, state))
}

class DiscHandler(ctrl: ControllerInterface, override val next: Option[Handler]) extends Handler {
  override def handle(request: String, state: GameState): Option[GameState] =
    if (request == "0" && state.drawnCard.isEmpty && !state.isFlippingPhase) {
      ctrl.drawFromDisc()
      Some(ctrl.getGameState)
    } else next.flatMap(_.handle(request, state))
}

class DeckHandler(ctrl: ControllerInterface, override val next: Option[Handler]) extends Handler {
  override def handle(request: String, state: GameState): Option[GameState] =
    if (request == "1" && state.drawnCard.isEmpty && !state.isFlippingPhase) {
      ctrl.drawFromDeck()
      Some(ctrl.getGameState)
    } else next.flatMap(_.handle(request, state))
}

class LastHandler extends Handler {
  override val next: Option[Handler] = None
  override def handle(request: String, state: GameState): Option[GameState] = {
    println(s"Input '$request' not allowed in this phase.")
    None
  }
}

case class SupportHandler(ctrl: ControllerInterface) {
  private val chain = new UndoHandler(ctrl, 
    Some(new FlipHandler(ctrl, 
      Some(new DiscardActionHandler(ctrl, 
        Some(new SwapHandler(ctrl, 
          Some(new DiscHandler(ctrl, 
            Some(new DeckHandler(ctrl, 
              Some(new LastHandler())
            ))
          ))
        ))
      ))
    ))
  )

  def handle(request: String, state: GameState): Option[GameState] = chain.handle(request, state)
}
