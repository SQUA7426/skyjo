package de.htwg.se.skyjo.Model

import de.htwg.se.skyjo.Model.{Deck}

import scala.util.Random
import scala.util.control._
import scala.collection.immutable.Seq
import de.htwg.se.skyjo.util.{Mediator, Colleague}

def fillBoard(
    _mediator: Mediator,
    xSize: Int,
    ySize: Int,
    d: Deck
): (Board, Deck) = {
  if (d.deck.size == 0) then
    val deck: Deck = Deck(_mediator)
    fillBoard(_mediator, 4, 3, deck)
  else {

    def drawField(deck: Deck): (Card, Deck) = {
      val turnedDeck =
        if (deck.upperCard == "Deck")
          new Deck(_mediator, deck.deck, deck.turnUpperCard())
        else deck

      val topCard = turnedDeck.getUpperCard()
      val newDeck =
        new Deck(_mediator, turnedDeck.remove(1), "Deck")
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
    (
      new Board(_mediator, xSize, ySize, turnedBrd),
      new Deck(_mediator, d.remove(xSize * ySize), "Deck")
    )
  }
}

case class Board(
    val _mediator: Mediator,
    val xSize: Int,
    val ySize: Int,
    brd: Vector[Vector[Card]]
) extends Colleague {
  override def send(msg: String): Unit = _mediator.send(this,msg)

  override def receive(msg: String): Boolean = {
    println(s"Board Received Message: ${msg}")
    true
  }

  override def toString(): String =
    brd.flatten.toSeq.zipWithIndex.map { case (aCard, idx) =>
      if ((idx + 1) % xSize == 0)
        ((" " * (2 - len(aCard.toString()))) + s"${aCard.toString()}\n")
      else ((" " * (2 - len(aCard.toString()))) + s"${aCard.toString()}|")
    }.mkString

  def turnBoardCard(input: Int): Board =
    val turnedIdxBrd: Vector[Vector[Card]] = brd.zipWithIndex.map {
      case (vectorRow, vectorNum) =>
        vectorRow.zipWithIndex.map { case (cCard, idx) =>
          if (vectorNum * xSize + idx == input)
            new Card(_mediator, cCard.value, true)
          else cCard
        }
    }
    new Board(_mediator, xSize, ySize, turnedIdxBrd)

  def switch(that: Any, input: Int): (Any, Board) =
    val x: String = brd.flatten.apply(input).trueCopy().toString()
    val sw: Vector[Vector[Card]] = brd.zipWithIndex.map {
      case (vectorRow, vectorNum) =>
        vectorRow.zipWithIndex.map { case (cCard, idx) =>
          if (vectorNum * xSize + idx == input) then
            that match {
              case d: Deck         => toCard(_mediator, d.upperCard)
              case d2: DiscardPile => toCard(_mediator, d2.toString())
            }
          else cCard
        }
    }
    (
      that match {
        case d: Deck           => new Deck(_mediator, d.remove(1), "Deck")
        case disc: DiscardPile => new DiscardPile(_mediator, x)
      },
      new Board(_mediator, xSize, ySize, sw)
    )

  def reduce(row: Int, col: Int): (Board, Boolean) = {
    if (col != -1) {
      val checkCol: Vector[Boolean] = (0 until xSize).toVector.map { colIdx =>
        brd.map(_(colIdx)).distinct.size == 1 && brd.size != 1
      }
      if checkCol(col) == true then
        val slicedBoard = brd.map(_.patch(col, Nil, 1))
        return (new Board(_mediator, xSize - 1, ySize, slicedBoard), true)
      else {
        (new Board(_mediator, xSize, ySize, brd), false)
      }
    }

    if (row != -1) {
      val checkRow: Vector[Boolean] = brd.map { r =>
        r.forall(_ == r.head)
      }

      if checkRow(row) == true then
        return (
          new Board(
            _mediator,
            xSize,
            ySize - 1,
            brd.slice(0, row) ++ brd.drop(row + 1)
          ),
          true
        )
      else {
        (new Board(_mediator, xSize, ySize, brd), false)
      }
    }
    (new Board(_mediator, xSize, ySize, brd), false)
  }
}
def getBoardCard(b: Board, input: Int): Card = {
  if input < 0 || input > (b.ySize * b.ySize - 1) then
    throw new IndexOutOfBoundsException(
      s"Idx: ${input} is not a valid Board entry!"
    )
  b.brd.flatten.apply(input).trueCopy()
}

object Board {
  def apply(_mediator: Mediator): Board = {
    fillBoard(_mediator, 4, 3, Deck(_mediator))._1
  }
}
