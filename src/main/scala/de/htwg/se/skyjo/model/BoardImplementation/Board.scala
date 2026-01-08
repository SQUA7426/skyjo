package de.htwg.se.skyjo.model.BoardImplementation

import de.htwg.se.skyjo.model.DeckInterface
import de.htwg.se.skyjo.model.DeckImplementation.*
import de.htwg.se.skyjo.model.DiscardPileInterface
import de.htwg.se.skyjo.model.DiscardPileImplementation.*
import de.htwg.se.skyjo.model.CardInterface
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.Controller

import scala.util.Random
import scala.util.control._
import scala.collection.immutable.Seq
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.BoardInterface

case class Board(
    val _mediator: Mediator,
    val xSize: Int,
    val ySize: Int,
    val brd: Vector[Vector[CardInterface]] = Vector.empty
) extends Colleague,
      BoardInterface {
  override def send(msg: String): Unit = _mediator.send(this, msg)

  override def receive(msg: String): Boolean = {
    println(s"Board Received Message: ${msg}")
    true
  }

  override def toString(): String =
    if brd.isEmpty then "Empty Bpard"
    else
      brd.flatten.toSeq.zipWithIndex.map { case (aCard, idx) =>
        if ((idx + 1) % xSize == 0)
          ((" " * ((aCard.toString().length()))) + s"${aCard.toString()}\n")
        else ((" " * ((aCard.toString().length()))) + s"${aCard.toString()}|")
      }.mkString

  def turnBoardCard(pos: Int): BoardInterface = {
    val turnedIdxBrd: Vector[Vector[CardInterface]] = brd.zipWithIndex.collect {
      case (vectorRow, vectorNum) =>
        vectorRow.zipWithIndex.collect { case (cCard, idx) =>
          if (vectorNum * xSize + idx == pos)
            cCard.trueCopy
          else cCard
        }
    }
    new Board(_mediator, xSize, ySize, turnedIdxBrd)
  }

  def getMediator = _mediator

  def getSize: (Int, Int) = (xSize, ySize)

  def getBoard = brd

  def getBoardCard(pos: Int): CardInterface = {
    if pos < 0 || pos > (ySize * xSize - 1) then
      throw new IndexOutOfBoundsException(
        s"Idx: ${pos} is not a valid Board entry!"
      )
    brd.flatten.apply(pos).trueCopy
  }

  def switch(
      newCard: CardInterface,
      pos: Int
  ): (CardInterface, BoardInterface) = {
    val oldCard = getBoardCard(pos)
    val updatedBrd = brd.zipWithIndex.map { case (row, y) =>
      row.zipWithIndex.map { case (card, x) =>
        if (y * xSize + x == pos) newCard.trueCopy else card
      }
    }
    (oldCard, new Board(_mediator, xSize, ySize, updatedBrd))
  }

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

object Board {
  def apply(ctrl: Controller): (BoardInterface, DeckInterface) = {
    ctrl.fillBoard(4, 3, Deck(ctrl))
  }
}
