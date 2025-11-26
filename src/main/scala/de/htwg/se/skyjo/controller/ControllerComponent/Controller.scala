package de.htwg.se.skyjo.controller.ControllerComponent

import de.htwg.se.skyjo.model.{
  Board,
  Deck,
  DiscardPile,
  fillBoard,
  fullDeck,
  getBoardCard
}
import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent._
import de.htwg.se.skyjo.util.Observable

import scala.io.StdIn.{readInt, readLine}
// import annotation.tailrec
import scala.util.Random
/*turn:
  Deck umdrehen -> aufHand -> Fall1: austauschen ; Fall2: auf DiscardPile & BoardKarte umdrehen
  DiscardPile -> aufHand -> muss mit BoardKarte Tauschen -> ausgetauschte BoardKarte auf DiscardPile
 */

// enum Color(color: String) {
//   def getValue(): String = color
//   case RESET extends Color("\u001b[0m")
//   case RED extends Color("\u001b[31m")
//   case GREEN extends Color("\u001b[32m")
//   case YELLOW extends Color("\u001b[33m")
//   case BLUE extends Color("\u001b[34m")
//   case Magenta extends Color("\u001b[35m")
//   case Cyan extends Color("\u001b[36m")
//   case LightRed extends Color("\u001b[91m")
//   case LightGreen extends Color("\u001b[92m")
// }
class Controller extends Observable {
  val tui = new Tui(this)
  def getReducedBrd(updatedBoard: Board): Board = {
    val reducedBoards: Array[(Board, Boolean)] = new Array(
      updatedBoard.xSize + updatedBoard.ySize
    )
    for j <- 0 until updatedBoard.xSize do
      reducedBoards(j) = updatedBoard.reduce(-1, j)
    for j <- 0 until updatedBoard.ySize do
      reducedBoards(j + updatedBoard.xSize) = updatedBoard.reduce(j, -1)
    val r = reducedBoards.map(_._2).exists(_ == true)
    val endBoard: Board = if r == true then
      val allUpdatedBrds =
        reducedBoards.filter((brds, bools) => bools == true).map(_._1).toArray
      allUpdatedBrds(0)
    else updatedBoard
    println(endBoard)
    endBoard
  }
  def takeFromDisc(
      b: Board,
      d: Deck,
      disc: DiscardPile
  ): (Board, Deck, DiscardPile) = {
    if disc.toString() == "Disc" then tui.turn(b, d, disc)
    else {
      println(
        tui.inputRequest(b, disc.toString())
        // s"Which BoardCard [0-${b.xSize * b.ySize - 1}] do you want to switch with ${disc.toString()}?"
      )
      val c2 = readInt()
      if c2 < b.xSize * b.ySize && c2 >= 0 then
        val (disc2: DiscardPile, b2) = (b.switch(disc, c2): @unchecked)
        (b2, d, disc2)
      else takeFromDisc(b, d, disc)
    }
  }

  def takeFromDeck(
      b: Board,
      d: Deck,
      disc: DiscardPile
  ): (Board, Deck, DiscardPile) = {

    val tempD: Deck = new Deck(d.deck, d.turnUpperCard())
    val c1 = readLine()

    c1 match {
      case "1" => {
        tui.inputRequest(b, tempD.getUpperCard().toString())
        val c = readInt()
        val disc2: DiscardPile = new DiscardPile(getBoardCard(b, c).toString())
        val (d2: Deck, b2) = (b.switch(tempD, c): @unchecked)
        (b2, d2, disc2)
      }
      case "2" => {
        val (disc2: DiscardPile, d2: Deck) =
          (disc.putToDiscardPile(tempD): @unchecked)
        tui.cardTurnRq(b)

        // println(s"Which BoardCard [0-${b.xSize * b.ySize - 1}] do you want to turn around?")
        val c = readInt()
        val (dd, b2) = b.switch(disc2, c)
        // println(s"You turned BoardCard: ${disc2}")
        (b2, d2, disc2)
      }
      case i if i != "2" && i != "1" => {
        // println("You need to enter either 1 or 2!");
        takeFromDeck(b, d, disc)
      } /*throw new IllegalArgumentException("You need to enter either 1 or 2!\n")*/
    }
  }

  def firstRound(
      numPlayers: Int,
      plBoards: Array[Board],
      deck: Deck,
      disc: DiscardPile
  ): (Array[Board], Deck, DiscardPile, Boolean) = {

    var curDeck = deck
    var curDisc = disc

    for i <- 0 until numPlayers do
      val (cardsOnBoard, deckAfterFill) =
        fillBoard(plBoards(i).xSize, plBoards(i).ySize, curDeck)
      val beforeTurned = cardsOnBoard
      val initSize = plBoards(i).xSize * plBoards(i).ySize
      val arr: Array[Int] =
        Random.shuffle({ (for { i <- 0 until initSize } yield i) }).toArray
      val firstTurned: Board = beforeTurned.turnBoardCard(arr(0))
      plBoards(i) = firstTurned.turnBoardCard(arr(1))
      curDeck = deckAfterFill

    for i <- 0 until numPlayers do
      tui.turnOfPlayer(i)
      // println(s"Player ${i}:")
      val (updatedBoard, deckAfterTurn, discAfterTurn) =
        tui.turn(plBoards(i), curDeck, curDisc)
      plBoards(i) = getReducedBrd(updatedBoard)
      curDeck = deckAfterTurn
      curDisc = discAfterTurn
    (plBoards, curDeck, curDisc, false)
  }

  def nextRounds(
      numPlayers: Int,
      plBoards: Array[Board],
      deck: Deck,
      disc: DiscardPile
  ): (Array[Board], Deck, DiscardPile, Boolean) = {

    var curDeck = deck
    var curDisc = disc

    for i <- 0 until numPlayers do
      tui.turnOfPlayer(i)
      // println(s"Player ${i}:")
      val (updatedBoard, deckAfterTurn, discAfterTurn) =
        tui.turn(plBoards(i), curDeck, curDisc)
      plBoards(i) = getReducedBrd(updatedBoard)
      curDeck = deckAfterTurn
      curDisc = discAfterTurn
    (plBoards, curDeck, curDisc, false)
  }

  @annotation.tailrec
  final def gameLoop(
      numPlayers: Int,
      plBoards: Array[Board],
      deck: Deck,
      disc: DiscardPile,
      round: Int = 1
  ): (Array[Board], Deck, DiscardPile) = {

    val (boardsAfter, deckAfter, discAfter, stopBetween) =
      if round == 1 then firstRound(numPlayers, plBoards, deck, disc)
      else nextRounds(numPlayers, plBoards, deck, disc)
    // TEST START
    val finishedBoards: Array[Boolean] =
      (for { i <- 0 until numPlayers } yield finished(boardsAfter(i))).toArray
    val isFinished: Boolean = finishedBoards
      .groupBy(identity)
      .map(t => (t._1, t._2.size))
      .map(_._1)
      .exists(_ == true)

    // println(s"Someone finished: ${isFinished}\n")
    // TEST END
    if isFinished then
      // println("Game finished! (a player finished)")
      tui.finishedConf()
      (boardsAfter, deckAfter, discAfter)
    else gameLoop(numPlayers, boardsAfter, deckAfter, discAfter, round + 1)
  }

  def finished(b: Board): Boolean =
    b.brd.forall(row => row.forall(c => c._2 == true))
}
