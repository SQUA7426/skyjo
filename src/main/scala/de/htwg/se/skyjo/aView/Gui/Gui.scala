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
      b = new BoardView(ctr)
      print(b.termBoard.toString())
      stage = new JFXApp3.PrimaryStage {
        scene = new Scene {
          root = new Pane {
            style = "-fx-background-color: darkgreen;"
            boardLayer.children = b.viewBoard()
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
        currentState = b.currentState,
      )
    )
    true
  }
  def guiButtons(stage: Stage): HBox = {

    val bt_help = new Button("Help") {
      onAction = _ => {
        val alert = new Alert(AlertType.Information) {
          initOwner(stage)
          title = "Help"
          headerText = "Spielablauf"
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
    val bt_undo = new Button("Undo") {
      onAction = _ => if (ctr.currState == State.BEGIN) ctr.undo()
    }

    val bt_redo = new Button("Redo") {
      onAction = _ => if (ctr.currState == State.BEGIN) ctr.redo()
    }

    val bt_quit = new Button("Quit") {
      onAction = _ => Platform.exit()
    }

    new HBox {
      spacing = 20
      layoutX = 20
      layoutY = 60
      children = List(bt_help, bt_undo, bt_redo, bt_quit)
    }
  }

}
