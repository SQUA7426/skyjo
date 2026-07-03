package de.htwg.se.skyjo.util
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.{
  GameState,
  CardInterface,
  BoardInterface,
  DiscardPileInterface,
  DeckInterface
}
import scala.util.Try

trait HandlerInterface:
  val next: HandlerInterface
  def handle(request: String, pos: Int): Try[GameState]

