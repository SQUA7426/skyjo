package de.htwg.se.skyjo.model

import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{Card, Deck, DiscardPile, Board}
import de.htwg.se.skyjo.model.GameState
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import java.io.ByteArrayOutputStream
import scala.collection.immutable.Seq

import java.io.ByteArrayInputStream
import scala.Console

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

    "reduce a col" in:
      twoTimesTwoBoard.reduce(-1, 1)._2 shouldBe a[Boolean]

    "reduce a row and col" in:
      twoTimesTwoBoard.reduce(1, 1)._2 shouldBe a[Boolean]

    // ------------------ EXCEPTION ------------------------------- //
    "throw an EXCEPTION when accessing wrong boardIdx" in:
      val errBrd = the [IndexOutOfBoundsException] thrownBy(board.getBoardCard(30))
  }
}
