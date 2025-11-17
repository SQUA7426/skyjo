package de.htwg.se

import de.htwg.se.Card
import de.htwg.se.Deck

import scala.collection.immutable.Vector
import scala.util.Random
import scala.util.control._
import scala.collection.immutable.Seq
var xSize:Int = 0
var ySize:Int = 0
def fillBoard( d: Deck): Vector[Vector[Card]] =
  Vector.tabulate(ySize, xSize) { (ySize, xSize) =>
    {
      var ran: Int = d.upperCardInt()
      println(ran)
      while (d.cardsLeftOf(ran) > 0) do
        ran = d.upperCardInt()
        d.removeCardFromDeck(ran, d.deck)
      Card(ran)
    }
  }

class Board(x: Int, y: Int, board: Vector[Vector[Card]]) {
  ySize = y
  xSize = x
  val brd = board
  //var brd: Vector[Vector[Card]] =
    def str(): Unit = {
      var br = brd.map(t => t.toList).flatten
      for i <- 0 until ySize do
        Seq("+-----+ ", "|     | ").foreach(t => println(t * xSize))
        for j <- 0 until xSize do print(br(i * xSize + j))
        println()
        Seq("|     | ", "+-----+ ").foreach(t => println(t * xSize))
    }

  override def toString(): String = {
    val s1: Seq[String] = Seq("+-----+ ", "|     | ").map(t => t.repeat(xSize))
    val s2: Seq[String] = Seq("|     | ", "+-----+ ").map(t => t.repeat(xSize))
    val s3 = (s1 ++ s2).mkString
    s3
  }
}
