package de.htwg.se

import de.htwg.se.Card
import de.htwg.se.Deck

import scala.collection.immutable.Vector
import scala.util.Random
import scala.util.control._
import scala.collection.immutable.Seq

def fillBoard(xSize: Int, ySize: Int, d: Deck): Vector[Vector[Card]] =
  if (d.deck.size == 0) then
    val deck2 = fillDeck(Seq.empty[Card])
    fillBoard(4,3, Deck(fillDeck(Seq.empty[Card]), "Deck"))
  else {
    def drawField(deck: Deck): (Card, Deck) = {
      val turnedDeck =
        if (deck.upperCard == "Deck") Deck(deck.deck, deck.turnUpperCard())
        else deck

      val topCard = turnedDeck.getUpperCard()
      val newDeck =
        Deck(turnedDeck.remove(1), "Deck")
      (topCard, newDeck)
    }
    def fillRow(deck: Deck, n: Int): (Vector[Card], Deck) =
      if (n == 0) (Vector.empty, deck)
      else {
        val (field, nextDeck) = drawField(deck)
        val (rest, finalDeck) = fillRow(nextDeck, n - 1)
        (field +: rest, finalDeck)
      }
    def fillRows(deck: Deck, n: Int): (Vector[Vector[Card]], Deck) =
      if (n == 0) (Vector.empty, deck)
      else {
        val (row, nextDeck) = fillRow(deck, xSize)
        val (rows, finalDeck) = fillRows(nextDeck, n - 1)
        (row +: rows, finalDeck)
      }
    val (board, _) = fillRows(d, ySize)
    board
  }

case class Board(x: Int, y: Int, board: Vector[Vector[Card]]) {
  val ySize = y
  val xSize = x
  val brd: Vector[Vector[Card]]= board

  override def toString(): String = brd.flatten.toSeq.map(t => s" ${t} |").mkString
}
