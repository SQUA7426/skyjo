package de.htwg.se.skyjo.aView.Gui

import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.stage.Stage
import scalafx.scene.paint.Color
import scalafx.scene.shape._
import scalafx.scene.shape.Shape
import scalafx.scene.Node._
import scalafx.beans.property.StringProperty
import scalafx.scene.text.Text
import scalafx.collections.ObservableBuffer
import scala.util.Random
import scalafx.scene.layout.{VBox, HBox}
import scalafx.scene.control.Button
import scalafx.scene.control.Label
import scalafx.event.ActionEvent
import scalafx.scene.layout.Pane
import scalafx.scene.input.MouseEvent
import scalafx.scene.control.Alert.*
import scalafx.scene.control.ButtonType.*

import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.ButtonType
import scalafx.application.Platform

import de.htwg.se.skyjo.aView.Gui.{BoardView, fontname}
import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.util.Observer
import de.htwg.se.skyjo.model.{
  GameState,
  State,
  CardInterface,
  DeckInterface,
  DiscardPileInterface,
  BoardInterface
}
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{Deck, DiscardPile}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.Controller

import scalafx.scene.text.Font
import scalafx.scene.layout.StackPane
import scalafx.geometry.Pos
import scalafx.scene.layout.Background
import scalafx.geometry.Insets
import sbt.testing.EventHandler
import scalafx.scene.Node

// object UIConstants {
//   val cardWidth = 132
//   val cardHeight = 198
//   val padding = 30
//   val fontname = "Arial"
// }

object Gui extends JFXApp3 with Observer {
  var ctr: ControllerInterface = _
  var boardLayer: Pane = _
  var b: BoardView = _

  override def start(): Unit = {
    try {
      require(ctr != null, "Controller must be set before launching GUI!")
      boardLayer = new Pane()
      b = new BoardView(ctr, boardLayer)
      print(b.termBoard.toString())
      stage = new JFXApp3.PrimaryStage {
        scene = new Scene {
          root = new Pane {
            style = "-fx-background-color: darkgreen;"
            b.boardPane.children = b.viewBoard()
            children = Seq(boardLayer, guiButtons(stage))
          }
        }
      }
      stage.show()
      ctr.add(this)
      update("")
    } catch {
      case e: Exception =>
        println(s"Statup error ${e.getMessage()}")
        e.printStackTrace()
    }
  }

  override def update(choose: String): Boolean = {
    Platform.runLater {
      try {
        println("In GUI update")
        b.syncController
        val reducedBoard = ctr.getReducedBrd(b.termBoard)._1
        b.termBoard = reducedBoard
        b.manyCards = b.BOARD_INIT(false)
        val newUI: Seq[Node] = b.viewBoard() :+ guiButtons(stage)
        boardLayer.children_=(newUI)
        b.vDiscard.uptCardView
        b.vDeck.uptCardView
        b.manyCards.map(_.uptCardView)
        ctr.assertGameState(
          ctr.getGameState.copy(
            boards = ctr.getBrds.updated(ctr.getPlIdx, b.termBoard),
            deck = b.aDeck,
            disc = b.aDisc,
            currentState = b.currentState
          )
        )
        b.uptBoardPane
      } catch {
        case e: Exception =>
          e.printStackTrace()
          println(s"Update error: ${e.getMessage}")
      }
    }
    true
  }
  def guiButtons(stage: Stage): HBox = {
    val ht = 20
    val wt = 60

    val bt_help = new Button("Help") {
      onAction = _ => {
        val alert = new Alert(AlertType.Information) {
          initOwner(stage)
          title = "Help"
          headerText = "Viewing Help"
          contentText = (
            "Rules:\n" ++
              "STATE: BEGIN\n" ++
              "0.1 DISC SELECT\n" ++
              "0.2 DECK SELECT\n" ++
              "0.3 COMMANDS => EXECUTING COMMAND\n" ++
              "\n" ++
              "STATE: MID\n" ++
              "0.1.1 / 0.2.1 BOARDCARD SELECT\n" ++
              "0.1.2 SWITCH DISC w/ BOARDCARD\n" ++
              "0.2.2 SWITCH DECK w/ BOARDCARD\n" ++
              "\n" ++
              "STATE: END\n" ++
              "0.3.2 SELECT BOARDCARD => turn BOARDCARD\n"
          )
        }
        alert.showAndWait()
      }
    }

    val bt_undo = new Button("undo")
    bt_undo.tooltip = "Undoing Turn"
    bt_undo.setPrefHeight(ht)
    bt_undo.setPrefWidth(wt)
    bt_undo.onMouseClicked = _ => {
      if b.currentState == State.BEGIN && ctr.currMemento.undoStack.nonEmpty
      then {
        // println(s"MemStack.undoStack:\n${ctr.currMemento.undoStack.toString()}\n")
        // val ctrl = new Controller(b._med, Array(b.termBoard), b.aDeck, b.aDisc)
        val mem: Memento = ctr.currMemento.undoStack(0)
        ctr.currMemento.undo(mem, b.aDeck, b.termBoard, b.aDisc) match {
          case Some(resBoard, resDeck, resDisc) => {

            val tmpRedo = ctr.currMemento
              .undoStack(0)
              .copy(
                // lastDisc = DiscardPile(oldUndo.replacedCard.trueCopy.toString())
                lastDisc = DiscardPile(mem.replacedCard.trueCopy.toString())
              )

            ctr.guiUndo(resBoard, resDeck, resDisc, b)

            // upt views
            val newUI: Seq[Node] = b.viewBoard() :+ guiButtons(stage)
            boardLayer.children_=(newUI)
            b.vDiscard.uptCardView
            b.vDeck.uptCardView
            b.manyCards.map(_.uptCardView)

            // println("\nREDOSTACK\n")

            ctr.save(tmpRedo)
            if !ctr.currMemento.undoStack.isEmpty then
              ctr.currMemento.undoStack.pop()
            if !ctr.currMemento.redoStack.isEmpty then
              ctr.currMemento.redoStack.pop()
            ctr.currMemento.redoStack.push(tmpRedo)
            b.syncController
            // println(ctr.currMemento.redoStack(0))

            println()
          }
          case None => {}
        }
      }
    }

    val bt_redo = new Button("redo")
    bt_redo.tooltip = "Redoing last Turn"
    bt_redo.setPrefHeight(ht)
    bt_redo.setPrefWidth(wt)
    bt_redo.onMouseClicked = _ => {
      if b.currentState == State.BEGIN && ctr.currMemento.redoStack.nonEmpty
      then {
        if b.currentState == State.BEGIN then
          val mem: Memento = ctr.currMemento.redoStack(0)
          ctr.currMemento.redo(
            mem,
            ctr.getDeck,
            ctr.getBrds(ctr.getPlIdx),
            ctr.getDisc
          ) match {
            case Some(resBoard, resDeck, resDisc) => {
              ctr.guiRedo(resBoard, resDeck, resDisc, b)
              // upt views
              val newUI: Seq[Node] = b.viewBoard() :+ guiButtons(stage)
              boardLayer.children_=(newUI)
              b.vDiscard.uptCardView
              b.vDeck.uptCardView
              b.manyCards.map(_.uptCardView)

              println("\nUNDOSTACK\n")
              val preUndoStack = ctr.currMemento.undoStack(0)

              ctr.save(preUndoStack)
              // ctr.currMemento.redoStack.clear()
              if !ctr.currMemento.redoStack.isEmpty then
                ctr.currMemento.redoStack.pop()
              println(ctr.currMemento.undoStack(0))
              b.syncController
              println()
            }
            case None => {}
          }
      }
    }

    val ButtonTypeJson = new ButtonType("Json")
    val ButtonTypeXml = new ButtonType("Xml")

    val bt_save = new Button("save")
    bt_save.tooltip = "save GAME"
    bt_save.setPrefHeight(ht)
    bt_save.setPrefWidth(wt)
    bt_save.onMouseClicked = _ => {
      val alert = new Alert(AlertType.Confirmation) {
        initOwner(stage)
        title = "Saving"
        headerText = "Saving current GameState inside ./saves/"
        contentText = "Save as a..."
        buttonTypes = Seq(
          ButtonTypeJson,
          ButtonTypeXml,
          ButtonType.Cancel
        )
      }

      val res = alert.showAndWait()

      res match {
        case Some(ButtonTypeJson) => {ctr.json_save; }
        case Some(ButtonTypeXml)  => {ctr.xml_save; }
        case _                 => { println("Canceled Saving."); }
      }
    }

    val bt_load = new Button("load")
    bt_load.tooltip = "load GAME"
    bt_load.setPrefHeight(ht)
    bt_load.setPrefWidth(wt)
    bt_load.onMouseClicked = _ => {
      val alert = new Alert(AlertType.Confirmation) {
        initOwner(stage)
        title = "Saving"
        headerText = "Loading current GameState inside ./saves/"
        contentText = "Loading from type..."
        buttonTypes = Seq(
          ButtonTypeJson,
          ButtonTypeXml,
          ButtonType.Cancel
        )
      }

      val res = alert.showAndWait()

      res match {
        case Some(ButtonTypeJson) => {
          ctr.json_load(b)
          update("")
        }
        case Some(ButtonTypeXml)  => {
          ctr.xml_load(b)
          update("")
        }
        case _                 => { println("Canceled Loading."); }
      }
    }

    val bt_quit = new Button("quit")
    bt_quit.tooltip = "quitting GAME"
    bt_quit.setPrefHeight(ht)
    bt_quit.setPrefWidth(wt)
    bt_quit.onMouseClicked = _ => {
      val alert = new Alert(AlertType.Confirmation) {
        initOwner(stage)
        title = "Quitting Game"
      }
      alert.headerText = "Do you really want to quit?"
      alert.contentText = "Your progress will not be saved."

      val result: Option[ButtonType] = alert.showAndWait()

      if (result.contains(ButtonType.OK)) {
        println("Quitting Game")
        Platform.exit()
      }
    }

    val buttonBox = new HBox {
      spacing = 50
      children = List(bt_help, bt_undo, bt_redo, bt_save, bt_load, bt_quit)
    }
    buttonBox
  }
}
