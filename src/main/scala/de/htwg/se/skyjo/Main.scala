package de.htwg.se.skyjo

import de.htwg.se.skyjo.model.{Board, Deck, DiscardPile, fullDeck}
import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.Controller
import de.htwg.se.skyjo.util.Memento
def main(args: Array[String]): Unit = {
  println("Enter a number of players:")
  // val plCount = readInt()
  val plCount = 2
  val Ctr = new Controller()
  var deck: Deck = new Deck(fullDeck()._1, fullDeck()._2)
  val disc: DiscardPile = new DiscardPile("Disc")
  val plBoards = {
    Array.fill(plCount)(new Board(2, 2, Vector())) // Empty Boards
    
    
  }
  //val t = new Tui(Ctr)
  val t = new Tui(Ctr)
  Ctr.gameLoop(plCount, plBoards, deck, disc)
}
