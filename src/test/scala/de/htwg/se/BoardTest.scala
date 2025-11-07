package de.htwg.se

import de.htwg.se.Card
import de.htwg.se.Deck
import de.htwg.se.Board
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import java.io.ByteArrayOutputStream
import scala.collection.immutable.Seq

class BoardTest extends AnyWordSpec with Matchers {
  "A Board" when {
    val d = new Deck(fillDeck(Seq.empty[Card]), "Deck")
    "not filled" should:
      "create a New filled Board even when filled with x=0 and y=0" in:
        fillBoard(4,3,Deck(Seq.empty[Card].toVector,"Deck")) shouldBe a[Vector[Vector[Card]]]
    val b:Vector[Vector[Card]] = fillBoard(4,3,d)
    "filled" should:
      "create a New filled Board when filled x=4 and y=3" in:
        b shouldBe a[Vector[Vector[Card]]]
      "still be a filled Board when UpperCard of Deck was turned" in:
        val turnedDeck = Deck(d.deck,d.turnUpperCard())
        val bTurnedUpperCard:Vector[Vector[Card]] = fillBoard(4,3,turnedDeck)
        bTurnedUpperCard shouldBe a[Vector[Vector[Card]]]
    "initialized" should:
      val aBoard = new Board(4,3,b)
      "be as String" in:
        aBoard.toString() shouldBe aBoard.brd.flatten.toSeq.map(t => s" ${t} |").mkString
  }
}
