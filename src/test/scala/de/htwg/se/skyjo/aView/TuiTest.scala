package de.htwg.se.skyjo.aView

import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.model.{Board, Deck, DiscardPile, Card, fillDeck}

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import java.io.ByteArrayInputStream
import de.htwg.se.skyjo.model.fillBoard

class TuiTest extends AnyWordSpec with Matchers {
  "A Tui should" when:
    val t = new Tui()
    "finished()" should:
      "return true when all cards are face-up" in:
        val board = new Board(
          2,
          2,
          Vector(
            Vector(Card(1, true), Card(2, true)),
            Vector(Card(3, true), Card(4, true))
          )
        )

        t.finished(board) shouldBe true

    "finished()" should:
      "return false if one card is face-down" in:
        val board = new Board(
          2,
          2,
          Vector(
            Vector(Card(1, true), Card(2, false)),
            Vector(Card(3, true), Card(4, true))
          )
        )
        t.finished(board) shouldBe false
    val deck = new Deck(fillDeck(Seq.empty[Card]), "Deck")
    "chooseTake1" should {
      "when a switch with discard and board card" in {
        val board = new Board(
          1,
          2,
          Vector(Vector(Card(1, true), Card(2, true)))
        )
        val disc = new DiscardPile("1")

        val simulatedInput = "1\n" // index 1
        Console.withIn(new ByteArrayInputStream(simulatedInput.getBytes())) {
          val (b2, d2, disc2) = t.chooseTake1(board, deck, disc)

          b2.brd(0)(1).value shouldBe 1

          disc2.toString shouldBe "2"
        }
      }
      "when switched discard with board" in:
        val board = new Board(
          1,
          2,
          Vector(Vector(Card(1, true), Card(2, true)))
        )
        val disc = new DiscardPile("1")

        val simulatedInput = "0\n" // index 1
        Console.withIn(new ByteArrayInputStream(simulatedInput.getBytes())) {
          val (b2, d2, disc2) = t.chooseTake1(board, deck, disc)

          b2.brd(0)(0).value shouldBe 1
        }
    }

    val disc = new DiscardPile("Disc")
    "chooseTake2: option 2" should {
      "put card to discard then flip board card" in {
        val board =
          new Board(2, 1, Vector(Vector(Card(1, false), Card(2, false))))

        val localDeck = new Deck(fillDeck(Seq.empty[Card]), "Deck")

        val simulatedInput =
          "2\n" +
            "1\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())

        Console.withIn(in) {
          val (b2, d2, disc2) = t.chooseTake2(board, localDeck, disc)

          disc2.toString should not be "Disc"
          b2.brd(0)(1).turned shouldBe true
        }
      }
      "when neither 1 or 2 is choosen" in:
        val board =
          new Board(2, 1, Vector(Vector(Card(1, false), Card(2, false))))

        val localDeck = new Deck(fillDeck(Seq.empty[Card]), "Deck")

        val simulatedInput =
          "3\n" +
            "2\n1\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())

        Console.withIn(in) {
          val (b2, d2, disc2) = t.chooseTake2(board, localDeck, disc)

          disc2.toString should not be "Disc"
          b2.brd(0)(1).turned shouldBe true
        }
    }
    "a Turn" should:
      "chooseTake1 if DiscardPile==Disc" in:
        val b:Board = fillBoard(2,2,deck)._1

        val simulatedInput = "1\n1\n1\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())

        Console.withIn(in) {
          val (afterBoards, afterDeck, afterDisc) = t.chooseTake1(b,deck,disc)
          afterBoards shouldBe a[Board]
        }
      "chooseTake1 if choose==0 and idx not in Board size" in:
        val b:Board = new Board(2, 1, Vector(Vector(Card(1,false),Card(2,false))))
        val disc2 = new DiscardPile("3")
        val simulatedInput = "0\n22\n0\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())

        Console.withIn(in) {
          val (afterBoards, afterDeck, afterDisc) = t.turn(b,deck,disc2)
          afterBoards shouldBe a[Board]
        }
    "firstRound" should:
      "fill boards and plays one turn per player" in:

        val boards =Array.fill(1)(new Board(2, 2, Vector())) 
        val simulatedInput = "3\n1\n1\n0\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())

        Console.withIn(in) {
          val (afterBoards, afterDeck, afterDisc,stop) =
            t.firstRound(1, boards, deck, disc)
          afterBoards(0).brd(0).size should (be(1) or be(2))
        }
    "NextRound" should:
      "execute the right steps" in:

        val boards = Array(
          // new Board(2,1,Vector(Vector(Card(1, true), Card(2,false)))),
          new Board(
            2,
            2,
            Vector(
              Vector(Card(1, true), Card(2, false)),
              Vector(Card(4, true), Card(4, true))
            )
          )
        )

        val simulatedInput = "1\n1\n1\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())

        Console.withIn(in) {
          val (afterBoards, afterDeck, afterDisc,stop) =
            t.nextRounds(1, boards, deck, disc)

          afterBoards(0).brd(0).length should (be (2) or be (1))
        }

    "Game Loop" should:
      "execute right" in:
        val boards: Array[Board] =
          Array.fill(1)(new Board(2, 2, Vector(Vector())))
        val simulatedInput = "3\n1\n1\n0\n1\n1\n1\n1\n1\n2\n1\n1\n3\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())

        Console.withIn(in) {
          // val (afterBoards, afterDeck, afterDisc) = firstRound(1, boards, deck, disc)
          val gL = t.gameLoop(1, boards, deck, disc)
          gL
        }
}
