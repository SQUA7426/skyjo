package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.model.CardInterface
import de.htwg.se.skyjo.util.*

trait BoardInterface:
  def getMediator: Mediator

  def getBoard: Vector[Vector[CardInterface]]

  def getSize: (Int, Int)

  def getBoardCard(pos: Int): CardInterface

  def turnBoardCard(pos: Int): BoardInterface

  def switch(newCard: CardInterface, pos: Int): (CardInterface, BoardInterface)

  def reduce(row: Int, col: Int): (BoardInterface, Boolean)
