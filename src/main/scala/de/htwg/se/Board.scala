package de.htwg.se

import de.htwg.se.Card
import de.htwg.se.Deck

import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import scala.util.control._
import scala.collection.mutable.Seq

abstract class Board {
  val ySize = 3
  val xSize = 4
  var brd: ArrayBuffer[ArrayBuffer[Card]] = new ArrayBuffer(ySize)

  def clearBoard(): Unit = for i <- 0 until ySize do brd(i).clear

  def fillBoard(d: Deck): Unit = {
    for i <- 0 until ySize do
      var r: ArrayBuffer[Card] = new ArrayBuffer(xSize)
      for j <- 0 until xSize do
        var ran: Int = Random.between(-2, 12)
        while d.cardsLeftOf(ran) == 0 do ran = Random.between(-2, 12)
        r.addOne(d.takeCard(ran))
      brd.addOne(r)
  }

  def str(): Unit = {
    var br = brd.map(t => t.toList).flatten
    for i <- 0 until ySize do
      Seq("+-----+ ", "|     | ").foreach(t => println(t * xSize))
      for j <- 0 until xSize do print(br(i * xSize + j))
      println()
      Seq("|     | ", "+-----+ ").foreach(t => println(t * xSize))
  }

  override def toString(): String = {
    str()
    "\n"
  }
}

class Hand {
  private var handCard: String = "Hand"
  override def toString(): String = if handCard.size==4 then s"|${handCard}| " else s"${handCard}"

  def takeFromDeck(d: Deck): Unit = {
    if d.upperCard.compareTo("Deck") == 0 then d.turnUpperCard()
    handCard = d.upperCard
    d.turnUpperCard()
  }
}
