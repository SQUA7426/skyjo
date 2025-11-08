package de.htwg.se

import de.htwg.se.{Card, Hand, Deck}

import scala.collection.immutable.Vector
import scala.util.Random
import scala.util.control._
import scala.collection.immutable.Seq

def fillBoard(xSize: Int, ySize: Int, d: Deck): (Vector[Vector[Card]], Deck) =
  if (d.deck.size == 0) then
    val deck2 = fillDeck(Seq.empty[Card])
    fillBoard(4, 3, Deck(fillDeck(Seq.empty[Card]), "Deck"))
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
    val turnedBrd: Vector[Vector[Card]] = board.zipWithIndex.map {
      case (vectorRow, vectorNum) =>
        vectorRow.zipWithIndex.map { case (cCard, idx) => cCard.falseCopy() }
    }
    (turnedBrd, new Deck(d.remove(xSize*ySize), "Deck"))
  }

case class Board(
    val xSize: Int,
    val ySize: Int,
    val brd: Vector[Vector[Card]]
) {

  override def toString(): String =
   brd.flatten.toSeq.zipWithIndex.map {case(aCard,idx) => if ((idx+1)%4==0) ((" " * (2-len(aCard.toString()))) + s"${aCard.toString()}\n") else ((" " * (2-len(aCard.toString()))) + s"${aCard.toString()}|")}.mkString 

  def turnBoardCard(input: Int): Board =
    val turnedIdxBrd: Vector[Vector[Card]] = brd.zipWithIndex.map {
      case (vectorRow, vectorNum) =>
        vectorRow.zipWithIndex.map { case (cCard, idx) =>
          if (vectorNum * xSize + idx == input) new Card(cCard.value, true)
          else cCard
        }
    }
    new Board(xSize, ySize, turnedIdxBrd)

  def switch(that: Any, input: Int): (Any, Board) =
    val x: String = brd.zipWithIndex
      .map { case (vectorRow, vectorNum) =>
        vectorRow.zipWithIndex.map { case (cCard, idx) =>
          if (vectorNum * xSize + idx == input) cCard.value.toString()
        }
      }
      .toString()
    val sw: Vector[Vector[Card]] = brd.zipWithIndex.map {
      case (vectorRow, vectorNum) =>
        vectorRow.zipWithIndex.map { case (cCard, idx) =>
          if (vectorNum * xSize + idx == input) then
            that match {
              case d: Deck         => toCard(d.upperCard)
              case h: Hand         => toCard(h.toString())
              case d2: DiscardPile => toCard(d2.toString())
            }
          else cCard
        }
    }
    (
      that match {
        case h: Hand           => new Hand(x)
        case d: Deck           => new Deck(d.remove(1), "Deck")
        case disc: DiscardPile => new DiscardPile(x)
      },
      new Board(xSize, ySize, sw)
    )
}
// object Board
// def apply(xSize: Int, ySize: Int, brd: Vector[Vector[Card]]): Board = new Board(
//   xSize,
//   ySize,
//   fillBoard(xSize, ySize, Deck(fillDeck(Seq.empty[Card]), "Deck"))
// )
