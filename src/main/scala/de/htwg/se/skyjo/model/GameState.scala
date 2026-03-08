package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{Deck, DiscardPile, Board,Card}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.Controller

import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.skyjo.SkyjoModule
import net.codingwell.scalaguice.InjectorExtensions.*

import scala.xml.{Node,NodeSeq}
import play.api.libs.json.{Json, JsObject}

case class GameState(
    mementos: Vector[MoveCaretaker],
    boards: Vector[BoardInterface],
    deck: DeckInterface,
    disc: DiscardPileInterface,
    plIdx: Int,
    currentState: State
) {

  // FILEIO //

  // XML //
  // def toJson: JsObject =
  //   Json.obj(
  //     "mementos" -> mementos.map(_.toJson),
  //     "boards" -> boards.map(_.toJson),
  //     "deck" -> deck.toJson,
  //     "disc" -> disc.toJson,
  //     "plIdx" -> plIdx,
  //     "currentState" -> currentState.toJson
  //   )

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
  private def stateFromXML(n: NodeSeq): State =
    val stateXml = {n \ "state"}
    val strXml = { stateXml \ "str"}.text.toString
    val preXml = { stateXml \ "pre"}.text.toString
    State.ASSERT(strXml, preXml)
  private def discFromXml(n: NodeSeq): DiscardPileInterface =
    val discXml = {n \ "discardpile"}
    val discPXml = {discXml \ "discpile"}.text
    val discTXml = {discXml \ "turned"}.text.toBoolean
    DiscardPile(discPXml, discTXml)

  private def deckFromXml(dn: NodeSeq): DeckInterface =
    val upper = {dn \ "uppercard"}.text
    val deckXml = {dn \ "deckcards"}.head
    val cardXml = deckXml.map(c => c \\ "card")
    val card = Card(0)
    Deck(cardXml.map(ns => card.fromXml(ns.head)).toVector,
      upper
    )

  def boardFromXml(xml: NodeSeq): BoardInterface =
    val brdXml = {xml \\ "brd"}.head \\ "row"
    val cardXml = brdXml.map(c => c \\ "card")
    Board(Node2Int(xml \ "xSize"),
          Node2Int(xml \ "ySize"),
          cardXml.map(rowXml => {
            rowXml.map(cXml => this.boards(plIdx).getBoard(0)(0).fromXml(cXml.head)).toVector
          }).toVector
        )

  private def Node2Int(ns: NodeSeq): Int =
    ns.head.text.replace(" ", "").toInt

  def fromXml(xml: Node): GameState = {

    val gsXml = { xml \ "gamestate"}.head
    val idx = {gsXml \ "plIdx"}.text.toInt
    val tempState = new GameState(Vector.empty, Vector.empty, null, null, 0, State.BEGIN)
    
    val injector = Guice.createInjector(SkyjoModule(boards.size))
    val med = injector.getInstance(classOf[ConcreteMediator])
    val ctr = new Controller(tempState, idx, med)
    val mc = MoveCaretaker(ctr)

    val memXml = { gsXml \ "mementos"}.head
    val mementosXml = memXml.map(mem => mc.fromXml(mem)).toVector
    val boardsXml = { gsXml \ "boards"}.head
    val brdsXml = boardsXml.map(b => boardFromXml(b)).toVector
    val deck = deckFromXml(gsXml \ "deck")
    val disc = discFromXml(gsXml \ "disc")

    val stateXml = stateFromXML(gsXml \ "currentState")
    GameState(mementosXml, brdsXml, deck, disc, idx, stateXml)
  }
}
