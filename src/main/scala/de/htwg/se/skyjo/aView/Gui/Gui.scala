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

// Konstanten global oder im Companion Object
object UIConstants {
  val cardWidth = 132
  val cardHeight = 198
  val padding = 30
  val fontname = "Arial" // Oder "Parisienne" falls verfügbar
}

object Gui extends JFXApp3 with Observer {

  val boardLayer = new Pane()
  // Wir erlauben null initial, aber fordern Init vor Start
  var ctr: ControllerInterface = _
  var b: BoardView = _
  var currentStage: Stage = _

  def init(control: ControllerInterface): Unit = {
    ctr = control
    ctr.add(this)
  }

  override def start(): Unit = {
    require(
      ctr != null,
      "Controller muss vor start() mit Gui.init() gesetzt werden!"
    )

    b = new BoardView(ctr)

    stage = new JFXApp3.PrimaryStage {
      // 1. Wir speichern 'this' (die Stage) in einer lokalen Variable
      val mainStage = this

      title.value = "ScalaFX Skyjo"
      width = 1000
      height = 1000

      scene = new Scene {
        root = new Pane {
          style = "-fx-background-color: darkgreen;"

          boardLayer.children = b.viewBoard()

          // 2. Hier nutzen wir 'mainStage' statt 'this'
          children = Seq(boardLayer, guiButtons(mainStage))
        }
      }
    }
    // currentStage für globale Referenzen setzen (falls nötig)
    currentStage = stage
  }

  // --- Observer Update ---
  override def update: Boolean = {
    Platform.runLater {
      // 1. Die BoardView Instanz anweisen, neue Daten vom Controller zu holen
      b.syncWithController()

      // 2. Die Pane 'boardLayer' leeren und mit neuen CardViews befüllen
      // b.viewBoard() erzeugt jetzt CardViews mit den Werten (wie der 8),
      // die du gerade im Log gesehen hast.
      boardLayer.children = b.viewBoard()

      // 3. Optional: Buttons neu positionieren oder disablen (z.B. Undo/Redo)
      // Wenn die Buttons in einem eigenen Container liegen,
      // muss dieser hier nicht zwingend angefasst werden.
    }
    true
  }
  def guiButtons(stage: Stage): HBox = {

    val bt_help = new Button("Help") {
      onAction = _ => {
        val alert = new Alert(AlertType.Information) {
          initOwner(stage) // Das funktioniert jetzt korrekt mit der Stage
          title = "Help"
          // ... Rest des Codes
          headerText = "Spielablauf"
          contentText = """Phase BEGIN:
            | - Klicke Deck um Karte zu ziehen
            | - Klicke Disc um oberste Karte zu nehmen
            |
            |Phase MID:
            | - Klicke auf eine Board-Karte zum Tauschen
            | - (Falls Deck gezogen): Klicke auf Disc zum Wegwerfen
            |
            |Phase END:
            | - Klicke eine verdeckte Karte zum Umdrehen
            |""".stripMargin
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
      layoutX = 100
      layoutY = 850 // Unten positionieren
      children = List(bt_help, bt_undo, bt_redo, bt_quit)
    }
  }

}
