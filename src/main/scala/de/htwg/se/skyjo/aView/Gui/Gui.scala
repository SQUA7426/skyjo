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
// import javafx.scene.control.Alert
import scalafx.scene.control.ButtonType.*
// import javafx.scene.control.ButtonType

import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.ButtonType
import scalafx.application.Platform

import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.DeckImplementation.*
import de.htwg.se.skyjo.model.BoardImplementation.*
import de.htwg.se.skyjo.model.DiscardPileImplementation.*
import de.htwg.se.skyjo.model.CardInterface
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.util.Observer
import de.htwg.se.skyjo.model.{GameState, State}

import scalafx.scene.text.Font
import scalafx.scene.layout.StackPane
import scalafx.geometry.Pos
import scalafx.scene.layout.Background
import scalafx.geometry.Insets
import sbt.testing.EventHandler
import scalafx.scene.Node

import de.htwg.se.skyjo.aView.Gui.{BoardView, fontname}

case class Gui(ctr: ControllerInterface) extends Observer with JFXApp3 {
  ctr.add(this)
  val boardLayer = new Pane()
  val b = new BoardView()

  override def start(): Unit = {
    print(b.termBoard.toString())
    stage = new JFXApp3.PrimaryStage {
      title.value = "ScalaFX Skyjo"
      width = 800
      height = 1200
      scene = new Scene {

        root = new Pane {
          style = "-fx-background-color: darkgreen;"
          boardLayer.children = b.viewBoard()
          children = Seq(boardLayer, (guiButtons(stage)))
        }
      }
      update
    }
  }

  def guiButtons(stage: Stage): HBox = {
    val ht = 20
    val wt = 60

    val bt_help = new Button("help")
    bt_help.tooltip = "Viewing Help"
    bt_help.setPrefHeight(ht)
    bt_help.setPrefWidth(wt)
    bt_help.onMouseClicked = _ => {
      val help = new Alert(AlertType.Information) {
        initOwner(stage)
        title = "Help window"
      }
      help.headerText = "HELP"
      help.contentText = (
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
      val re = help.showAndWait()
      re match {
        case Some(ButtonType.OK) => println("closed Help Box")
        case _                   => {}
      }
    }

    val bt_undo = new Button("undo")
    bt_undo.tooltip = "Undoing Turn"
    bt_undo.setPrefHeight(ht)
    bt_undo.setPrefWidth(wt)
    bt_undo.onMouseClicked = _ => {
    }

    val bt_redo = new Button("redo")
    bt_redo.tooltip = "Redoing last Turn"
    bt_redo.setPrefHeight(ht)
    bt_redo.setPrefWidth(wt)
    bt_redo.onMouseClicked = _ => {
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

  override def update: Boolean = true
}

