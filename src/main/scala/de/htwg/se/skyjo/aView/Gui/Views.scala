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
import sbt.testing.EventHandler
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

case class BoardView(ctr: ControllerInterface) extends Observer {
  ctr.add(this)

  def update(choose: String): Boolean = true

  // val _med: Mediator = new ConcreteMediator()
  val _med = ctr.getMediator
  val padding = 30
  var aDeck = ctr.getDeck
  var termBoard = ctr.getBrds(ctr.getPlIdx)
  var aDisc = ctr.getDisc
  var memStack = ctr.currMemento

  case class CardView(
      x_pos: Int,
      y_pos: Int,
      h: Int = 198,
      w: Int = 132,
      colour: Color = Color.DarkBlue,
      var cCard: CardInterface,
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
        case State.BEGIN => {
          selected = !selected

          if selected && (isDisc || isDeck) then
            currentState = currentState.nextState()

          if isDisc then currentState.pre = "DISC"
          else if isDeck then
            currentState.pre = "DECK"
            aDeck = new Deck(aDeck.getDeckCards, ctr, aDeck.turnUpperCard)
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
            if vDiscard.turned then
              turned = true
              // println("switching Disc / Board")
              switchDiscB()
            else println("Cannot get from Empty DiscardPile")
          else if currentState.pre.compareTo("DECK") == 0 then
            turned = true
            if isDisc then
              // println("switching Deck / Disc")
              vDiscard.switchDeckDisc()
            else if !isDisc && !isDeck then
              // println("switching Deck / Board")
              switchDeckB()

          selected = !selected
          vDiscard.turned =
            if aDisc.toString().compareTo("Disc") == 0 then false else true
          vDiscard.selected = false
          vDiscard.uptCardView

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
      // println(ctr.getBoard.flatten)
      // println(s"aDisc:\n${aDisc.toString()}")
      // println(s"aDeck:\n${aDeck.toString()}")
      if ctr.getBoard.forall(row => row.forall(c => c.isTurned == true)) then
        popup(termBoard)
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
  var manyCards: Seq[BoardView#CardView] = BOARD_INIT()

  def BOARD_INIT(begin: Boolean = true): Seq[BoardView#CardView] = {
    val (cols, rows) = ctr.getSize
    val flattened = termBoard.getBoard.flatten
    // def BOARD_INIT(begin: Boolean = true): Seq[CardView] = {
    // println(ctr.getBoard.flatten.map(c => c.trueCopy))
    val br: Seq[CardView] = {
      for {
        row <- 0 until rows
        col <- 0 until cols
      } yield {
        val index = rows * col + col
        new CardView(
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
          switchDiscB = () => {
            val preDisc = ctr.toCard(aDisc.toString())
            val (tmpDisc, tmpBoard) = (termBoard
              .switch(preDisc, index): @unchecked)
            aDisc = DiscardPile(ctr, tmpDisc.toString())
            vDiscard.cCard = ctr.getBoard.flatten.apply(index)
            val preBoard = termBoard
            termBoard = tmpBoard
            manyCards.apply(index).cCard = preDisc

            memStack.save(
              Memento(
                false,
                // aDeck.getUpperCard(),
                preDisc,
                index,
                preBoard.getBoardCard(index),
                DiscardPile(ctr, preDisc.toString()),
                preBoard.getBoardCard(index).isTurned
              )
            )
          },
          switchDeckB = () => {
            val preDisc = aDisc
            aDisc = DiscardPile(ctr, termBoard.getBoardCard(index).toString())
            val turnedDeck: DeckInterface =
              new Deck(aDeck.getDeckCards, ctr, aDeck.getCard.toString())
            val (tmpDeckCard, tmpBoard) = (termBoard.switch(
              ctr.toCard(turnedDeck.toString()),
              index
            ): @unchecked)
            val tmpDeck =
              Deck(turnedDeck.getDeckCards, ctr, tmpDeckCard.toString())
            vDiscard.cCard = ctr.toCard(aDisc.toString())
            val preBoard = termBoard
            termBoard = tmpBoard
            manyCards.apply(index).cCard = ctr.toCard(aDeck.toString())
            aDeck = new Deck(tmpDeck.getDeckCards, ctr)
            vDeck.cCard = ctr.toCard(aDeck.turnUpperCard)

            memStack.save(
              Memento(
                true,
                ctr.toCard(turnedDeck.toString()),
                index,
                preBoard.getBoardCard(index),
                preDisc,
                preBoard.getBoardCard(index).isTurned
              )
            )
          },
          endTurn = () => {
            termBoard = termBoard.turnBoardCard(index)
            manyCards.apply(index).turned = true
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

  val vDiscard: CardView =
    (CardView(
      100,
      720,
      colour = Color.SteelBlue,
      cCard = ctr.toCard(0).falseCopy,
      med = _med,
      isDisc = true,
      switchDeckDisc = () => {
        val turnedDeck =
          new Deck(aDeck.getDeckCards, ctr, aDeck.turnUpperCard)
        val toDisc = aDisc.putToDiscardPile(turnedDeck)
        aDisc = toDisc._1
        aDeck = toDisc._2

        vDiscard.cCard = ctr.toCard(aDisc.toString())
        vDeck.cCard = ctr.toCard(aDeck.turnUpperCard)

        currentState = currentState.nextState()
        currentState.pre = "BOARD"
      },
      switchDiscB = () => {},
      switchDeckB = () => {},
      endTurn = () => {}
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
      400,
      720,
      colour = Color.SteelBlue,
      cCard = ctr.toCard(aDeck.getCard).falseCopy,
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

def popup(b: BoardInterface) = {
  val finished = new Alert(AlertType.Information) {
    // initOwner(stage)
    title = "finished window"
  }
  finished.headerText = "FINISHED"
  finished.contentText =
    s"SUM:  ${b.getBoard.flatten.map(c => c.getValue).fold(0)((x, y) => x + y).toString()}"
  val re = finished.showAndWait()
  re match {
    case Some(ButtonType.OK) => {
      println("closed FINISHED Box")
      Platform.exit()
    }
    case _ => {}
  }
}
