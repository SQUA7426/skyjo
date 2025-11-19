package de.htwg.se
import de.htwg.se.{Card,Board,Deck,/*Hand,*/DiscardPile}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import java.io.ByteArrayOutputStream
import scala.collection.immutable.Seq

class BoardTest extends AnyWordSpec with Matchers {
  "A Board" when {
    val d = new Deck(fillDeck(Seq.empty[Card]), "Deck")
    val b:Vector[Vector[Card]] = fillBoard(4,3,d)._1
    "initialized and not filled" should:
      "create a New filled Board even when filled with x=0 and y=0" in:
        fillBoard(4,3,Deck(Seq.empty[Card].toVector,"Deck"))._1 shouldBe a[Vector[Vector[Card]]]
    "initialized and filled" should:
      "return a Board when UpperCard of Deck was turned" in:
        val turnedDeck = Deck(d.deck,d.turnUpperCard())
        val bTurnedUpperCard:Vector[Vector[Card]] = fillBoard(4,3,turnedDeck)._1
        bTurnedUpperCard shouldBe a[Vector[Vector[Card]]]
      val aBoard = new Board(4,3,b)
      "get a Card when gotten" in:
        val c: Card = getBoardCard(aBoard,0)
        c shouldBe a[Card]
      "get an IndexOutOfBoundsException when wrong idx gotten" in:
        val throwError = the [IndexOutOfBoundsException] thrownBy(getBoardCard(aBoard,20))
      "be as String" in:
        aBoard.toString() shouldBe aBoard.brd.flatten.toSeq.zipWithIndex.map {case(aCard,idx) => if ((idx+1)%4==0) ((" " * (2-len(aCard.toString()))) + s"${aCard.toString()}\n") else ((" " * (2-len(aCard.toString()))) + s"${aCard.toString()}|")}.mkString
      "when a BoardCard is turned (e.g. 3rd) return a Board" in:
        aBoard.turnBoardCard(3) shouldBe a[Board]
      "return a new Deck when switched with DeckUpperCard" in:
        val d2: Deck = new Deck(d.deck, d.turnUpperCard())
        aBoard.switch(d2,3)._1 shouldBe a[Deck]
      "return a new DiscardPile when switched with the DiscardPile" in:
        val disc: DiscardPile = new DiscardPile("Disc")
      "colCheck should return true" in:
        val secBrd: Board = new Board(2,2, Vector(Vector(Card(1,true), Card(2,true)), Vector(Card(3,true),Card(2,true))))
        secBrd.reduce(-1,1)._2 shouldBe true
  }
}
