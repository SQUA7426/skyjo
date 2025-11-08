package de.htwg.se
import de.htwg.se.{Card,Board,Deck,Hand,DiscardPile}
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
      "be as String" in:
        aBoard.toString() shouldBe aBoard.brd.flatten.toSeq.zipWithIndex.map {case(aCard,idx) => if ((idx+1)%4==0) ((" " * (2-len(aCard.toString()))) + s"${aCard.toString()}\n") else ((" " * (2-len(aCard.toString()))) + s"${aCard.toString()}|")}.mkString
      "when a BoardCard is turned (e.g. 3rd) return a Board" in:
        aBoard.turnBoardCard(3) shouldBe a[Board]
      "return a new Deck when switched with DeckUpperCard" in:
        val d2: Deck = new Deck(d.deck, d.turnUpperCard())
        aBoard.switch(d2,3)._1 shouldBe a[Deck]
      val h: Hand = new Hand(d.turnUpperCard())
      "return a new Hand when switched with HandCard" in:
        aBoard.switch(h,3)._1 shouldBe a[Hand]
      "return a new DiscardPile when switched with the DiscardPile" in:
        val disc: DiscardPile = new DiscardPile("Disc")
        val disc2: DiscardPile = new DiscardPile(disc.putToDiscardPile(h)._1.toString())
        aBoard.switch(disc2,3)._1 shouldBe a[DiscardPile]
  }
}
