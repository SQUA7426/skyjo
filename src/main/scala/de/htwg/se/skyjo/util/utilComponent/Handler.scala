package de.htwg.se.skyjo.util.utilComponent

import de.htwg.se.skyjo.util.{HandlerInterface, Memento}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.{BoardInterface, CardInterface, DeckInterface, DiscardPileInterface}
import scala.util.Try
import de.htwg.se.skyjo.model.State


class DiscHandler(ctrl: ControllerInterface, b: BoardInterface, d: DeckInterface, disc: DiscardPileInterface) extends HandlerInterface:
  override val next: HandlerInterface = DeckHandler(ctrl, b, d, disc)

  override def handle(request: String): Try[Unit] =
    if request == "0" && ctrl.currState == State.BEGIN then
      Try {
        println("DRAW FROM DISC")
        ctrl.drawFromDisc()
      }
    else next.handle(request)
class DeckHandler(ctrl: ControllerInterface, b: BoardInterface, d: DeckInterface, disc: DiscardPileInterface) extends HandlerInterface:
  override val next: HandlerInterface = LastHandler()

  override def handle(request: String): Try[Unit] =
    if request == "1"  && ctrl.currState == State.BEGIN then
      Try {
        println("DRAW FROM DECK")
        ctrl.drawFromDeck()
      }
    else next.handle(request)


class LastHandler extends HandlerInterface:
  override val next: HandlerInterface = this
  // override def handle(request: String): Option[(BoardInterface, DeckInterface, DiscardPileInterface)] =
  override def handle(request: String): Try[Unit] =
    Try {
      IllegalArgumentException(s"The request: '$request' arrived at the LastHandler")
    }
    // None

case class SupportHandler(
    ctrl: ControllerInterface,
    b: BoardInterface,
    d: DeckInterface,
    disc: DiscardPileInterface
):
  private val h = new DiscHandler(ctrl, b, d, disc)
  // def handle(request: String): Option[(BoardInterface, DeckInterface, DiscardPileInterface)] =
  def handle(request: String): Try[Unit] =
    h.handle(request)
