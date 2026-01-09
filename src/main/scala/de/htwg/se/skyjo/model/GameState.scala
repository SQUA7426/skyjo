package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.model.{BoardInterface,CardInterface, DeckInterface, DiscardPileInterface}
import de.htwg.se.skyjo.util.*

enum State(str: String = "BEGIN", var pre: String = "BOARD") {
  def nextState(): State = {
    if str == "BEGIN" then State.MID
    else State.END
  }
  case BEGIN extends State()
  case MID extends State("MID")
  case END extends State("END")
  def reset(): State = BEGIN
}

case class GameState (
  med: Mediator,
  boards: Vector[BoardInterface],
  deck: DeckInterface,
  disc: DiscardPileInterface,
  playerIdx: Int,
  drawnCard: Option[CardInterface] = None,
  isFlippingPhase: Boolean = false,
  currentState: State = State.BEGIN
) {
  override def toString(): String = {
    val brds = boards.foreach(bi => bi.toString())
    val de = deck.toString()
    val di = disc.toString()
    val card = drawnCard match {
      case Some(ci) => ci.toString()
      case None => "None"
    }
    val str = s"boards\n$brds\ndeck:\n$de\ndisc\n$di\nplayerIdx: $playerIdx\ndrawnCard: $card\nisFlippingPhase: $isFlippingPhase\n"
    str
  }
}
