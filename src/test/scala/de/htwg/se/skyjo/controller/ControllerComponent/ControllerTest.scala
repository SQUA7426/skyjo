package de.htwg.se.skyjo.controller.ControllerComponent

import de.htwg.se.skyjo.model.{
  Board,
  Card,
  Deck,
  DiscardPile,
  fillBoard,
  fullDeck,
  getBoardCard
}
import de.htwg.se.skyjo.aView.Tui

import scala.io.StdIn.{readInt, readLine}
// import annotation.tailrec
import scala.util.Random
import java.io.ByteArrayInputStream
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.skyjo.model.fillDeck

class ControllerTest extends AnyWordSpec with Matchers {
  "A Controller" when:
    val d = Deck(fillDeck(Seq.empty[Card]), "Deck")
    val b: Board = fillBoard(4, 3, d)._1
    val disc = DiscardPile("Disc")
    val ctrl = Controller()
    "it is working, it" should:
      "reduce a Board Column right" in:
        val updatedBoard = Board(
          3,
          2,
          Vector(
            Vector(Card(3), Card(1), Card(2).trueCopy()),
            Vector(Card(4), Card(6), Card(2).trueCopy())
          )
        )
        ctrl.getReducedBrd(updatedBoard) shouldBe a[Board]
      "reduce a Board Row right" in:
        val updatedBoard = Board(
          3,
          2,
          Vector(
            Vector(Card(3), Card(1), Card(5)),
            Vector(Card(2).trueCopy(), Card(2).trueCopy(), Card(2).trueCopy())
          )
        )
        ctrl.getReducedBrd(updatedBoard) shouldBe a[Board]
        ctrl.getReducedBrd(updatedBoard) shouldBe a[Board]

      "be unable to take a Card from the DiscardPile, when there's no Card" in:
        val brdArr = Array(b)
        val simulatedInput = "4\n0\n1\n1\n0\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())
        Console.withIn(in) {
          val (bTakeDisc, dTakeDisc, discTakeDisc, end) =
            ctrl.firstRound(1, brdArr, d, disc)
          dTakeDisc shouldBe a[Deck]
          discTakeDisc shouldBe a[DiscardPile]
        }
      val bTemp: Board = fillBoard(2, 1, d)._1
      val disc2 = DiscardPile("4")
      "be unable to take a Card from the DiscardPile" in:
        val simulatedInput = "40\n0\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())

        Console.withIn(in) {
          val (bTakeDisc, dTakeDisc, discTakeDisc) =
            ctrl.takeFromDisc(bTemp, d, disc2)
          dTakeDisc shouldBe a[Deck]
          discTakeDisc shouldBe a[DiscardPile]
        }

      "be able to take a Card from the DiscardPile" in:
        val simulatedInput = "0\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())

        Console.withIn(in) {
          val (bTakeDisc, dTakeDisc, discTakeDisc) =
            ctrl.takeFromDisc(bTemp, d, disc2)
          dTakeDisc shouldBe a[Deck]
          discTakeDisc shouldBe a[DiscardPile]
        }
      "be able to take a Card from the Deck Option 1" in:
        val simulatedInput = "3\n1\n0\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())
        Console.withIn(in) {
          ctrl.takeFromDeck(b, d, disc)
        }
      "be able to take a Card from the Deck Option 2" in:
        val anotherSimulatedInput = "2\n0\n"
        val in2 = new ByteArrayInputStream(anotherSimulatedInput.getBytes())
        Console.withIn(in2) {
          ctrl.takeFromDeck(b, d, disc)
        }
      val b2 = fillBoard(2, 1, d)._1
      val plBoards: Array[Board] = Array(b2)
      "manage the first round" in:
        val simulatedInput = "1\n1\n0\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())

        Console.withIn(in) {
          val (brdAfterFirst, deckAfterFirst, discAfterFist, firstTurnBool) =
            ctrl.firstRound(1, plBoards, d, disc)
          firstTurnBool shouldBe false
        }
      "manage next rounds" in:
        val simulatedInput = "1\n1\n0\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())
        Console.withIn(in) {
          ctrl.nextRounds(1, plBoards, d, disc)._4 shouldBe false
        }
      "manage a gameLoop" in:
        val simulatedInput = "1\n1\n0\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())
        Console.withIn(in) {
          val gl = ctrl.gameLoop(1, plBoards, d, disc)
          gl._3 shouldBe a[DiscardPile]
        }
      "manage some gameLoop" in:
        val biggerPlBoards = Array(fillBoard(2, 2, d)._1)
        val simulatedInput = "1\n1\n3\n1\n1\n2\n1\n1\n1\n1\n1\n0\n"
        val in = new ByteArrayInputStream(simulatedInput.getBytes())
        Console.withIn(in) {
          val gl = ctrl.gameLoop(1, biggerPlBoards, d, disc)
          gl._3 shouldBe a[DiscardPile]
        }
}
