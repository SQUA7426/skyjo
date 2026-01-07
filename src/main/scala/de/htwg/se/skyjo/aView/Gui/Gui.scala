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

import de.htwg.se.skyjo.model.{
  Board,
  Card,
  Deck,
  DiscardPile,
  fillBoard,
  getBoardCard,
  toCard
}
import de.htwg.se.skyjo.util.{Mediator, ConcreteMediator}
import scala.collection.Seq
import scala.collection.mutable.Buffer
import de.htwg.se.skyjo.util.{SupportCommand, Command}
import de.htwg.se.skyjo.controller.ControllerComponent.Controller

import scalafx.scene.text.Font
import scalafx.scene.layout.StackPane
import scalafx.geometry.Pos
import scalafx.scene.layout.Background
import scalafx.geometry.Insets
import sbt.testing.EventHandler
import de.htwg.se.skyjo.util.MoveCaretaker
import de.htwg.se.skyjo.util.Memento
import scalafx.scene.Node

val fontname = "Parisienne"

enum State(str: String = "BEGIN", var pre: String = "BOARD") {
  def nextState(): State = {
    if str == "BEGIN" then State.MID
    else State.END
  }
  case BEGIN extends State()
  case MID extends State("MID")
  case END extends State("END")
  def reset(): State = BEGIN
}
var currentState: State = State.BEGIN

case class BoardView() {

  val _med: Mediator = new ConcreteMediator()
  val padding = 30
  var aDeck: Deck = Deck(_med)
  var termBoard: Board = fillBoard(_med, 4, 3, aDeck)._1
  var aDisc: DiscardPile = new DiscardPile(_med, "Disc")
  var memStack: MoveCaretaker = new MoveCaretaker(_med)

  case class CardView(
      x_pos: Int,
      y_pos: Int,
      h: Int = 198,
      w: Int = 132,
      colour: Color = Color.DarkBlue,
      var cCard: Card,
      var turned: Boolean = false,
      val med: Mediator = _med,
      val isDisc: Boolean = false,
      val isDeck: Boolean = false,
      switchDeckDisc: () => Unit,
      switchDiscB: () => Unit,
      switchDeckB: () => Unit,
      endTurn: () => Unit
  ) {
    val arcH = 30
    val arcW = arcH

    val label: Text = createLabel
    var selected = false

    override def toString(): String = cCard.toString()

    val cardShape = new Rectangle {
      height = h; width = w; x = x_pos; y = y_pos; arcHeight = arcH;
      arcWidth = arcW; fill = colour
    }

    def createLabel: Text = new Text(
      if turned then cCard.value.toString()
      else {
        if isDeck then "Deck"
        else if isDisc then "Disc"
        else ""
      }
    ) {
      fill = if turned then Color.Black else Color.DarkBlue
      font = Font(fontname, size = h * 0.3)
      alignmentInParent = Pos.Center
    }

    def uptLabel(): Unit = {
      label.text =
        if turned then cCard.value.toString()
        else {
          if isDeck then "Deck"
          else if isDisc then "Disc"
          else ""
        }
      label.fill = if turned then Color.Black else Color.DarkBlue
      label.font = Font(fontname, size = h * 0.3)
    }

    cardShape.onMouseClicked = (_: MouseEvent) => {
      currentState match {
        case State.BEGIN => {
          selected = !selected

          if selected && (isDisc || isDeck) then
            currentState = currentState.nextState()

          if isDisc then currentState.pre = "DISC"
          else if isDeck then
            currentState.pre = "DECK"
            aDeck = new Deck(_med, aDeck.deck, aDeck.turnUpperCard())
            turned = true
          else
            selected = !selected
            currentState.pre = "BOARD"
          // println(s"changed to State: ${currentState.toString}")
          uptCardView
        }
        case State.MID => {
          selected = !selected

          if currentState.pre.compareTo("DISC") == 0 then
            if aDiscard.turned then
              turned = true
              // println("switching Disc / Board")
              switchDiscB()
            else println("Cannot get from Empty DiscardPile")
          else if currentState.pre.compareTo("DECK") == 0 then
            turned = true
            if isDisc then
              // println("switching Deck / Disc")
              aDiscard.switchDeckDisc()
            else if !isDisc && !isDeck then
              // println("switching Deck / Board")
              switchDeckB()

          selected = !selected
          aDiscard.turned =
            if aDisc.discPile.compareTo("Disc") == 0 then false else true
          aDiscard.selected = false
          aDiscard.uptCardView

          vDeck.selected = false
          vDeck.turned = false
          vDeck.uptCardView

          uptCardView
          if currentState == State.MID then
            currentState = currentState.reset()
            // println(s"changed to State: ${currentState.toString}")
        }
        case State.END => {
          endTurn()
          uptCardView
        }
      }
      // println(termBoard.brd.flatten)
      // println(s"aDisc:\n${aDisc.discPile}")
      // println(s"aDeck:\n${aDeck.toString()}")
      if termBoard.brd.forall(row => row.forall(c => c.isTurned() == true)) then popup
    }

    def uptCardView: Unit = {
      uptLabel()
      cardShape.fill = {
        if (selected && turned == false) {
          Color.LightGray
        }
        if selected then Color.LightSalmon
        else {
          if !turned then colour else Color.LightGray
        }
      }
    }

    val view: StackPane = new StackPane {
      layoutX = x_pos
      layoutY = y_pos
      children = Seq(cardShape, label)
    }
  }

  // BOARDVIEW
  var manyCards: Seq[CardView] = BOARD_INIT()

  def BOARD_INIT(begin: Boolean = true): Seq[CardView] = {
    println(termBoard.brd.flatten.map(c => c.trueCopy()))
    val br: Seq[CardView] = {
      for {
        row <- 0 until termBoard.ySize
        col <- 0 until termBoard.xSize
      } yield {
        new CardView(
          x_pos = (padding + ((padding + 132) * col)),
          y_pos = (padding + ((padding + 198) * row)),
          cCard =
            termBoard.brd.flatten.apply((termBoard.ySize * row) + col + row),
          turned =
            if begin then false
            else
              termBoard.brd.flatten
                .apply((termBoard.ySize * row) + col + row)
                .turned
          ,
          switchDeckDisc = () => {},
          switchDiscB = () => {
            val preDisc = toCard(_med, aDisc.discPile)
            val (tmpDisc: DiscardPile, tmpBoard: Board) = (termBoard
              .switch(aDisc, (termBoard.ySize * row) + col + row): @unchecked)
            aDisc = tmpDisc
            aDiscard.cCard =
              termBoard.brd.flatten.apply((termBoard.ySize * row) + col + row)
            val preBoard = termBoard
            termBoard = tmpBoard
            manyCards.apply((termBoard.ySize * row) + col + row).cCard = preDisc

            memStack.save(
              Memento(
                false,
                // aDeck.getUpperCard(),
                preDisc,
                (termBoard.ySize * row) + col + row,
                getBoardCard(preBoard, (preBoard.ySize * row) + col + row),
                DiscardPile(_med, preDisc.toString()),
                getBoardCard(
                  preBoard,
                  (preBoard.ySize * row) + col + row
                ).turned
              )
            )
          },
          switchDeckB = () => {
            val preDisc = aDisc
            aDisc = DiscardPile(
              _med,
              getBoardCard(termBoard, ((termBoard.ySize * row) + col + row))
                .toString()
            )
            val turnedDeck =
              new Deck(_med, aDeck.deck, aDeck.getUpperCard().toString())
            val (tmpDeck: Deck, tmpBoard: Board) = (termBoard.switch(
              turnedDeck,
              (termBoard.ySize * row) + col + row
            ): @unchecked)

            aDiscard.cCard = toCard(_med, aDisc.discPile)
            val preBoard = termBoard
            termBoard = tmpBoard
            manyCards.apply((termBoard.ySize * row) + col + row).cCard =
              toCard(_med, aDeck.toString())
            aDeck = new Deck(_med, tmpDeck.deck)
            vDeck.cCard = toCard(_med, aDeck.turnUpperCard())

            memStack.save(
              Memento(
                true,
                turnedDeck.getUpperCard(),
                (termBoard.ySize * row) + col + row,
                getBoardCard(preBoard, (preBoard.ySize * row) + col + row),
                preDisc,
                getBoardCard(
                  preBoard,
                  (preBoard.ySize * row) + col + row
                ).turned
              )
            )
          },
          endTurn = () => {
            termBoard =
              termBoard.turnBoardCard((termBoard.ySize * row) + col + row)
            manyCards.apply((termBoard.ySize * row) + col + row).turned = true
            currentState = currentState.reset()
          }
        )
      }
    }
    br
  }

  def viewBoard(): Seq[StackPane] = {
    manyCards.map(_.view) :+ viewDisc() :+ viewDeck()
  }

  // VIEW DISC

  val aDiscard: CardView =
    (if aDisc.toString() != "Disc" then
       CardView(
         100,
         720,
         cCard = toCard(_med, aDisc.discPile).falseCopy(),
         med = _med,
         isDisc = true,
         switchDeckDisc = () => {
           val turnedDeck =
             new Deck(_med, aDeck.deck, aDeck.getUpperCard().toString())
           val toDisc = aDisc.putToDiscardPile(turnedDeck)
           aDisc = toDisc._1
           aDeck = toDisc._2

           aDiscard.cCard = toCard(_med, aDisc.discPile)
           vDeck.cCard = toCard(_med, aDeck.turnUpperCard())

           currentState = currentState.nextState()
           currentState.pre = "BOARD"
         },
         switchDiscB = () => {},
         switchDeckB = () => {},
         endTurn = () => {}
       )
     else
       CardView(
         100,
         720,
         colour = Color.SteelBlue,
         cCard = toCard(_med, 0).falseCopy(),
         med = _med,
         isDisc = true,
         switchDeckDisc = () => {
           val turnedDeck =
             new Deck(_med, aDeck.deck, aDeck.getUpperCard().toString())
           val toDisc = aDisc.putToDiscardPile(turnedDeck)
           aDisc = toDisc._1
           aDeck = toDisc._2

           aDiscard.cCard = toCard(_med, aDisc.discPile)
           vDeck.cCard = toCard(_med, aDeck.turnUpperCard())

           currentState = currentState.nextState()
           currentState.pre = "BOARD"
         },
         switchDiscB = () => {},
         switchDeckB = () => {},
         endTurn = () => {}
       )
    )

  def viewDisc(): StackPane = {
    new StackPane {
      layoutX = aDiscard.x_pos
      layoutY = aDiscard.y_pos
      children = Seq(aDiscard.cardShape, aDiscard.label)
    }
  }

  val vDeck = vDECKINIT

  def vDECKINIT: CardView = {
    CardView(
      400,
      720,
      colour = Color.SteelBlue,
      cCard = toCard(_med, aDeck.getUpperCard().value).falseCopy(),
      med = _med,
      isDeck = true,
      switchDeckDisc = () => {},
      switchDiscB = () => {},
      switchDeckB = () => {},
      endTurn = () => {}
    )
  }

  def viewDeck(): StackPane = {
    new StackPane {
      layoutX = vDeck.x_pos
      layoutY = vDeck.y_pos
      children = Seq(vDeck.cardShape, vDeck.label)
    }
  }
}

object Gui extends JFXApp3 {
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
      if currentState == State.BEGIN then
        // println(s"MemStack.undoStack:\n${b.memStack.undoStack.toString()}\n")
        // val ctrl = new Controller(b._med, Array(b.termBoard), b.aDeck, b.aDisc)
        val mem: Memento = b.memStack.undoStack(0)
        b.memStack.undo(mem, b.aDeck, b.termBoard, b.aDisc) match {
          case Some(resBoard, resDeck, resDisc) => {
            b.termBoard = resBoard
            b.aDeck = resDeck
            b.aDisc = resDisc
            b.manyCards = b.BOARD_INIT(false)
            b.vDeck.cCard = toCard(b._med, b.aDeck.turnUpperCard())
            b.aDiscard.cCard = toCard(b._med, b.aDisc.discPile)

            // upt views
            val newUI: Seq[Node] = b.viewBoard() :+ guiButtons(stage)
            boardLayer.children_=(newUI)
            b.aDiscard.uptCardView
            b.vDeck.uptCardView
            b.manyCards.map(_.uptCardView)

            println("\nREDOSTACK\n")
            println(b.memStack.redoStack(0))
            println()
          }
          case None => {}
        }
    }

    val bt_redo = new Button("redo")
    bt_redo.tooltip = "Redoing last Turn"
    bt_redo.setPrefHeight(ht)
    bt_redo.setPrefWidth(wt)
    bt_redo.onMouseClicked = _ => {
      if currentState == State.BEGIN then
        val ctrl = new Controller(b._med, Array(b.termBoard), b.aDeck, b.aDisc)
        val mem: Memento = b.memStack.redoStack(0)
        b.memStack.redo(mem, b.aDeck, b.termBoard, b.aDisc) match {
          case Some(resBoard, resDeck, resDisc) => {
            b.termBoard = resBoard
            b.aDeck = resDeck
            b.aDisc = resDisc
            b.manyCards = b.BOARD_INIT(false)
            b.vDeck.cCard = toCard(b._med, resDeck.turnUpperCard().toString())
            b.aDiscard.cCard = toCard(b._med, b.aDisc.discPile)

            // upt views
            val newUI: Seq[Node] = b.viewBoard() :+ guiButtons(stage)
            boardLayer.children_=(newUI)
            b.aDiscard.uptCardView
            b.vDeck.uptCardView
            b.manyCards.map(_.uptCardView)

            println("\nUNDOSTACK\n")
            val preUndoStack = b.memStack.undoStack(0)
            val tmpMem = Memento(preUndoStack.fromDeck, preUndoStack.replacedCard, preUndoStack.boardIndex, preUndoStack.takenCard, preUndoStack.lastDisc, preUndoStack.lastDisc.isTurned)
            b.memStack.undoStack.clear()
            b.memStack.undoStack.push(tmpMem)
            println(b.memStack.undoStack(0))
            println()
          }
          case None => {}
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

def popup = {
      val finished = new Alert(AlertType.Information) {
        // initOwner(stage)
        title = "finished window"
      }
      finished.headerText = "FINISHED"
      val re = finished.showAndWait()
      re match {
        case Some(ButtonType.OK) => {
          println("closed FINISHED Box")
          Platform.exit()
        }
        case _                   => {}
      }
}
