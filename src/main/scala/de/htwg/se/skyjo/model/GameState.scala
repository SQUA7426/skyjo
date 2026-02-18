package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.util.*
import scala.xml.Node

case class GameState(
    mementos: Vector[MoveCaretaker],
    boards: Vector[BoardInterface],
    deck: DeckInterface,
    disc: DiscardPileInterface,
    plIdx: Int,
    currentState: State
) {

  // FILEIO //

  def toXml: Node = {
    <gamestate>
      <mementos>
        {mementos.map(mc => mc.toXml)}
      </mementos>
      <boards>
        {boards.map(b => b.toXml)}
      </boards>
      <deck>
        {deck.toXml}
      </deck>
      <disc>
        {disc.toXml}
      </disc>
      <plIdx>{plIdx}</plIdx>
      <currentState>{currentState.toXml}</currentState>
    </gamestate>
  }
  def fromXml(xml: Node): GameState
}
