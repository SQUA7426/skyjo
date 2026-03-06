package de.htwg.se.skyjo.fileIoComponent.fileIoJsonImpl

import de.htwg.se.skyjo.fileIoComponent.FileIOInterface
import de.htwg.se.skyjo.model.{
  GameState,
  BoardInterface,
  CardInterface,
  DeckInterface,
  DiscardPileInterface
}
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{Board, Card, Deck, DiscardPile}

import de.htwg.se.skyjo.util.Memento

import play.api.libs.json.{Json, JsObject, Writes, Reads}
import play.api.libs.json.Format.GenericFormat
import java.io.{PrintWriter, File}
import scala.io.Source
import de.htwg.se.skyjo.util.MoveCaretaker
import java.nio.file.{Files, Paths}

class JsonImpl extends FileIOInterface:
  private val path = "./game_state_data.json"

  def load: GameState =
    val input = Files.readString(Paths.get(path))
    val out = Json.parse(input)
    val gs: GameState = ( out \\ "GameState").head.as
    gs

  def save(gs: GameState): Unit =
    val gsJsonData = gs.toJson
    val jsonString = Json.prettyPrint(gsJsonData)
    Files.write(Paths.get(path), jsonString.getBytes)

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
      "deck" -> Json.toJson(deck.getDeckCards),
      "uppercard" -> deck.toString()
    )
  }

  implicit val boardIntWrites: Writes[BoardInterface] = Writes { board =>
    Json.obj("xSize" -> board.getSize._1, "ySize" ->  board.getSize._2, "brd" -> board.getBoard)
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
    Json.obj("undoStack" -> mc.undoStack, "redoStack" -> mc.redoStack)
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

  implicit val cardIntReads: Reads[CardInterface] = Reads {json =>
    for
      value <- (json \ "value").validate[Int]
      turned <- (json \ "turned").validate[Boolean]
    yield Card(value, turned)
  }

  implicit val discIntReads: Reads[DiscardPileInterface] = Reads {json =>
    for
      dP <- (json \ "discPile").validate[String]
      turned <- (json \ "turned").validate[Boolean]
    yield DiscardPile(dP, turned)
  }

  implicit val deckIntReads: Reads[DeckInterface] = Reads {json =>
    for
      deckCards <- (json \ "deck").validate[Vector[CardInterface]]
      uppercard <- (json \ "uppercard").validate[String]
    yield Deck(deckCards, uppercard)
  }

  implicit val boardIntReads: Reads[BoardInterface] = Reads {json =>
    for
      x <- (json \ "xSize").validate[Int]
      y <- (json \ "ySize").validate[Int]
      brd <- (json \ "brd").validate[Vector[Vector[CardInterface]]]
    yield Board(x,y,brd)
  }

  implicit val mementoReads: Reads[Memento] = Reads {json =>
     for
       fD <- (json \ "fromDeck").validate[Boolean]
       tC <- (json \ "takenCard").validate[CardInterface]
       idx <- (json \ "boardIndex").validate[Int]
       rC <- (json \ "replacedCard").validate[CardInterface]
       lD <- (json \ "lastDisc").validate[DiscardPileInterface]
       rCT <- (json \ "replacedCardTurned").validate[Boolean]
     yield Memento(fD,tC,idx,rC,lD,rCT)
  }

