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
import de.htwg.se.skyjo.model.DeckImplementation.*
import de.htwg.se.skyjo.model.BoardImplementation.*
import de.htwg.se.skyjo.model.DiscardPileImplementation.*
import de.htwg.se.skyjo.model.CardInterface
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.GameState
// import de.htwg.se.skyjo.util.MoveCaretaker
// import de.htwg.se.skyjo.util.Memento

import de.htwg.se.skyjo.model.State

import scalafx.scene.text.Font
import scalafx.scene.layout.StackPane
import scalafx.geometry.Pos
import scalafx.scene.layout.Background
import scalafx.geometry.Insets
import sbt.testing.EventHandler
import scalafx.scene.Node
import de.htwg.se.skyjo.model.DiscardPileInterface
import de.htwg.se.skyjo.model.BoardInterface
import de.htwg.se.skyjo.model.DeckInterface

val fontname = "Parisienne"

case class BoardView() {

  // val _med: Mediator = new ConcreteMediator()
  val padding = 30
  // var aDeck: Deck = Deck(_med)
  // var termBoard: Board = fillBoard(_med, 4, 3, aDeck)._1
  // var aDisc: DiscardPile = new DiscardPile(_med, "Disc")
  // var memStack: MoveCaretaker = new MoveCaretaker(_med)
  val plCount = 1
  val med = new ConcreteMediator()

  val tempState = new GameState(med, Vector.empty, null, null, 0, None)
  val ctr = new Controller(tempState)

  var aDeck: DeckInterface = new Deck(ctr.fullDeck(), ctr)
  var aDisc: DiscardPileInterface = new DiscardPile(ctr)

  val plBoards = Vector.fill(plCount)(new Board(med, 4, 3, Vector.empty))

  ctr.state = new GameState(med, plBoards, aDeck, aDisc, 0, None)
  ctr.setup()

  var termBoard = ctr.getGameState.boards(ctr.getGameState.playerIdx)

  // val tui = new Tui(ctr)

  case class CardView(
      x_pos: Int,
      y_pos: Int,
      h: Int = 198,
      w: Int = 132,
      colour: Color = Color.DarkBlue,
      var cCard: CardInterface,
      var turned: Boolean = false,
      val med: Mediator = med,
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
      if turned then cCard.trueCopy.toString()
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
        if turned then cCard.trueCopy.toString()
        else {
          if isDeck then "Deck"
          else if isDisc then "Disc"
          else ""
        }
      label.fill = if turned then Color.Black else Color.DarkBlue
      label.font = Font(fontname, size = h * 0.3)
    }

    cardShape.onMouseClicked = (_: MouseEvent) => {
      ctr.getGameState.currentState match {
        case State.BEGIN => {
          selected = !selected

          if selected && (isDisc || isDeck) then
            ctr.state = ctr.getGameState.copy(
              currentState = ctr.getGameState.currentState.nextState()
            )

          if isDisc then ctr.getGameState.currentState.pre = "DISC"
          else if isDeck then
            ctr.getGameState.currentState.pre = "DECK"
            aDeck = new Deck(ctr.getDeck, ctr, aDeck.turnUpperCard)
            turned = true
          else
            selected = !selected
            ctr.getGameState.currentState.pre = "BOARD"
          // println(s"changed to State: ${currentState.toString}")
          uptCardView
        }
        case State.MID => {
          selected = !selected

          if ctr.getGameState.currentState.pre.compareTo("DISC") == 0 then
            if aDiscard.turned then
              turned = true
              // println("switching Disc / Board")
              switchDiscB()
            else println("Cannot get from Empty DiscardPile")
          else if ctr.getGameState.currentState.pre.compareTo("DECK") == 0 then
            turned = true
            if isDisc then
              // println("switching Deck / Disc")
              aDiscard.switchDeckDisc()
            else if !isDisc && !isDeck then
              // println("switching Deck / Board")
              switchDeckB()

          selected = !selected
          aDiscard.turned = if aDisc.toString() == "Disc" then false else true
          aDiscard.selected = false
          aDiscard.uptCardView

          vDeck.selected = false
          vDeck.turned = false
          vDeck.uptCardView

          uptCardView
          if ctr.getGameState.currentState == State.MID then
            ctr.state = ctr.getGameState.copy(
              currentState = ctr.getGameState.currentState.reset()
            )
            // println(s"changed to State: ${ctr.getGameState.currentState.toString}")
        }
        case State.END => {
          endTurn()
          uptCardView
        }
      }
      // println(termBoard.brd.flatten)
      // println(s"aDisc:\n${aDisc.discPile}")
      // println(s"aDeck:\n${aDeck.toString()}")

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
          case _ => {}
        }
      }

      if ctr.getBoard.forall(row => row.forall(_.isTurned)) then popup
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
    println(ctr.getBoard.flatten.map(c => c.trueCopy))
    val (xSize, ySize) = termBoard.getSize
    val br: Seq[CardView] = {
      for {
        row <- 0 until ySize
        col <- 0 until xSize
      } yield {
        new CardView(
          x_pos = (padding + ((padding + 132) * col)),
          y_pos = (padding + ((padding + 198) * row)),
          cCard = ctr.getBoard.flatten.apply((ySize * row) + col + row),
          turned =
            if begin then false
            else
              ctr.getBoard.flatten
                .apply((ySize * row) + col + row)
                .isTurned
          ,
          switchDeckDisc = () => {},
          switchDiscB = () => {
            val preDisc = ctr.toCard(aDisc.toString())
            val (tmpDisc: DiscardPileInterface, tmpBoard: BoardInterface) =
              (termBoard
                .switch(preDisc, (ySize * row) + col + row): @unchecked)
            aDisc = tmpDisc
            aDiscard.cCard =
              ctr.getBoard.flatten.apply((ySize * row) + col + row)
            val preBoard = termBoard
            termBoard = tmpBoard
            manyCards.apply((ySize * row) + col + row).cCard = preDisc

            // memStack.save(
            //   Memento(
            //     false,
            //     // aDeck.getUpperCard(),
            //     preDisc,
            //     (termBoard.ySize * row) + col + row,
            //     getBoardCard(preBoard, (preBoard.ySize * row) + col + row),
            //     DiscardPile(_med, preDisc.toString()),
            //     getBoardCard(
            //       preBoard,
            //       (preBoard.ySize * row) + col + row
            //     ).turned
            //   )
            // )
            val preStage = ctr.getGameState.copy(
              boards = ctr.getGameState.boards
                .updated(ctr.getGameState.playerIdx, preBoard),
              disc = DiscardPile(ctr, preDisc.toString()),
              drawnCard = Some(preDisc)
            )
            ctr.save(preStage)
          },
          switchDeckB = () => {
            val preDisc = aDisc
            aDisc = DiscardPile(
              ctr,
              termBoard
                .getBoardCard(((ySize * row) + col + row))
                .toString()
            )
            val turnedDeck =
              new Deck(ctr.getDeck, ctr, ctr.draw()._1.toString())
            val (tmpDeck: Deck, tmpBoard: Board) = (termBoard.switch(
              ctr.toCard(turnedDeck.toString()),
              (ySize * row) + col + row
            ): @unchecked)

            aDiscard.cCard = ctr.toCard(aDisc.toString())
            val preBoard = termBoard
            termBoard = tmpBoard
            manyCards.apply((ySize * row) + col + row).cCard =
              ctr.toCard(aDeck.toString())
            aDeck = new Deck(tmpDeck.getDeck, ctr)
            vDeck.cCard = ctr.toCard(aDeck.turnUpperCard)

            // memStack.save(
            //   Memento(
            //     true,
            //     turnedDeck.getUpperCard(),
            //     (termBoard.ySize * row) + col + row,
            //     getBoardCard(preBoard, (preBoard.ySize * row) + col + row),
            //     preDisc,
            //     getBoardCard(
            //       preBoard,
            //       (preBoard.ySize * row) + col + row
            //     ).turned
            //   )
            // )
            val preStage = ctr.getGameState.copy(
              boards = ctr.getGameState.boards
                .updated(ctr.getGameState.playerIdx, preBoard),
              deck = turnedDeck,
              disc = DiscardPile(ctr, preDisc.toString()),
              drawnCard = Some(ctr.toCard(turnedDeck))
            )
            ctr.save(preStage)
          },
          endTurn = () => {
            termBoard = termBoard.turnBoardCard((ySize * row) + col + row)
            manyCards.apply((ySize * row) + col + row).turned = true
            ctr.state = ctr.getGameState.copy(
              currentState = ctr.getGameState.currentState.reset()
            )
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
         cCard = ctr.toCard(aDisc.toString()).falseCopy,
         med = this.med,
         isDisc = true,
         switchDeckDisc = () => {
           val turnedDeck =
             new Deck(ctr.getDeck, ctr, ctr.draw()._1.toString())
           val toDisc = aDisc.putToDiscardPile(turnedDeck)
           aDisc = toDisc._1
           aDeck = toDisc._2

           aDiscard.cCard = ctr.toCard(aDisc.toString())
           vDeck.cCard = ctr.toCard(aDeck.turnUpperCard)

           ctr.state = ctr.getGameState.copy(
             currentState = ctr.getGameState.currentState.reset()
           )
           ctr.getGameState.currentState.pre = "BOARD"
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
         cCard = ctr.toCard(0).falseCopy,
         med = this.med,
         isDisc = true,
         switchDeckDisc = () => {
           val turnedDeck =
             new Deck(ctr.getDeck, ctr, ctr.draw().toString())
           val toDisc = aDisc.putToDiscardPile(turnedDeck)
           aDisc = toDisc._1
           aDeck = toDisc._2

           aDiscard.cCard = ctr.toCard(aDisc.toString())
           vDeck.cCard = ctr.toCard(aDeck.turnUpperCard)

           ctr.state = ctr.getGameState.copy(
             currentState = ctr.getGameState.currentState.reset()
           )
           ctr.getGameState.currentState.pre = "BOARD"
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
      cCard = ctr.toCard(ctr.draw()._1.toString()).falseCopy,
      med = this.med,
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
