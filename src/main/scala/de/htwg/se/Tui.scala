package de.htwg.se

import de.htwg.se.{Card, Deck, /*Hand,*/ Board, DiscardPile}
import scala.io.StdIn.{readLine,readInt}
import annotation.tailrec

/*turn:
  Deck umdrehen -> aufHand -> Fall1: austauschen ; Fall2: auf DiscardPile & BoardKarte umdrehen
  DiscardPile -> aufHand -> muss mit BoardKarte Tauschen -> ausgetauschte BoardKarte auf DiscardPile
 */

def chooseTake1(
    b: Board,
    d: Deck,
    disc: DiscardPile
): (Board, Deck, DiscardPile) = {
  if disc.toString() == "Disc" then
    println("DiscardPile has no Card onto it!")
    turn(b, d, disc)
  else {
    println(
      s"Which BoardCard [0-${b.xSize * b.ySize - 1}] do you want to switch with ${disc.toString()}?"
    )
    val c2 = readInt()
    if c2 < b.xSize*b.ySize && c2 >= 0 then
      val (disc2: DiscardPile, b2) = (b.switch(disc, c2): @unchecked)
      println(b2)
      (b2, d, disc2)
    else chooseTake1(b,d,disc)
  }
}

def chooseTake2(
    b: Board,
    d: Deck,
    disc: DiscardPile
): (Board, Deck, DiscardPile) = {
  val tempD: Deck = new Deck(d.deck, d.turnUpperCard())
  println(s"[1] Do you want to switch ${tempD.toString()} with a BoardCard")
  println(
    s"[2] Or lay ${tempD.toString()} onto the DiscardPile and then turn a BoardCard?"
  )
  val c1 = readLine()
  c1 match {
    case "1" => {
      println(
        s"Which BoardCard [0-${b.xSize * b.ySize - 1}] do you want to switch with ${tempD.getUpperCard().toString()}?"
      )
      val c = readInt()
      val disc2: DiscardPile = new DiscardPile(getBoardCard(b, c).toString())
      val (d2: Deck, b2) = (b.switch(tempD, c): @unchecked)
      println(
        s"Switched BoardCard ${disc2.toString()} with DeckUpperCard ${tempD.toString()}"
      )
      println(b2)
      (b2, d2, disc2)
    }
    case "2" => {
      val (disc2: DiscardPile, d2: Deck) =
        (disc.putToDiscardPile(tempD): @unchecked)
      println(
        s"Which BoardCard [0-${b.xSize * b.ySize - 1}] do you want to turn around?"
      )
      val c = readInt()
      val (dd, b2) = b.switch(disc2, c)
      println(s"You turned BoardCard: ${disc2}")
      println(b2)
      (b2, d2, disc2)
    }
    case i if i != "2" && i != "1" => {
      println("You need to enter either 1 or 2!"); chooseTake2(b, d, disc)
    } /*throw new IllegalArgumentException("You need to enter either 1 or 2!\n")*/
  }
}

def firstRound(
    numPlayers: Int,
    plBoards: Array[Board],
    deck: Deck,
    disc: DiscardPile
): (Array[Board], Deck, DiscardPile,Boolean) = {

  var curDeck = deck
  var curDisc = disc

  for i <- 0 until numPlayers do
    val (cardsOnBoard, deckAfterFill) =
      fillBoard(plBoards(i).xSize, plBoards(i).ySize, curDeck)
    plBoards(i) = new Board(plBoards(i).xSize, plBoards(i).ySize, cardsOnBoard)
    curDeck = deckAfterFill

  for i <- 0 until numPlayers do
    println(s"Player ${i}:")
    val (updatedBoard, deckAfterTurn, discAfterTurn) =
      turn(plBoards(i), curDeck, curDisc)
    val reducedBoards: Array[(Board, Boolean)] = new Array(
      updatedBoard.xSize + updatedBoard.ySize
    )
    for j <- 0 until updatedBoard.xSize do
      reducedBoards(j) = updatedBoard.reduce(-1, j)
    for j <- 0 until updatedBoard.ySize do
      reducedBoards(j + updatedBoard.xSize) = updatedBoard.reduce(j, -1)
    if reducedBoards.map((brds,bool) =>(bool)==true).size>0 then plBoards(i) = updatedBoard
    else plBoards(i) = reducedBoards(0)._1
    curDeck = deckAfterTurn
    curDisc = discAfterTurn
  (plBoards, curDeck, curDisc,false)
}

def nextRounds(
    numPlayers: Int,
    plBoards: Array[Board],
    deck: Deck,
    disc: DiscardPile
): (Array[Board], Deck, DiscardPile,Boolean) = {

  var curDeck = deck
  var curDisc = disc

  for i <- 0 until numPlayers do
    println(s"Player ${i}:")
    val (updatedBoard, deckAfterTurn, discAfterTurn) =
      turn(plBoards(i), curDeck, curDisc)
    val reducedBoards: Array[(Board, Boolean)] = new Array(
      updatedBoard.xSize + updatedBoard.ySize
    )
    for j <- 0 until updatedBoard.xSize do
      reducedBoards(j) = updatedBoard.reduce(-1, j)
    for j <- 0 until updatedBoard.ySize do
      reducedBoards(j + updatedBoard.xSize) = updatedBoard.reduce(j, -1)
    if reducedBoards.map((brds,bool) =>(bool)==true).size>0 then plBoards(i) = updatedBoard
    else plBoards(i) = reducedBoards(0)._1
    curDeck = deckAfterTurn
    curDisc = discAfterTurn
    if finished(plBoards(i)) then return (plBoards, curDeck, curDisc,true)
  (plBoards, curDeck, curDisc,false)
}

@tailrec
def gameLoop(
    numPlayers: Int,
    plBoards: Array[Board],
    deck: Deck,
    disc: DiscardPile,
    round: Int = 1
): (Array[Board], Deck, DiscardPile) = {

  val (boardsAfter, deckAfter, discAfter, stopAfter) =
    if round == 1 then firstRound(numPlayers, plBoards, deck, disc)
    else nextRounds(numPlayers, plBoards, deck, disc)

  if stopAfter then
    println("Game finished! (a player finished)")
    (boardsAfter, deckAfter, discAfter)
  else
    gameLoop(numPlayers, boardsAfter, deckAfter, discAfter, round + 1)
}


def turn(b: Board, d: Deck, disc: DiscardPile): (Board, Deck, DiscardPile) =
  println(b)
  println(s"| ${disc.toString()} |\n")
  println("Whatcha want to do?")
  println("[0] Take discard and switch with a board card")
  println("[1] Take deck card and choose:")
  println("\t[1] switch with board card")
  println("\t[2] put on discard and flip board card")

  val choose = readLine()

  choose match {
    case "0" => chooseTake1(b, d, disc)

    case "1" => chooseTake2(b, d, disc)

    case _ =>
      println(s"${choose} is not valid, doing nothing.")
      (b, d, disc)
  }

def finished(b: Board): Boolean =
  b.brd.forall(row => row.forall(c => c._2 == true))

def main(args: Array[String]): Unit = {
  // println("Enter a number of players:")
  // val plCount = readInt()
  val plCount = 1
  val deck: Deck = new Deck(fullDeck()._1, fullDeck()._2)
  val disc: DiscardPile = new DiscardPile("Disc")

  val plBoards = Array.fill(plCount)(new Board(2,2,Vector())) // Empty Boards
  gameLoop(plCount,plBoards,deck,disc)

}
