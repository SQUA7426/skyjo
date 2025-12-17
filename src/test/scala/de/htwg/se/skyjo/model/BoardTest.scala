package de.htwg.se.skyjo.model
import de.htwg.se.skyjo.model.{
  Board,
  Card,
  Deck,
  DiscardPile,
  fillBoard,
  getBoardCard,
  len
}
import de.htwg.se.skyjo.util.ConcreteMediator
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import java.io.ByteArrayOutputStream
import scala.collection.immutable.Seq

class BoardTest extends AnyWordSpec with Matchers {
  "A Board" when {
    val med = new ConcreteMediator
    val bTemp = Board(med)
    val b = bTemp._1
    val d = bTemp._2

    //--------------------------- INIT & FILL BOARD --------------------------//

    "initialized and not filled" should:
      "create a New filled Board even when filled with x=0 and y=0" in:
        fillBoard(med, 0, 0, new Deck(med, Vector(),"Deck"))._1 shouldBe a[Board]
    "initialized and filled" should:
      "return a Board when UpperCard of Deck was turned" in:
        val turnedDeck = new Deck(med, d.deck, d.turnUpperCard())
        val bTurnedUpperCard: Board = fillBoard(med, 4, 3, turnedDeck)._1
        bTurnedUpperCard shouldBe a[Board]

      //--------------------------- TAKE FROM BOARD ----------------------------//

      "get a Card when gotten" in:
        val c: Card = getBoardCard(b, 0)
        c shouldBe a[Card]
      "get an IndexOutOfBoundsException when wrong idx gotten" in:
        val throwError =
          the[IndexOutOfBoundsException] thrownBy (getBoardCard(b, 20))
      "when a BoardCard is turned (e.g. 3rd) return a Board" in:
        b.turnBoardCard(3) shouldBe a[Board]

      //--------------------------- SWITCH /W BOARD ----------------------------//

      "return a new Deck when switched with DeckUpperCard" in:
        val d2: Deck = new Deck(med, d.deck, d.turnUpperCard())
        b.switch(d2, 3)._1 shouldBe a[Deck]
      "return a new DiscardPile when switched with the DiscardPile" in:
        val disc: DiscardPile = new DiscardPile(med, "Disc")

      //--------------------------- REDUCE ----------------------------//

      "colCheck should return true" in:
        val secBrd: Board = new Board(
          med,
          2,
          2,
          Vector(
            Vector(Card(med, 1, true), Card(med, 2, true)),
            Vector(Card(med, 3, true), Card(med, 2, true))
          )
        )
        secBrd.reduce(-1, 1)._2 shouldBe true

      //--------------------------- STRING  ----------------------------//

      "be as String" in:
        b.toString() shouldBe b.brd.flatten.toSeq.zipWithIndex.map {
          case (aCard, idx) =>
            if ((idx + 1) % 4 == 0)
              ((" " * (2 - len(aCard.toString()))) + s"${aCard.toString()}\n")
            else ((" " * (2 - len(aCard.toString()))) + s"${aCard.toString()}|")
        }.mkString
  }
}
