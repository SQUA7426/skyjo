package de.htwg.se.util
import scala.util.Random
// class Board;
// class Card;
class Controller;
// class Deck;
// class DiscardPile;
class Tui;

trait Mediator {
  def notify(sender: Mediator, event: String): Unit
  def send(msg: String): Unit
}
class Mediating:
  var thresholdingMediator: Vector[Mediator] = Vector()
  def add(m: Mediator): Unit = thresholdingMediator = thresholdingMediator :+ m
  def remove(m: Mediator): Unit = thresholdingMediator =
    thresholdingMediator.filterNot(o => o == m)
  def notifyMediators(): Unit = thresholdingMediator.foreach(o => o.notify())

class Card(
    private val _mediator: Mediator,
    val value: Int,
    turned: Boolean = false
) extends Mediator {
  def apply(mediator: Mediator, value: Int): Card = new Card(mediator, value)
  override def notify(sender: Mediator, event: String): Unit = {
    _mediator.notify(this, "Created Card")
  }
  override def send(msg: String): Unit = _mediator.send(msg)
}

class Deck(
    private val _mediator: Mediator,
    val deck: Vector[Card],
    val upperCard: String
) extends Mediator {
  override def notify(sender: Mediator, event: String): Unit =
    _mediator.notify(this, "Created Deck")
  override def send(msg: String): Unit = _mediator.send(msg)
}

def fullDeck(_mediator: Mediator) = {
  val seqCards = Seq.empty[Card]
  val v1: Vector[Card] =
    (for { i <- 1 to 10; j <- -1 to 12 } yield Card(_mediator, j)).toVector
  val v2: Vector[Card] = (for {
    i <- 1 to 5; j <- -2 to 0; if j == -2 || j == 0
  } yield Card(_mediator, j)).toVector
  val fullDeck: Vector[Card] = v1 ++ v2
  val diffs: Vector[Card] = fullDeck.diff(seqCards)
  val shuffled = Random.shuffle(diffs)
  shuffled
}
object Deck:
  def apply(_mediator: Mediator): Deck =
    new Deck(_mediator, fullDeck(_mediator), "Deck")

class DiscardPile(private val _mediator: Mediator, val discPile: String)
    extends Mediator {
  override def notify(sender: Mediator, event: String): Unit =
    _mediator.notify(this._mediator, "Created DiscardPile")
  override def send(msg: String): Unit = _mediator.send(msg)

  override def toString(): String = s"${discPile}"

  def putToDiscardPile(from: Any): Unit =
    from match { case d: Deck => _mediator.send("Put To DiscardPile") }
}

def fillBoard(xSize: Int, ySize: Int, d: Deck): Board =
  if (d.deck.size == 0) then
    val deck: Deck = Deck(_mediator)
    fillBoard(4, 3, deck)
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
    (
      new Board(xSize, ySize, turnedBrd),
      new Deck(d.remove(xSize * ySize), "Deck")
    )
  }
class Board(
    private val _mediator: Mediator,
    val xSize: Int,
    val ySize: Int,
    brd: Vector[Vector[Card]]
) extends Mediator {
}

object Board:
  def apply(_mediator: Mediator): Board = { fillBoard() }
