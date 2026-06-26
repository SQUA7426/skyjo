package de.htwg.se.skyjo.model

<<<<<<< HEAD
import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
import de.htwg.se.skyjo.model.DeckImplementation.*
import de.htwg.se.skyjo.model.BoardImplementation.*
import de.htwg.se.skyjo.model.DiscardPileImplementation.*
import de.htwg.se.skyjo.model.CardImplementation.*
import de.htwg.se.skyjo.util.*
=======
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{Card, Deck, DiscardPile, Board}
>>>>>>> origin/docker
import de.htwg.se.skyjo.model.GameState
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import java.io.ByteArrayOutputStream
import scala.collection.immutable.Seq

import java.io.ByteArrayInputStream
import scala.Console

<<<<<<< HEAD
class BoardTest extends AnyWordSpec with Matchers {
  "A Board" should {
    val plCount = 1
    val med = new ConcreteMediator()

    val tempState = new GameState(med, Vector.empty, null, null, 0, None)
    val ctr = new Controller(tempState)

    val deck = new Deck(ctr.fullDeck(), ctr)
    val disc = new DiscardPile(ctr)

    val plBoards = Vector.fill(plCount)(new Board(med, 4, 3, Vector.empty))

    ctr.state = new GameState(med, plBoards, deck, disc, 0, None)
    ctr.setup()

    val board = ctr.getGameState.boards(ctr.getGameState.playerIdx)
    val tui = new Tui(ctr)

    "before filled" in:
      plBoards(0).brd shouldBe (Vector.empty)

    "be alternatively initialized" in:
      Board(ctr)._1 shouldBe a[BoardInterface]

    "parse toString()" in:
      board.toString() shouldBe a[String]
    "getBoard" in:
      ctr.getBoard shouldBe a[Vector[Vector[CardInterface]]]

    // ------------------- REDUCE --------------------------------- //
    "reduce nothing normally" in:
      ctr.reduce(0,0)._2 shouldBe a[Boolean]

    val twoTimesTwoBoard = new Board(med, 2, 2, Vector(Vector(Card(1,ctr), Card(1, ctr)), Vector(Card(1,ctr), Card(1, ctr).falseCopy)))

    "reduce a row" in:
      twoTimesTwoBoard.reduce(1, -1)._2 shouldBe a[Boolean]

=======
import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.skyjo.SkyjoModule
import net.codingwell.scalaguice.InjectorExtensions.*

class BoardTest extends AnyWordSpec with Matchers {
  "A Board" should {
    val plCount = 1

    val injector = Guice.createInjector(SkyjoModule(plCount))

    val ctr = injector.getInstance(classOf[ControllerInterface])

    ctr.setup()

    val board = ctr.getGameState.boards(ctr.getPlIdx)

    "be alternatively initialized" in:
      Board(ctr)._1 shouldBe a[BoardInterface]

    "parse toString()" in:
      board.toString() shouldBe a[String]
    "getBoard" in:
      ctr.getBoard shouldBe a[Vector[Vector[CardInterface]]]

    // ------------------- REDUCE --------------------------------- //
    "reduce nothing normally" in:
      ctr.reduce(0,0)._2 shouldBe a[Boolean]

    val twoTimesTwoBoard = new Board(2, 2, Vector(Vector(Card(1), Card(1)), Vector(Card(1), Card(1).falseCopy)))

    "reduce a row" in:
      twoTimesTwoBoard.reduce(1, -1)._2 shouldBe a[Boolean]

>>>>>>> origin/docker
    "reduce a col" in:
      twoTimesTwoBoard.reduce(-1, 1)._2 shouldBe a[Boolean]

    "reduce a row and col" in:
      twoTimesTwoBoard.reduce(1, 1)._2 shouldBe a[Boolean]

<<<<<<< HEAD
=======
    "can be converted to Json" in:
      val twoXtwoJson = twoTimesTwoBoard.toJson

>>>>>>> origin/docker
    // ------------------ EXCEPTION ------------------------------- //
    "throw an EXCEPTION when accessing wrong boardIdx" in:
      val errBrd = the [IndexOutOfBoundsException] thrownBy(board.getBoardCard(30))
  }
}
