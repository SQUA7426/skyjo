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

object UIConstants {
  val cardWidth = 132
  val cardHeight = 198
  val padding = 30
  val fontname = "Arial"
}

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
    println("In GUI update")
    b.syncController
    b.termBoard = ctr.getReducedBrd(b.termBoard)._1
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
    Platform.runLater {
      b.uptBoardPane
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
            // println("UNDO")
            // println(s"resBoard: ${resBoard}")
            // println(s"resDeck: ${resDeck.turnUpperCard}")
            // println(s"resDisc: ${resDisc}")
            b.termBoard = resBoard
            b.aDeck = resDeck
            b.aDisc = resDisc
            val oldUndo = ctr.currMemento.undoStack(0)
            ctr.assertGameState(
              ctr.getGameState.copy(
                boards = ctr.getBrds.updated(ctr.getPlIdx, resBoard),
                deck = resDeck,
                disc = resDisc
              )
            )
            val tmpRedo = ctr.currMemento
              .undoStack(0)
              .copy(
                takenCard = oldUndo.replacedCard,
                replacedCard = oldUndo.takenCard,
                lastDisc = DiscardPile(ctr, oldUndo.replacedCard.toString())
              )

            b.manyCards = b.BOARD_INIT(false)
            b.vDeck.cCard = ctr.toCard(b._med, b.aDeck.turnUpperCard)
            b.vDiscard.cCard = ctr.getDiscCard().get

            b.syncController
            // upt views
            val newUI: Seq[Node] = b.viewBoard() :+ guiButtons(stage)
            boardLayer.children_=(newUI)
            b.vDiscard.uptCardView
            b.vDeck.uptCardView
            b.manyCards.map(_.uptCardView)

            println("\nREDOSTACK\n")

            ctr.save(tmpRedo)
            ctr.assertGameState(
              ctr.getGameState.copy(mementos =
                ctr.getMementos.updated(ctr.getPlIdx, ctr.currMemento)
              )
            )
            ctr.currMemento.undoStack.clear()
            ctr.currMemento.redoStack.clear()
            ctr.currMemento.redoStack.push(tmpRedo)
            b.syncController
            println(ctr.currMemento.redoStack(0))

            // println(s"\nDisc: ${ctr.getDisc} ; aDisc: ${b.aDisc} ; vDisc: ${b.vDiscard}")
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
          ctr.currMemento.redo(mem, ctr.getDeck, ctr.getBrds(ctr.getPlIdx), ctr.getDisc) match {
            case Some(resBoard, resDeck, resDisc) => {
              val lDisc = ctr.currMemento.undoStack(0).lastDisc
              // println("REDO")
              // println(s"resBoard: ${resBoard}")
              // println(s"resDeck: ${resDeck.turnUpperCard}")
              // println(s"resDisc: ${resDisc}")
              b.termBoard = resBoard
              b.aDeck = resDeck
              b.aDisc = lDisc
              ctr.assertGameState(
                ctr.getGameState.copy(
                  boards = ctr.getBrds.updated(ctr.getPlIdx, resBoard),
                  deck = resDeck,
                  disc = lDisc
                )
              )
              b.manyCards = b.BOARD_INIT(false)
              b.vDeck.cCard = ctr.toCard(b._med, b.aDeck.turnUpperCard)
              b.vDiscard.cCard = ctr.getDiscCard().get

              // upt views
              val newUI: Seq[Node] = b.viewBoard() :+ guiButtons(stage)
              boardLayer.children_=(newUI)
              b.vDiscard.uptCardView
              b.vDeck.uptCardView
              b.manyCards.map(_.uptCardView)

              println("\nUNDOSTACK\n")
              val preUndoStack = ctr.currMemento.undoStack(0)
              val tmpMem = Memento(
                preUndoStack.fromDeck,
                preUndoStack.replacedCard,
                preUndoStack.boardIndex,
                preUndoStack.takenCard,
                preUndoStack.lastDisc,
                preUndoStack.lastDisc.isTurned
              )
              ctr.save(preUndoStack)
              // ctr.currMemento.undoStack.clear()
              // ctr.currMemento.undoStack.push(tmpMem)
              println(ctr.currMemento.undoStack(0))
              // println(s"\nDisc: ${ctr.getDisc} ; aDisc: ${b.aDisc} ; vDisc: ${b.vDiscard}")
              b.syncController
              println()
            }
            case None => {}
          }
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
      spacing = 120
      children = List(bt_help, bt_undo, bt_redo, bt_quit)
    }
    buttonBox
  }
}
