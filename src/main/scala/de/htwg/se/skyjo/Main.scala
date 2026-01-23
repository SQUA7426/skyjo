package de.htwg.se.skyjo

import de.htwg.se.skyjo.aView.Gui.Gui
import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.Controller
import de.htwg.se.skyjo.util.MoveCaretaker
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.{
  State,
  GameState,
  BoardInterface,
  CardInterface,
  DeckInterface,
  DiscardPileInterface
}
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{Deck, DiscardPile, Board, Card}
import scala.io.StdIn.readLine

def main(args: Array[String]): Unit = {
  val plCount = 1
  val med = new ConcreteMediator()

  // val tempState = new GameState(med, Vector.empty, null, null, 0, None)
  val tempState = new GameState(med, Vector.empty, Vector.empty, null, null, 0, State.BEGIN)
  val ctr = new Controller(tempState)

  println("Init Deck & Disc")
  val deck = new Deck(ctr.fullDeck(), ctr)
  val disc = new DiscardPile(ctr)

  println("Init MementoCs & Boards")
  val plMoveC = Vector.fill(plCount)(new MoveCaretaker(ctr))
  val plBoards = Vector.fill(plCount)(new Board(med, 4, 3, Vector.empty))

  println("updating Controllerstate")
  // ctr.state = new GameState(med, plBoards, deck, disc, 0, None)
  ctr.state = new GameState(med, plMoveC, plBoards, deck, disc, 0, State.BEGIN)
  ctr.state = ctr.state.copy(
    deck = Deck(ctr),
    disc = DiscardPile(ctr)
    )

  println(ctr)

  val t = new Tui(ctr)
  ctr.setup()
  println("input => g for GUI")
  // val choose = "g"
  val choose = readLine()
  if choose == "g" then
    Gui.init(ctr)
    Gui.main(args)
  else
    val t = new Tui(ctr)
    // println("GAMESTATE")
    // println(ctr.getGameState.toString())
    t.startGame

    println("After finished")
    val simpleCard = new Card(3, true, ctr)
    med.add(deck)
    med.add(disc)
    med.add(simpleCard)
    med.add(plBoards(0))
    med.send(deck, "REQUEST GET UPPERCARD")
    med.send(plBoards(0), "REQUEST PUT TO DISCARDPILE")
    med.send(disc, "REQUEST CARD FROM DECK")
}
