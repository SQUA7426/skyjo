package de.htwg.se.skyjo.util.utilComponent

import de.htwg.se.skyjo.util.{HandlerInterface, Memento}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.{BoardInterface, CardInterface, DeckInterface, DiscardPileInterface}
import scala.util.Try


// extension [Unit](opt: Option[Unit]) def toTry: Try[Unit] = Try(opt.get)


class DiscHandler(ctrl: ControllerInterface, b: BoardInterface, d: DeckInterface, disc: DiscardPileInterface) extends HandlerInterface:
  override val next: HandlerInterface = DeckHandler(ctrl, b, d, disc)
  // override def handle(request: String): Option[(BoardInterface, DeckInterface, DiscardPileInterface)] =
    // if request == "0" then ctrl.takeFromDisc(b, d, disc).orElse(next.handle(request))
    // else next.handle(request)
  override def handle(request: String): Try[Unit] =
    if request == "0" then
      Try {
        ctrl.drawFromDisc()
      }
    else next.handle(request)
class DeckHandler(ctrl: ControllerInterface, b: BoardInterface, d: DeckInterface, disc: DiscardPileInterface) extends HandlerInterface:
  override val next: HandlerInterface = UndoHandler(ctrl, b, d, disc)
  // override def handle(request: String): Option[(BoardInterface, DeckInterface, DiscardPileInterface)] = {
  //   if request == "1" then ctrl.takeFromDeck()
  //   // else next.handle(request)
  // }
  override def handle(request: String): Try[Unit] =
    if request == "1" then
      Try {
        ctrl.drawFromDeck()
      }
    else next.handle(request)

class UndoHandler(
    ctrl: ControllerInterface,
    val b: BoardInterface,
    val d: DeckInterface,
    val disc: DiscardPileInterface
) extends HandlerInterface:
  override val next: HandlerInterface = LastHandler()
//
//   override def handle(request: String): Option[(BoardInterface, DeckInterface, DiscardPileInterface)] = {
  override def handle(request: String): Try[Unit] = Try {
    println(request.compareTo("2") == 0)
    if request.compareTo("2") == 0 then {
      println(s"UndoHandler handled request: ${request}")
      if (ctrl.currMemento.undoStack(ctrl.getPlIdx) != null) {
        val mem: Memento = ctrl.getMementos(ctrl.getPlIdx).undoStack.pop()
        // Some(ctrl.currMemento.undo(mem, d, b, disc)).getOrElse(Option(b,d,disc))
        ctrl.currMemento.undo(mem, d, b, disc)
      }
      else this.next.handle(request)
      // else Some(b, d, disc)
    } else this.next.handle(request)
  }

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
