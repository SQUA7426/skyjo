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
import scala.util.{Try, Success, Failure}

import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.ButtonType
import scalafx.application.Platform

import scalafx.scene.text.Font
import scalafx.scene.layout.StackPane
import scalafx.geometry.Pos
import scalafx.scene.layout.Background
import scalafx.geometry.Insets
import de.htwg.se.skyjo.util.{
  MoveCaretaker,
  Memento,
  ConcreteMediator,
  Mediator,
  Observer
}
import de.htwg.se.skyjo.model.{
  BoardInterface,
  DeckInterface,
  DiscardPileInterface,
  CardInterface,
  GameState,
  State
}
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{
  Card,
  Board,
  Deck,
  DiscardPile
}
import scalafx.scene.Node
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.util.Observer

val fontname = "Parisienne"

case class BoardView(ctr: ControllerInterface, var boardPane: Pane) {

  var currentState: State = ctr.currState

  val padding = 30
  var aDeck = ctr.getDeck
  var termBoard = ctr.getBrds(ctr.getPlIdx)
  var aDisc = ctr.getDisc

  def syncBoard(board: BoardInterface): BoardInterface =
    val tmpBrd = manyCards.map(_.cCard)
    val (cols, rows) = ctr.getSize

    var tmpEndVec = Vector.empty[Vector[CardInterface]]
    var tmpVec = Vector.empty[CardInterface]
    for {
      row <- 0 until rows
      col <- 0 until cols
    } {
      val idx = row * cols + col
      val tmpCard = new Card(tmpBrd(idx).getValue, tmpBrd(idx).isTurned)
      tmpVec = tmpVec :+ tmpCard
      if (idx + 1) % 4 == 0 then
        tmpEndVec = tmpEndVec :+ tmpVec
        tmpVec = Vector.empty[CardInterface]
    }
    Board(cols, rows, tmpEndVec)

  def uptBoardPane(r: Int, c: Int) =
    manyCards = BOARD_INIT(false)

    val flattenTerm: Vector[CardInterface] = termBoard.getBoard.flatten

    for i <- 0 until flattenTerm.size do
      manyCards(i).cCard = flattenTerm(i)
      manyCards(i).turned = flattenTerm(i).isTurned

    val newUI: Seq[Node] = viewBoard()
    boardPane.children_=(newUI)
    vDiscard.uptCardView
    vDeck.uptCardView
    manyCards.map(_.uptCardView)

  case class CardView(
      idx: Int,
      x_pos: Int,
      y_pos: Int,
      h: Int = 198,
      w: Int = 132,
      colour: Color = Color.DarkBlue,
      var cCard: CardInterface,
      var turned: Boolean = false,
      val isDisc: Boolean = false,
      val isDeck: Boolean = false,
      switchDeckDisc: () => Unit,
      switchDiscB: () => Unit,
      switchDeckB: () => Unit
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
      if turned then cCard.getValue.toString()
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
        if turned then cCard.getValue.toString()
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
        case State.BEGIN =>
          println(
            s"CardView Klick BEGIN isDeck=$isDeck isDisc=$isDisc idx=$idx"
          )
          if isDeck then ctr.guiPreviewDeckCard()
          else if isDisc then ctr.guiSelectDisc()
          else ()
        case State.MID =>
          if !isDeck && !isDisc then
            if currentState.pre == "DECK" then ctr.guiConfirmDeckSwitch(idx)
            else if currentState.pre == "DISC" then ctr.drawFromDisc(idx)
            else ()
          else if isDisc && currentState.pre == "DECK" then
            ctr.guiDeckToDisc()
          else ()

        case State.END =>
          if idx >= 0 then
            ctr.guiTurnBrdCard(idx)
        case _ => ()
      }

      if termBoard.getBoard.forall(row => row.forall(c => c.isTurned == true))
      then popup(ctr)
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
  var manyCards: Seq[BoardView#CardView] = BOARD_INIT(false)

  def BOARD_INIT(begin: Boolean = true): Seq[BoardView#CardView] = {
    val (cols, rows) = termBoard.getBoard.head.size -> termBoard.getBoard.size
    val flattened = termBoard.getBoard.flatten
    val br: Seq[CardView] = {
      for {
        row <- 0 until rows
        col <- 0 until cols
      } yield {
        val index = row * cols + col
        new CardView(
          idx = index,
          x_pos = (padding + ((padding + 132) * col)),
          y_pos = (padding + ((padding + 198) * row)),
          cCard = ctr.getBoard.flatten.apply(index),
          turned =
            if begin then false
            else
              ctr.getBoard.flatten
                .apply(index)
                .isTurned
          ,
          switchDeckDisc = () => {},
          switchDiscB = () => {},
          switchDeckB = () => {}
        )
      }
    }
    br
  }

  def viewBoard(): Seq[StackPane] = {
    manyCards.map(_.view) :+ viewDisc() :+ viewDeck()
  }

  // VIEW DISC
  val vDiscard: CardView =
    (CardView(
      -1,
      100,
      720,
      colour = Color.SteelBlue,
      cCard = ctr.toCard(0).falseCopy,
      isDisc = true,
      switchDeckDisc = () => {},
      switchDiscB = () => {},
      switchDeckB = () => {}
    ))

  def viewDisc(): StackPane = {
    new StackPane {
      layoutX = vDiscard.x_pos
      layoutY = vDiscard.y_pos
      children = Seq(vDiscard.cardShape, vDiscard.label)
    }
  }

  val vDeck = vDECKINIT

  def vDECKINIT: CardView = {
    CardView(
      -2,
      400,
      720,
      colour = Color.SteelBlue,
      cCard = ctr.toCard(aDeck.getCard.get).falseCopy,
      isDeck = true,
      switchDeckDisc = () => {},
      switchDiscB = () => {},
      switchDeckB = () => {}
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

def popup(ctr: ControllerInterface) = {
  val finished = new Alert(AlertType.Information) {
    title = "finished window"
  }
  var arr = Seq.empty[String]
  for i <- 0 until ctr.getBrds.size do
    arr = arr :++ Seq(
      s"Player $i: ${ctr.getBrds(i).getBoard.flatten.map(c => c.getValue).fold(0)((x, y) => x + y).toString()}\n"
    )

  var str = ""
  for j <- 0 until arr.size do str = str :++ arr(j)
  println(str)

  finished.headerText = "FINISHED"
  finished.contentText = str
  val re = finished.showAndWait()
  re match {
    case Some(ButtonType.OK) => {
      println("closed FINISHED Box")
      Platform.exit()
    }
    case _ => {}
  }
}
