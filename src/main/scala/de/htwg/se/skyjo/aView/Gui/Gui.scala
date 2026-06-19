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
        val gs = ctr.getGameState

        b.termBoard = gs.boards(gs.plIdx)
        b.aDeck = gs.deck
        b.aDisc = gs.disc
        b.currentState = gs.currentState

        b.manyCards = b.BOARD_INIT(false)
        val boardUI: Seq[Node] = b.viewBoard()

        gs.previewDeckCard match
          case Some(card) =>
            b.vDeck.cCard = card
            b.vDeck.turned = true
          case None =>
            b.vDeck.cCard = ctr.toCard(b.aDeck.turnUpperCard)
            b.vDeck.turned = false

        val discOpt = ctr.getDiscCard()

        discOpt match {
          case Some(card) =>
            b.vDiscard.cCard = card
            b.vDiscard.turned = true
          case None => {
            b.vDiscard.cCard = ctr.toCard(0)
            b.vDiscard.turned = false
          }
        }

        b.vDiscard.uptCardView
        b.vDeck.uptCardView
        b.manyCards.foreach(_.uptCardView)

        val newUI: Seq[Node] = boardUI :+ guiButtons(stage)
        boardLayer.children_=(newUI)
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
      if b.currentState == State.BEGIN then ctr.undo()
    }

    val bt_redo = new Button("redo")
    bt_redo.tooltip = "Redoing last Turn"
    bt_redo.setPrefHeight(ht)
    bt_redo.setPrefWidth(wt)

    bt_redo.onMouseClicked = _ => {
      if b.currentState == State.BEGIN then ctr.redo()
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
        case Some(ButtonTypeJson) => { ctr.json_save; }
        case Some(ButtonTypeXml)  => { ctr.xml_save; }
        case _                    => { println("Canceled Saving."); }
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
        case Some(ButtonTypeJson) =>
          ctr.json_load(b)
        case Some(ButtonTypeXml) =>
          ctr.xml_load(b)
        case _ => { println("Canceled Loading."); }
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
