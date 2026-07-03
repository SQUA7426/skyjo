package de.htwg.se.skyjo.fileIoComponent.fileIoJsonImpl

import de.htwg.se.skyjo.fileIoComponent.FileIOInterface
import de.htwg.se.skyjo.model.{
  GameState,
  BoardInterface,
  CardInterface,
  DeckInterface,
  DiscardPileInterface,
  State
}

import de.htwg.se.skyjo.model.modelInterfaceImplementation.{
  Board,
  Card,
  Deck,
  DiscardPile
}

import de.htwg.se.skyjo.util.{Memento, MoveCaretaker}

import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface

import play.api.libs.json.{Json, JsObject, Writes, Reads}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.*
import java.io.{PrintWriter, File}
import scala.io.Source
import java.nio.file.{Files, Paths}

class JsonImpl(ctrl: ControllerInterface) extends FileIOInterface:

  def load(filename: String): GameState =
    val input = Files.readString(Paths.get(f"${ctrl.path}$filename"))
    val out = Json.parse(input)
    val mems: Vector[MoveCaretaker] = (out \ "mementos").get.as[Vector[MoveCaretaker]]
    val boards = (out \ "boards").get.as[Vector[BoardInterface]]
    val deck = (out \ "deck").get.as[DeckInterface]
    val disc = (out \ "disc").get.as[DiscardPileInterface]
    val idx = (out \ "plIdx").get.as[Int]
    val cs = (out \ "currentState").get.as[State]
    val gs: GameState = GameState(mems,boards, deck,disc,idx,cs)
    gs

  def save(gs: GameState,filename: String): Unit =
    val gsJsonData = Json.toJson(gs)
    val jsonString = Json.prettyPrint(gsJsonData)
    Files.write(Paths.get(f"${ctrl.path}$filename"), jsonString.getBytes)

  implicit val cardIntWrites: Writes[CardInterface] = Writes { card =>
    Json.obj(
      "value" -> card.getValue,
      "turned" -> card.isTurned
    )
  }

  implicit val discIntWrites: Writes[DiscardPileInterface] = Writes { disc =>
    Json.obj(
      "discPile" -> disc.toString(),
      "turned" -> disc.isTurned
    )
  }

  implicit val deckIntWrites: Writes[DeckInterface] = Writes { deck =>
    Json.obj(
      "deck" -> deck.getDeckCards,
      "uppercard" -> deck.toString()
    )
  }

  implicit val boardIntWrites: Writes[BoardInterface] = Writes { board =>
    Json.obj(
      "xSize" -> board.getSize._1,
      "ySize" -> board.getSize._2,
      "brd" -> board.getBoard.flatten
    )
  }

  implicit val mementoWrites: Writes[Memento] = Writes { memento =>
    Json.obj(
      "fromDeck" -> memento._1,
      "takenCard" -> memento._2,
      "boardIndex" -> memento._3,
      "replacedCard" -> memento._4,
      "lastDisc" -> memento._5,
      "replacedCardTurned" -> memento._6
    )
  }

  implicit val moveCareWrites: Writes[MoveCaretaker] = Writes { mc =>
    Json.obj(
      "undoStack" -> mc.undoStack.toSeq,
      "redoStack" -> mc.redoStack.toSeq
    )
  }
  implicit val gameStateWrites: Writes[GameState] = Writes { gameState =>
    Json.obj(
      "mementos" -> gameState.mementos,
      "boards" -> gameState.boards,
      "deck" -> gameState.deck,
      "disc" -> gameState.disc,
      "plIdx" -> gameState.plIdx,
      "currentState" -> gameState.currentState.toJson
    )
  }

  // READS //

  implicit val cardIntReads: Reads[CardInterface] = Reads { json =>
    for
      value <- (json \ "value").validate[Int]
      turned <- (json \ "turned").validate[Boolean]
    yield Card(value, turned)
  }

  implicit val discIntReads: Reads[DiscardPileInterface] = Reads { json =>
    for
      dP <- (json \ "discPile").validate[String]
      turned <- (json \ "turned").validate[Boolean]
    yield DiscardPile(dP, turned)
  }

  implicit val deckIntReads: Reads[DeckInterface] = Reads { json =>
    for
      deckCards <- (json \ "deck").validate[Vector[CardInterface]]
      uppercard <- (json \ "uppercard").validate[String]
    yield Deck(deckCards, uppercard)
  }

  implicit val boardIntReads: Reads[BoardInterface] = Reads { json =>
    for
      x <- (json \ "xSize").validate[Int]
      y <- (json \ "ySize").validate[Int]
      flatBrd <- (json \ "brd").validate[Vector[CardInterface]]
    yield {
      val grid = flatBrd.grouped(x).toVector
      Board(x, y, grid)
    }
  }

  implicit val mementoReads: Reads[Memento] = Reads { json =>
    for
      fD <- (json \ "fromDeck").validate[Int]
      tC <- (json \ "takenCard").validate[CardInterface]
      idx <- (json \ "boardIndex").validate[Int]
      rC <- (json \ "replacedCard").validate[CardInterface]
      lD <- (json \ "lastDisc").validate[DiscardPileInterface]
      rCT <- (json \ "replacedCardTurned").validate[Boolean]
    yield Memento(fD, tC, idx, rC, lD, rCT)
  }

  def moveCareReads(ctrl: ControllerInterface): Reads[MoveCaretaker] = Reads {
    json =>
      for {
        uStack <- (json \ "undoStack").validate[Seq[Memento]]
        rStack <- (json \ "redoStack").validate[Seq[Memento]]
      } yield {
        val caretaker = new MoveCaretaker(ctrl)
        caretaker.undoStack.pushAll(uStack.reverse)
        caretaker.redoStack.pushAll(rStack.reverse)
        caretaker
      }
  }

  implicit val mcReads: Reads[MoveCaretaker] = moveCareReads(ctrl)

  implicit val stateReads: Reads[State] = Reads { json =>
    for
      s <- (json \ "str").validate[String]
      p <- (json \ "pre").validate[String]
    yield State.ASSERT(s, p)
  }

  implicit val gameStateReads: Reads[GameState] = Reads { json =>
    for
      mems <- (json \ "mementos").validate[Vector[MoveCaretaker]]
      b <- (json \ "boards").validate[Vector[BoardInterface]]
      de <- (json \ "deck").validate[DeckInterface]
      di <- (json \ "disc").validate[DiscardPileInterface]
      idx <- (json \ "plIdx").validate[Int]
      cs <- (json \ "currentState").validate[State]
    yield GameState(mems, b, de, di, idx, cs)
  }
