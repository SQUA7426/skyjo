package de.htwg.se 

import de.htwg.se.{Card,Deck,Hand,Board,DiscardPile}
import scala.io.StdIn.readLine
import scala.io.StdIn.readInt

def main(args: Array[String]): Unit = {
  println("Enter a number of players:")
  val plCount = readInt()
  val plBoards: Array[Board] = new Array(plCount)
  val plHands: Array[Hand] = new Array(plCount)
  val deck: Deck = new Deck(fullDeck()._1, fullDeck()._2)
  val disc: DiscardPile = new DiscardPile("Disc")
  for i <- 0 until plCount do
    val (vVC, d2) = fillBoard(4,3,deck)
    plBoards(i) = new Board(4,3,vVC)
    plHands(i) = new Hand("Hand")
    println(plBoards(i).turnBoardCard(i).turnBoardCard(i+1).toString() + "\n" + disc.toString() + " | " + d2.toString() + " | " + plHands(i).toString())
}
