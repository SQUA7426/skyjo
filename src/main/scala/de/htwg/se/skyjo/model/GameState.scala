package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{
  Deck,
  DiscardPile,
  Board,
  Card
}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.Controller
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface

import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.skyjo.SkyjoModule
import net.codingwell.scalaguice.InjectorExtensions.*

import scala.xml.{Node, NodeSeq}
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

  def fromXml(gsXml: Node): GameState = {

    // println(f"gs: ${gsXml}")
    val idx = { gsXml \ "plIdx" }.text.toInt
    // println(f"gs_idx: ${idx}")
    val tempState =
      new GameState(Vector.empty, Vector.empty, null, null, 0, State.BEGIN)
    // println(f"gs_tempState: ${tempState}")

    val injector = Guice.createInjector(SkyjoModule(boards.size))
    val med = injector.getInstance(classOf[ConcreteMediator])
    val ctr = new Controller(tempState, idx, med)
    // println(f"gs_ctr:) ${ctr}")
    val mc = MoveCaretaker(ctr)
    val (tmp_board, tmp_deck) = Board(ctr)
    val tmp_disc = DiscardPile("Disc", false)

    val memXml = { gsXml \ "mementos" }.head
    val mementosXml = memXml.map(mem => mc.fromXml(mem)).toVector
    // println(f"mementosXml: ${mementosXml.foreach(_.toString())}")
    val boardsXml = { gsXml \ "boards" \\ "board" }
    val brdsXml: Vector[BoardInterface] =
      boardsXml.map(b => tmp_board.fromXml(ctr, b)).toVector
    // println(brdsXml.foreach(_.toString))

    val dd = tmp_deck.fromXml(ctr, (gsXml \ "deck" \ "deck").head)
    // println(f"deck: ${dd.getDeckCards}")
    // val deckXml = tmp_deck
    val deckXml = dd

    // println(f"deckXml: ${deckXml.getDeckCards}")

    val discXml = tmp_disc.fromXml((gsXml \ "disc").head)
    // println(f"discXml: ${discXml}")

    val stateXml = currentState.fromXml((gsXml \ "currentState").head)
    // println(f"stateXml: ${stateXml}")
    GameState(mementosXml, brdsXml, deckXml, discXml, idx, stateXml)
  }
}
