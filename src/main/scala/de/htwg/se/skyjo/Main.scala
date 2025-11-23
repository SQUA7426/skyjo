package de.htwg.se.skyjo

import de.htwg.se.skyjo.model.{Deck, Board, DiscardPile, fullDeck}
import de.htwg.se.skyjo.aView.Tui

def main(args: Array[String]): Unit = {
  println("Enter a number of players:")
  // val plCount = readInt()
  val plCount = 2
  val deck: Deck = new Deck(fullDeck()._1, fullDeck()._2)
  val disc: DiscardPile = new DiscardPile("Disc")
  val plBoards =
    Array.fill(plCount)(new Board(2, 2, Vector())) // Empty Boards
  val t = new Tui()
  t.gameLoop(plCount, plBoards, deck, disc)
}
