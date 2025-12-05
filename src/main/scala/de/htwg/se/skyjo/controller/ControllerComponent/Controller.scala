package de.htwg.se.skyjo.controller.ControllerComponent

import de.htwg.se.skyjo.model.{Board, Card, Deck, DiscardPile, fillBoard, fullDeck, getBoardCard}
import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent._
import de.htwg.se.skyjo.util.{Observable,Mediator,Memento, MoveCaretaker}

import scala.io.StdIn.{readInt, readLine}
import scala.util.Random

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

class Controller(val _mediator: Mediator, var disBoards: Array[Board], var disDeck: Deck, var discard: DiscardPile) extends Observable {
  val tui = new Tui(this)

  private var fromDeck : Boolean = false
  var mementostack : MoveCaretaker = new MoveCaretaker(_mediator)

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
    notifyObservers
    endBoard
  }
  def takeFromDisc(
      b: Board,
      d: Deck,
      disc: DiscardPile
  ): Option[(Board, Deck, DiscardPile)] = {
    if (disc.toString() == "Disc") { notifyObservers; tui.turn(b, d, disc)}
    else {
      println(tui.inputRequest(b, disc.toString()))
      val c2 = readLine()
      val container = (for{i <- 0 until b.xSize*b.ySize} yield i.toString()).toVector
      if container.contains(c2)==true then
          fromDeck = false
          val (disc2: DiscardPile, b2) = (b.switch(disc, c2.toInt): @unchecked)
          mementostack.save(Memento(fromDeck, d.getUpperCard(), c2.toInt, getBoardCard(b, c2.toInt),disc2))
          discard = disc2
          return Option(b2, d, disc2)
      else notifyObservers; takeFromDisc(b, d, disc)
    }
  }

  def takeFromDeck(
      b: Board,
      d: Deck,
      disc: DiscardPile
  ): (Board, Deck, DiscardPile) = {

    val tempD: Deck = new Deck(_mediator,d.deck, d.turnUpperCard())
    fromDeck = true
    val c1 = readLine()

    c1 match {
      case "1" => {
        fromDeck = true
        tui.inputRequest(b, tempD.getUpperCard().toString())
        val c = readInt()
        val disc2: DiscardPile = new DiscardPile(_mediator,getBoardCard(b, c).toString())
        mementostack.save(Memento(fromDeck, d.getUpperCard(), c, getBoardCard(b, c),disc2))
        val (d2: Deck, b2) = (b.switch(tempD, c): @unchecked)
        notifyObservers

        (b2, d2, disc2)
      }
      case "2" => {
        val (disc2: DiscardPile, d2: Deck) = {
          notifyObservers

          (disc.putToDiscardPile(tempD): @unchecked)
        }
        tui.cardTurnRq(b)

        val c = readInt()
        val (dd, b2) = b.switch(disc2, c)

        disDeck = d2
        discard = disc2
        fromDeck = false
        mementostack.save(Memento(fromDeck, d.getUpperCard(),c,getBoardCard(b,c),disc2))
        (b2, d2, disc2)
      }

      case i if i != "2" && i != "1" => {
        notifyObservers
        takeFromDeck(b, d, disc)
      }

    }

  }

  def firstRound(
      numPlayers: Int,
      plBoards: Array[Board],
      deck: Deck,
      disc: DiscardPile
  ): (Array[Board], Deck, DiscardPile, Boolean) = {

    var curDeck:Deck = deck
    var curDisc = disc

    for i <- 0 until numPlayers do
      val (cardsOnBoard, deckAfterFill) =
        fillBoard(_mediator,plBoards(i).xSize, plBoards(i).ySize, curDeck)
      val beforeTurned = cardsOnBoard
      val initSize = plBoards(i).xSize * plBoards(i).ySize
      val arr: Array[Int] =
        Random.shuffle({ (for { i <- 0 until initSize } yield i) }).toArray
      val firstTurned: Board = beforeTurned.turnBoardCard(arr(0))
      plBoards(i) = firstTurned.turnBoardCard(arr(1))
      curDeck = deckAfterFill
      mementostack.save(Memento(true,getBoardCard(plBoards(i),arr(0)).falseCopy(),arr(0),getBoardCard(plBoards(i),arr(0)).trueCopy(),disc))
    for i <- 0 until numPlayers do
      tui.turnOfPlayer(i)
      // println(s"Player ${i}:")
      val (updatedBoard: Board, deckAfterTurn: Deck, discAfterTurn: DiscardPile) =
        tui.turn(plBoards(i), curDeck, curDisc).getOrElse((plBoards(i), curDeck, curDisc))
      plBoards(i) = getReducedBrd(updatedBoard)
      curDeck = deckAfterTurn
      curDisc = discAfterTurn
      notifyObservers

    disBoards = plBoards
    (plBoards, curDeck, curDisc, false)
  }

  def nextRounds(
      numPlayers: Int,
      plBoards: Array[Board],
      deck: Deck,
      disc: DiscardPile
  ): (Array[Board], Deck, DiscardPile, Boolean) = {

    var curDeck:Deck = deck
    var curDisc:DiscardPile = disc

    for i <- 0 until numPlayers do
      tui.turnOfPlayer(i)
      // println(s"Player ${i}:")
      val (updatedBoard: Board, deckAfterTurn: Deck, discAfterTurn: DiscardPile) =
        tui.turn(plBoards(i), curDeck, curDisc).getOrElse((plBoards(i), curDeck, curDisc))
      plBoards(i) = getReducedBrd(updatedBoard)
      curDeck = deckAfterTurn
      curDisc = discAfterTurn
      notifyObservers

    disBoards = plBoards
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

    var newd : Deck = deck
    if(mementostack.checkMemAct()){
      newd = mementostack.getNewDeck()
      mementostack.setFalse()
    }

    val (boardsAfter, deckAfter, discAfter, stopBetween) =
      if round == 1 then firstRound(numPlayers, plBoards, deck, disc)
      else nextRounds(numPlayers, plBoards, deck, disc)
    disBoards = boardsAfter

    // TEST START
    val finishedBoards: Array[Boolean] =
      (for { i <- 0 until numPlayers } yield finished(boardsAfter(i))).toArray
    val isFinished: Boolean = finishedBoards
      .groupBy(identity)
      .map(t => (t._1, t._2.size))
      .map(_._1)
      .exists(_ == true)
    // TEST END
    if isFinished then
      notifyObservers
      tui.finishedConf()
      (boardsAfter, deckAfter, discAfter)
    else gameLoop(numPlayers, boardsAfter, deckAfter, discAfter, round + 1)
  }

  def finished(b: Board): Boolean =
    notifyObservers
    b.brd.forall(row => row.forall(c => c.isTurned() == true))
}
