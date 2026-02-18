package de.htwg.se.skyjo.model.modelInterfaceImplementation

import de.htwg.se.skyjo.model.{BoardInterface, CardInterface, DeckInterface}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface

import scala.util.Random
import scala.util.control._
import scala.collection.immutable.Seq
import de.htwg.se.skyjo.util.*

import jakarta.inject.Inject

case class Board (
    // val _mediator: Mediator,
    val xSize: Int,
    val ySize: Int,
    val brd: Vector[Vector[CardInterface]] = Vector.empty
) extends BoardInterface:

  override def toString(): String =
    if brd.isEmpty then "Empty Bpard"
    else
      brd.flatten.toSeq.zipWithIndex.map { case (aCard, idx) =>
        if ((idx + 1) % xSize == 0)
          ((" " * ((aCard.toString().length()))) + s"${aCard.toString()}\n")
        else ((" " * ((aCard.toString().length()))) + s"${aCard.toString()}|")
      }.mkString

  // MEDIATOR //
  // override def send(msg: String): Unit = _mediator.send(this, msg)

  // override def receive(msg: String): Boolean = {
  //   println(s"Board Received Message: ${msg}")
  //   true
  // }

  // CTRL //
  def getBoardCard(pos: Int): CardInterface =
    if pos < 0 || pos > ((ySize - 1) * xSize + (xSize - 1) ) then
      throw new IndexOutOfBoundsException(
        s"Idx: ${pos} is not a valid Board entry!"
      )
    brd.flatten.apply(pos).trueCopy

  def getSize: (Int, Int) = (xSize, ySize)

  def getBoard = brd

  def turnBoardCard(pos: Int): BoardInterface =
    val turnedIdxBrd: Vector[Vector[CardInterface]] = brd.zipWithIndex.collect {
      case (vectorRow, vectorNum) =>
        vectorRow.zipWithIndex.collect { case (cCard, idx) =>
          if (vectorNum * xSize + idx == pos)
            cCard.trueCopy
          else cCard
        }
    }
    new Board( xSize, ySize, turnedIdxBrd)

  def swapFromMem(c: CardInterface, pos: Int): BoardInterface =
    val uptBrd: Vector[Vector[CardInterface]] = brd.zipWithIndex.collect { case (vec,vecPos) =>
      vec.zipWithIndex.collect {
        case (cCard, cardPos) => {
          if (vecPos * xSize + cardPos == pos) c else cCard
        }
      }
    }
    new Board( xSize, ySize, uptBrd)

  def switch(
      newCard: CardInterface,
      pos: Int
  ): (CardInterface, BoardInterface) =
    val oldCard = getBoardCard(pos)
    val updatedBrd = brd.zipWithIndex.map { case (row, y) =>
      row.zipWithIndex.map { case (card, x) =>
        if (y * xSize + x == pos) newCard.trueCopy else card
      }
    }
    (oldCard, new Board( xSize, ySize, updatedBrd))

  def reduce(row: Int, col: Int): (BoardInterface, Boolean, Int, Int) =
    if (col != -1) {
      val checkCol: Vector[Boolean] = (0 until xSize).toVector.map { colIdx =>
        brd.map(_(colIdx)).distinct.size == 1 && brd.size != 1
      }
      if checkCol(col) == true then
        // println(s"reduce col: $col")
        val slicedBoard = brd.map(_.patch(col, Nil, 1))
        return (new Board( xSize - 1, ySize, slicedBoard), true, -1, col)
      else {
        // println("no col reduced")
        (new Board( xSize, ySize, brd), false, -1, -1)
      }
    }

    if (row != -1) {
      val checkRow: Vector[Boolean] = brd.map { r =>
        r.forall(_ == r.head)
      }
      // println("CheckRow:\n")
      // checkRow.foreach(println)

      if checkRow(row) == true then
        // println(s"reduce row: $row")
        return (
          new Board(
            
            xSize,
            ySize - 1,
            brd.slice(0, row) ++ brd.drop(row + 1)
          ),
          true,
          row,
          -1
        )
      else {
        // println("no row reduced")
        (new Board( xSize, ySize, brd), false, -1,-1)
      }
    }
    // println("no row and col reduced")
    (new Board( xSize, ySize, brd), false, -1, -1)

object Board:
  def apply(ctrl: ControllerInterface): (BoardInterface, DeckInterface) = {
    ctrl.fillBoard(4, 3, Deck(ctrl))
  }
