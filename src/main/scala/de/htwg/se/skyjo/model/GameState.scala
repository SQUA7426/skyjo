package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.util.*

case class GameState(
  med: Mediator,
  mementos: Vector[MoveCaretaker],
  boards: Vector[BoardInterface],
  deck: DeckInterface,
  disc: DiscardPileInterface,
  plIdx: Int,
  currentState: State
  )
  {}
