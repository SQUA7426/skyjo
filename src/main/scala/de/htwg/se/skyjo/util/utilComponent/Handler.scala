package de.htwg.se.skyjo.util.utilComponent

import de.htwg.se.skyjo.util.{HandlerInterface, Memento}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.{BoardInterface, CardInterface, DeckInterface, DiscardPileInterface, GameState, State}
import scala.util.Try
import de.htwg.se.skyjo.model.modelInterfaceImplementation.DiscardPile


class DiscHandler(ctrl: ControllerInterface, b: BoardInterface, d: DeckInterface, disc: DiscardPileInterface) extends HandlerInterface:
  override val next: HandlerInterface = DeckHandler(ctrl, b, d, disc)

  override def handle(request: String, pos:Int): Try[GameState] =
    if request == "0" && ctrl.currState == State.BEGIN then
      Try {
        println("DRAW FROM DISC")
        ctrl.drawFromDisc(pos)
      }
    else next.handle(request, pos)
class DeckHandler(ctrl: ControllerInterface, b: BoardInterface, d: DeckInterface, disc: DiscardPileInterface) extends HandlerInterface:
  override val next: HandlerInterface = SwitchHandler(ctrl, b, d, disc)

  override def handle(request: String, pos:Int): Try[GameState] =
    if request == "1"  && ctrl.currState == State.BEGIN then
      Try {
        println("DRAW FROM DECK")
        ctrl.drawFromDeck(pos)
      }
    else next.handle(request, pos)


class SwitchHandler(ctrl: ControllerInterface, b: BoardInterface, d: DeckInterface, disc: DiscardPileInterface) extends HandlerInterface:
  override val next: HandlerInterface = LastHandler(ctrl)

  override def handle(request: String, pos:Int): Try[GameState] =
    if request == "s" && ctrl.currState == State.BEGIN then
      Try {
        println("Switch")
        val (swDisc, swDeck) = ctrl.getDisc.putToDiscardPile(ctrl.getDeck)
        val tmpMem = Memento(fromDeck = true, takenCard = swDisc.getDiscCard().get, boardIndex = 0, replacedCard = ctrl.getDiscCard().get, ctrl.getDisc, ctrl.getDisc.isTurned)

        ctrl.save(tmpMem)

        val newGameState = ctrl.getGameState.copy(
          deck = swDeck,
          disc = new DiscardPile(ctrl, swDisc.toString()),
          currentState = ctrl.currState.nextState() // END-STATE
          )
        newGameState
        // ctrl.assertGameState(newGameState)
      }
    else next.handle(request, pos)



class LastHandler(ctrl: ControllerInterface) extends HandlerInterface:
  override val next: HandlerInterface = this
  // override def handle(request: String, pos:Int): Option[(BoardInterface, DeckInterface, DiscardPileInterface)] =
  override def handle(request: String, pos:Int): Try[GameState] =
    Try {
      ctrl.getGameState
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
  def handle(request: String, pos: Int): Try[GameState] =
    h.handle(request, pos)
