package de.htwg.se

import de.htwg.se.Card
import de.htwg.se.Deck

import scala.collection.immutable.Vector
import scala.util.Random
import scala.util.control._
import scala.collection.immutable.Seq

def fillBoard(xSize: Int, ySize: Int, d: Deck): Vector[Vector[Card]] =
  Vector.tabulate(ySize, xSize) { (y, x) =>
    {
      var ran: Int = Random.between(-2, 12)
      while (d.cardsLeftOf(ran) > 0) do ran = Random.between(-2, 12)
      d.removeCardFromDeck(ran, d.deck)
      Card(ran)
    }
  }

class Board(x: Int, y: Int, board: Vector[Vector[Card]]) {
  val ySize = y
  val xSize = x
  val brd = board
  // var brd: Vector[Vector[Card]] =
  // def str(): Unit = {
  //   var br = brd.map(t => t.toList).flatten
  //   for i <- 0 until ySize do
  //     Seq("+-----+ ", "|     | ").foreach(t => println(t * xSize))
  //     for j <- 0 until xSize do print(br(i * xSize + j))
  //     println()
  //     Seq("|     | ", "+-----+ ").foreach(t => println(t * xSize))
  // }

  override def toString(): String = {
    val s1: Seq[String] = Seq("+-----+ ", "|     | ").map(t => t.repeat(xSize))
    val s2: Seq[String] = Seq("|     | ", "+-----+ ").map(t => t.repeat(xSize))
    val s3 = (s1 ++ s2).mkString
    s3
  }
}
