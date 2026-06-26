package de.htwg.se.skyjo.aView.Gui

import java.util.concurrent.atomic.AtomicBoolean

import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.Node
import scalafx.scene.layout.{HBox, Pane}
import scalafx.scene.control.{Alert, Button}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.ButtonType

import de.htwg.se.skyjo.util.Observer
import de.htwg.se.skyjo.model.State
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface

object Gui extends JFXApp3 with Observer {

  private var controllerOpt: Option[ControllerInterface] = None
  private var boardLayer: Pane = _
  private var controlsBox: HBox = _
  private var b: BoardView = _
  private val rendering = new AtomicBoolean(false)

  def init(controller: ControllerInterface): Unit =
    controllerOpt = Some(controller)

  private def ctr: ControllerInterface =
    controllerOpt.getOrElse {
      throw new IllegalStateException(
        "Gui.init(controller) must be called before Gui.main(args)"
      )
    }

  override def start(): Unit = {
    boardLayer = new Pane()
    controlsBox = guiButtons()
    b = new BoardView(ctr, boardLayer)

    stage = new JFXApp3.PrimaryStage {
      title = "Skyjo"
      scene = new Scene {
        root = new Pane {
          style = "-fx-background-color: darkgreen;"
          children = Seq(boardLayer, controlsBox)
        }
      }
    }

    stage.show()
    ctr.add(this)
    update("")
  }

  override def update(choose: String): Boolean = {
    if !rendering.compareAndSet(false, true) then return true

    Platform.runLater {
      try {
        val gs = ctr.getGameState

        b.termBoard = gs.boards(gs.plIdx)
        b.aDeck = gs.deck
        b.aDisc = gs.disc
        b.currentState = gs.currentState

        b.manyCards = b.BOARD_INIT(false)
        val boardUI: Seq[Node] = b.viewBoard()

        gs.previewDeckCard match {
          case Some(card) =>
            b.vDeck.cCard = card
            b.vDeck.turned = true
          case None =>
            b.vDeck.cCard = ctr.toCard(ctr.peekUpperCard)
            b.vDeck.turned = false
        }

        ctr.getDiscCard() match {
          case Some(card) =>
            b.vDiscard.cCard = card
            b.vDiscard.turned = true
          case None =>
            b.vDiscard.cCard = ctr.toCard(0)
            b.vDiscard.turned = false
        }

        b.vDiscard.uptCardView
        b.vDeck.uptCardView
        b.manyCards.foreach(_.uptCardView)

        boardLayer.children = boardUI
      } finally {
        rendering.set(false)
      }
    }

    true
  }

  private def initOwnerIfAvailable(alert: Alert): Unit =
    Option(stage).foreach(alert.initOwner)

  def guiButtons(): HBox = {
    val ht = 20
    val wt = 60

    val btHelp = new Button("Help") {
      onAction = _ => {
        val alert = new Alert(AlertType.Information) {
          title = "Help"
          headerText = "Viewing Help"
          contentText =
            "Rules:\n" +
              "STATE: BEGIN\n" +
              "0.1 DISC SELECT\n" +
              "0.2 DECK SELECT\n" +
              "0.3 COMMANDS => EXECUTING COMMAND\n\n" +
              "STATE: MID\n" +
              "0.1.1 / 0.2.1 BOARDCARD SELECT\n" +
              "0.1.2 SWITCH DISC w/ BOARDCARD\n" +
              "0.2.2 SWITCH DECK w/ BOARDCARD\n\n" +
              "STATE: END\n" +
              "0.3.2 SELECT BOARDCARD => turn BOARDCARD\n"
        }
        initOwnerIfAvailable(alert)
        alert.showAndWait()
      }
    }

    val btUndo = new Button("undo")
    btUndo.tooltip = "Undoing Turn"
    btUndo.setPrefHeight(ht)
    btUndo.setPrefWidth(wt)
    btUndo.onMouseClicked = _ => {
      if b.currentState == State.BEGIN then ctr.undo()
    }

    val btRedo = new Button("redo")
    btRedo.tooltip = "Redoing last Turn"
    btRedo.setPrefHeight(ht)
    btRedo.setPrefWidth(wt)
    btRedo.onMouseClicked = _ => {
      if b.currentState == State.BEGIN then ctr.redo()
    }

    val buttonTypeJson = new ButtonType("Json")
    val buttonTypeXml = new ButtonType("Xml")

    val btSave = new Button("save")
    btSave.tooltip = "Save game"
    btSave.setPrefHeight(ht)
    btSave.setPrefWidth(wt)
    btSave.onMouseClicked = _ => {
      val alert = new Alert(AlertType.Confirmation) {
        title = "Saving"
        headerText = "Saving current game state inside ./saves/"
        contentText = "Save as..."
        buttonTypes = Seq(buttonTypeJson, buttonTypeXml, ButtonType.Cancel)
      }
      initOwnerIfAvailable(alert)

      alert.showAndWait() match {
        case Some(`buttonTypeJson`) => ctr.json_save
        case Some(`buttonTypeXml`)  => ctr.xml_save
        case _                      => ()
      }
    }

    val btLoad = new Button("load")
    btLoad.tooltip = "Load game"
    btLoad.setPrefHeight(ht)
    btLoad.setPrefWidth(wt)
    btLoad.onMouseClicked = _ => {
      val alert = new Alert(AlertType.Confirmation) {
        title = "Loading"
        headerText = "Loading game state from ./saves/"
        contentText = "Load from..."
        buttonTypes = Seq(buttonTypeJson, buttonTypeXml, ButtonType.Cancel)
      }
      initOwnerIfAvailable(alert)

      alert.showAndWait() match {
        case Some(`buttonTypeJson`) => ctr.json_load(b)
        case Some(`buttonTypeXml`)  => ctr.xml_load(b)
        case _                      => ()
      }
    }

    val btQuit = new Button("quit")
    btQuit.tooltip = "Quit game"
    btQuit.setPrefHeight(ht)
    btQuit.setPrefWidth(wt)
    btQuit.onMouseClicked = _ => {
      val alert = new Alert(AlertType.Confirmation) {
        title = "Quitting Game"
        headerText = "Do you really want to quit?"
        contentText = "Your progress will not be saved."
      }
      initOwnerIfAvailable(alert)

      val result = alert.showAndWait()
      if (result.contains(ButtonType.OK)) {
        Platform.exit()
      }
    }

    new HBox {
      spacing = 50
      children = List(btHelp, btUndo, btRedo, btSave, btLoad, btQuit)
    }
  }
}



