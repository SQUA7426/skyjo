package de.htwg.se.skyjo.util
import de.htwg.se.skyjo.controller.ControllerComponent.Controller
import de.htwg.se.skyjo.model.{Board, Deck, DiscardPile}

trait Handler:
  val next: Handler
  def handle(request: String): Option[(Board, Deck, DiscardPile)]

class DiscHandler(
    ctrl: Controller,
    val b: Board,
    val d: Deck,
    val disc: DiscardPile
) extends Handler:
  override val next: Handler = DeckHandler(ctrl, b, d, disc)

  override def handle(request: String): Option[(Board, Deck, DiscardPile)] =
    if request.compareTo("0") == 0 then {
      println(s"DiscHandler handled request: ${request}");
      ctrl.takeFromDisc(b, d, disc)
    } else this.next.handle(request)

class DeckHandler(
    ctrl: Controller,
    val b: Board,
    val d: Deck,
    val disc: DiscardPile
) extends Handler:
  override val next: Handler = UndoHandler(ctrl, b, d, disc)

  override def handle(request: String): Option[(Board, Deck, DiscardPile)] =
    if request.compareTo("1") == 0 then {
      println(s"DeckHandler handled request: ${request}");
      Option(ctrl.takeFromDeck(b, d, disc))
    } else this.next.handle(request)

class UndoHandler(
    ctrl: Controller,
    val b: Board,
    val d: Deck,
    val disc: DiscardPile
) extends Handler:
  override val next: Handler = LastHandler()

  override def handle(request: String): Option[(Board, Deck, DiscardPile)] =
    if request.compareTo("2") == 0 then {
      println(s"UndoHandler handled request: ${request}")
      if (ctrl.mementostack.undoStack != null) {
        val mem: Memento = ctrl.mementostack.undoStack.pop()
        return Option(ctrl.mementostack.undo(mem, d, b, disc))
      }
      Option(b, d, disc)
    } else this.next.handle(request)

class LastHandler extends Handler:
  override val next: Handler = this

  override def handle(request: String): Option[(Board, Deck, DiscardPile)] = {
    println(s"The request: '${request}' arrived at the LastHandler"); None
  }

case class SupportHandler(
    ctrl: Controller,
    b: Board,
    d: Deck,
    disc: DiscardPile
):
  private val h = new DiscHandler(ctrl, b, d, disc)

  def handle(request: String): Option[(Board, Deck, DiscardPile)] =
    h.handle(request)
