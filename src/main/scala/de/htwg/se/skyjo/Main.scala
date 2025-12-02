package de.htwg.se.skyjo

import de.htwg.se.skyjo.Model.{Board, Deck, DiscardPile, fullDeck}
import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.Controller
import de.htwg.se.skyjo.util.{ConcreteMediator,Mediator}

def main(args: Array[String]): Unit = {
  // println("Enter a number of players:")
  // val plCount = readInt()
  val plCount = 1
  val med = new ConcreteMediator()
  val Ctr = new Controller(med)
  val deck: Deck = Deck(med)
  val disc: DiscardPile = new DiscardPile(med,"Disc")
  val plBoards =
    Array.fill(plCount)(new Board(med, 2, 2, Vector()))
  val t = new Tui(Ctr)
  Ctr.gameLoop(plCount, plBoards, deck, disc)
  println("After finished")
  med.send(deck, "Card")
}
