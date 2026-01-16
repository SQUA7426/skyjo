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
import scalafx.scene.layout.{VBox, HBox, Pane}
import scalafx.scene.control.{Button, Label}
import scalafx.event.ActionEvent
import scalafx.scene.input.MouseEvent

import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ButtonType}
import scalafx.application.Platform

import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.model.{
  State,
  GameState,
  CardInterface,
  DiscardPileInterface,
  DeckInterface,
  BoardInterface
}
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{
  Board,
  Deck,
  Card,
  DiscardPile
}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.*
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.util.*
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
import de.htwg.se.skyjo.aView.Gui.UIConstants._

val fontname = "Parisienne"
class BoardView(ctr: ControllerInterface) {

  // Lokale Referenzen auf Daten, die wir vom Controller syncen
  var aDisc: DiscardPileInterface = ctr.getDisc
  var aDeck: DeckInterface = ctr.getDeck
  var termBoard: BoardInterface = ctr.getBrds(ctr.getPlIdx)

  // Die View-Objekte
  var manyCards: Seq[CardView] = Seq.empty

  // Initiale Synchronisation
 def syncWithController(): Unit = {
  // 1. Daten vom Controller holen
  aDisc = ctr.getDisc
  aDeck = ctr.getDeck
  termBoard = ctr.getBrds(ctr.getPlIdx)
  
  // 2. Die Board-Karten neu erzeugen (völlig richtig)
  manyCards = initBoardCards()

  // 3. WICHTIG: Die speziellen Views für Disc und Deck synchronisieren
  // Wir aktualisieren die cCard-Referenzen der bestehenden Objekte
  
  // Ablagestapel aktualisieren
  val discCard = ctr.toCard(aDisc.toString)
  vDiscard.cCard = discCard
  
  // Logik: Wenn der Stapel nicht leer ist, muss die Karte "turned" sein, 
  // damit die Zahl statt "Disc" angezeigt wird.
  vDiscard.turned = (aDisc.toString() != "Disc")
  vDiscard.uptCardView() 

  // Deck aktualisieren
  vDeck.cCard = ctr.toCard(aDeck.getCard.toString())
  // Das Deck ist normalerweise verdeckt (turned = false), 
  // außer du hast gerade eine Karte gezogen.
  vDeck.uptCardView()
} 

  // Erzeugt die Karten-Views für das Grid
  def initBoardCards(): Seq[CardView] = {
    val (cols, rows) = ctr.getSize
    val flattened = termBoard.getBoard.flatten

    for {
      r <- 0 until rows
      c <- 0 until cols
    } yield {
      val i = (r * cols) + c
      new CardView(
        x_pos = padding + (padding + cardWidth) * c,
        y_pos = padding + (padding + cardHeight) * r,
        cCard = flattened(i),
        idx = i, // Wichtig für Index-Zugriffe im Controller
        turned = flattened(i).isTurned
      )
    }
  }

  // --- Spezielle Views für Deck und Ablage ---
  // Wir erzeugen sie bei jedem Aufruf "frisch" oder lazy,
  // um sicherzugehen, dass sie den aktuellen Zustand von `aDisc`/`aDeck` haben.
  def vDiscard: CardView = {
    // Prüfen ob Disc leer ist ("Disc" String) oder Karte hat
    val cardVal = ctr.toCard(aDisc)
    new CardView(100, 720, cardVal, isDisc = true, turned = true)
  }

  def vDeck: CardView = {
    // Deck zeigt immer Rückseite, außer im MID State wenn "Pre: DECK"
    val showFront = (ctr.currState == State.MID && ctr.currState.pre == "DECK")
    val cardVal =
      if (showFront) ctr.toCard(aDeck.getCard.getOrElse("Deck"))
      else ctr.toCard("Deck")

    new CardView(400, 720, cardVal, isDeck = true, turned = showFront)
  }

  def viewBoard(): Seq[StackPane] = {
    manyCards.map(_.view) :+ vDiscard.view :+ vDeck.view
  }

  def updateAllViews(): Unit = {
    manyCards.foreach(_.uptCardView())
    // Deck und Disc werden beim Neuzeichnen von viewBoard() aktualisiert
  }

  // ==========================
  //      LOGIK METHODEN
  // ==========================

  // 1. Deck ziehen (Begin -> Mid)
  def performDrawDeck(): Unit = {
    ctr.drawFromDeck() // Controller zieht Karte, macht Memento, updated State (intern)
    // State weiterschalten
    val nextState = ctr.currState.nextState()
    nextState.pre = "DECK"
    ctr.assertGameState(ctr.copy(currentState = nextState))
  }

  // 2. Ablage ziehen (Begin -> Mid)
  def performDrawDisc(): Unit = {
    ctr.drawFromDisc() // Controller nimmt Karte, macht Memento
    // State weiterschalten
    val nextState = ctr.currState.nextState()
    nextState.pre = "DISC"
    ctr.assertGameState(ctr.copy(currentState = nextState))
  }

  // 3. Karte vom Deck auf Ablage werfen (Mid -> End)
  def performDiscardDrawnDeck(): Unit = {
    // Die gezogene Karte (die "oben" auf dem Deck liegt/gespeichert ist) kommt auf den Ablagestapel
    // Da drawFromDeck() bereits das Deck verändert hat, müssen wir aufpassen.
    // In deinem Controller gibt es logic dafür. Wir simulieren hier den Schritt:

    // Wir holen die Karte, die wir gerade gezogen haben (liegt theoretisch "offen" bereit)
    // Hier nutzen wir putToDiscardPile logic vom Controller
    val drawnCard = aDeck.getCard.get // Die Karte die wir gerade gezogen haben
    val (newDisc, newDeck) = ctr.putToDiscardPile(drawnCard)

    // Update Controller
    val nextState = ctr.currState.nextState() // Zu END
    nextState.pre = "BOARD" // Wir wollen jetzt im END state eine Karte umdrehen

    ctr.assertGameState(
      ctr.copy(
        d = newDeck,
        disc = newDisc,
        currentState = nextState
      )
    )
  }

  // 4. Tausch mit Board (Mid -> Begin/End)
  def performSwitchBoard(idx: Int): Unit = {
    val origin = ctr.currState.pre // "DECK" oder "DISC"

    if (origin == "DISC") {
      // Tausch mit Karte aus Ablage (die wir gerade genommen haben)
      // Hinweis: Da drawFromDisc() die Karte schon aus Disc entfernt hat,
      // müssen wir sie aus dem Memento oder einer temporären Variable holen?
      // Bei deiner Controller-Logik ist `drawFromDisc` schon passiert.
      // Wir nehmen an, die Karte ist im "Hand"-Status.
      // Vereinfachung: Wir nutzen switch logic direkt auf dem Board mit dem was wir haben.

      // Da wir die Karte im `drawFromDisc` Memento gespeichert haben,
      // ist der Switch etwas komplexer ohne "Hand"-Variable im Controller.
      // Wir nutzen hier ctr.toCard(aDisc) wenn es VOR dem draw wäre.
      // ABER: drawFromDisc hat schon stattgefunden.

      // workaround: Wir nehmen an, der User klickt auf Board.
      // Die Karte die getauscht werden soll ist die, die wir gerade "halten".
      // Das ist etwas tricky mit dem aktuellen Controller Interface.
      // Wir nutzen hier `termBoard.switch` mit der Karte aus dem Memento?
      // Oder wir nutzen die Tatsache, dass drawFromDisc ein Memento erzeugt hat.

      // Einfacherer Weg für GUI Logik:
      val cardInHand = ctr.currState.pre match {
        case "DECK" =>
          aDeck.getCard.get // Die oberste Karte des Decks (wurde gezogen)
        case "DISC" =>
          // Hier ist das Problem: drawFromDisc hat die Karte schon entfernt.
          // Wir müssten sie aus dem Memento holen.
          ctr.currMemento.undoStack(0).takenCard
        case _ => ctr.toCard(0)
      }

      val (newBoardCard, newBoard) = termBoard.switch(cardInHand, idx)

      // Die alte Board-Karte kommt auf den Ablagestapel
      val newDiscPile = DiscardPile(ctr, newBoardCard.getValue.toString)

      // Da drawFromDeck das Deck verändert hat, müssen wir für den Switch Deck evtl anpassen
      // Wenn von Deck: Karte ist weg vom Deck -> auf Board -> BoardKarte auf Disc.
      // Wenn von Disc: Karte ist weg von Disc -> auf Board -> BoardKarte auf Disc.

      val newDeck = if (origin == "DECK") {
        // Deck wurde schon durch drawFromDeck reduziert, das passt.
        // Wir müssen aber sicherstellen, dass wir nicht die "getCard" (die wir gerade genutzt haben) nochmal drauf haben
        // Das ist im Deck Interface gekapselt.
        aDeck // Deck bleibt wie es nach draw() war
      } else {
        aDeck
      }

      ctr.assertGameState(
        ctr.copy(
          brds = ctr.getBrds.updated(ctr.getPlIdx, newBoard),
          disc = newDiscPile,
          d = newDeck,
          currentState = ctr.currState.reset() // Zurück zu BEGIN
        )
      )
    } else if (origin == "DECK") {
      // Gleiche Logik wie oben
      val cardInHand = aDeck.getCard.get
      val (newBoardCard, newBoard) = termBoard.switch(cardInHand, idx)

      // Alte Karte auf Disc
      val newDiscPile = DiscardPile(ctr, newBoardCard.getValue.toString)

      // Deck muss aktualisiert werden (die gezogene Karte ist jetzt weg)
      val (removedCard, deckWithoutDrawn) =
        aDeck.draw() // Wir entfernen sie effektiv

      ctr.assertGameState(
        ctr.copy(
          brds = ctr.getBrds.updated(ctr.getPlIdx, newBoard),
          disc = newDiscPile,
          d = deckWithoutDrawn,
          currentState = ctr.currState.reset()
        )
      )
    }
  }

  // 5. Ende Phase: Karte umdrehen
  def performEndTurn(idx: Int): Unit = {
    val newBoard = termBoard.turnBoardCard(idx)
    ctr.assertGameState(
      ctr.copy(
        brds = ctr.getBrds.updated(ctr.getPlIdx, newBoard),
        currentState = ctr.currState.reset()
      )
    )
  }

  // ==========================
  //      CARD VIEW CLASS
  // ==========================

  class CardView(
      val x_pos: Int,
      val y_pos: Int,
      var cCard: CardInterface,
      val idx: Int = -1,
      var turned: Boolean = false,
      val isDisc: Boolean = false,
      val isDeck: Boolean = false
  ) {
    var selected = false

    val colour: Color =
      if (isDeck || isDisc) Color.SteelBlue else Color.DarkBlue

    val cardShape = new Rectangle {
      height = cardHeight
      width = cardWidth
      x = x_pos
      y = y_pos
      arcHeight = 30
      arcWidth = 30
      fill = colour
    }

    val label = new Text {
      font = Font(fontname, size = cardHeight * 0.3)
      alignmentInParent = Pos.Center
    }

    // Initiale Befüllung
    uptCardView()

    def uptLabel(): Unit = {
      label.text = if (turned || isDisc) {
        // Wenn offen ODER Ablagestapel -> Wert anzeigen
        cCard.getValue.toString
      } else {
        // Nur beim verdeckten Deck oder verdeckten Board-Karten Text anzeigen
        if (isDeck) "Deck" else ""
      }

      label.fill = if (turned || isDisc) Color.Black else Color.White
    }

    def uptCardView(): Unit = {
      uptLabel()
      cardShape.fill = {
        if (selected) Color.LightSalmon
        else if (turned) Color.LightGray
        else colour
      }
    }

    // --- CLICK HANDLER ---
    cardShape.onMouseClicked = (_: MouseEvent) => {
      val state = ctr.currState

      state match {
        // 1. ANFANG: Deck oder Disc wählen
        case State.BEGIN =>
          if (isDeck) {
            performDrawDeck()
          } else if (isDisc) {
            performDrawDisc()
          }

        // 2. MITTE: Aktion ausführen
        case State.MID =>
          val pre = state.pre

          if (!isDeck && !isDisc) {
            // Auf Board geklickt -> Tauschen
            performSwitchBoard(idx)
          } else if (isDisc && pre == "DECK") {
            // Wir haben Deck gezogen, wollen es aber auf Disc werfen
            performDiscardDrawnDeck()
          }

        // 3. ENDE: Karte aufdecken (wenn man im MID schritt discarded hat)
        case State.END =>
          if (!isDeck && !isDisc && !turned) {
            performEndTurn(idx)

            // Check Win Condition / Popup
            // (könnte auch im Controller via Notify passieren)
            if (termBoard.getBoard.flatten.forall(_.isTurned)) {
              def popup(termBoard: BoardInterface) = {
                val finished = new Alert(AlertType.Information) {
                  // initOwner(stage)
                  title = "finished window"
                }
                finished.headerText = s"FINISHED"
                //  with: ${termBoard.getBoard.flatten.map(c => c.getVal).fold(0)((x,y) =>x+y)}
                val re = finished.showAndWait()
                re match {
                  case Some(ButtonType.OK) => {
                    println("closed FINISHED Box")
                    Platform.exit()
                  }
                  case _ => {}
                }
              }
              popup(termBoard)
            }
          }
      }
    }

    def view: StackPane = new StackPane {
      layoutX = x_pos
      layoutY = y_pos
      children = Seq(cardShape, label)
    }
  }
} // }
// val fontname = "Parisienne"
//
// case class BoardView(ctr: ControllerInterface) extends Observer {
//   ctr.add(this)
//   override def update: Boolean = true
//
//   // val _med: Mediator = new ConcreteMediator()
//   val padding = 30
//   // var aDeck: Deck = Deck(_med)
//   // var termBoard: Board = fillBoard(_med, 4, 3, aDeck)._1
//   // var aDisc: DiscardPile = new DiscardPile(_med, "Disc")
//   // var memStack: MoveCaretaker = new MoveCaretaker(_med)
//   var med = ctr.getMediator
//   var aDisc = ctr.getDisc
//   var aDeck = ctr.getDeck
//
//   var termBoard = ctr.getGameState.boards(ctr.getPldx)
//
//   // val tui = new Tui(ctr)
//
//   case class CardView(
//       x_pos: Int,
//       y_pos: Int,
//       h: Int = 198,
//       w: Int = 132,
//       colour: Color = Color.DarkBlue,
//       var cCard: CardInterface,
//       var turned: Boolean = false,
//       val med: Mediator = med,
//       val isDisc: Boolean = false,
//       val isDeck: Boolean = false,
//       switchDeckDisc: () => Unit,
//       switchDiscB: () => Unit,
//       switchDeckB: () => Unit,
//       endTurn: () => Unit
//   ) {
//     val arcH = 30
//     val arcW = arcH
//
//     val label: Text = createLabel
//     var selected = false
//
//     override def toString(): String = cCard.toString()
//
//     val cardShape = new Rectangle {
//       height = h; width = w; x = x_pos; y = y_pos; arcHeight = arcH;
//       arcWidth = arcW; fill = colour
//     }
//
//     def createLabel: Text = new Text(
//       if turned then cCard.trueCopy.toString()
//       else {
//         if isDeck then "Deck"
//         else if isDisc then "Disc"
//         else ""
//       }
//     ) {
//       fill = if turned then Color.Black else Color.DarkBlue
//       font = Font(fontname, size = h * 0.3)
//       alignmentInParent = Pos.Center
//     }
//
//     def uptLabel(): Unit = {
//       label.text =
//         if turned then cCard.trueCopy.toString()
//         else {
//           if isDeck then "Deck"
//           else if isDisc then "Disc"
//           else ""
//         }
//       label.fill = if turned then Color.Black else Color.DarkBlue
//       label.font = Font(fontname, size = h * 0.3)
//     }
//
//     cardShape.onMouseClicked = (_: MouseEvent) => {
//       ctr.currState match {
//         case State.BEGIN => {
//           selected = !selected
//
//           if selected && (isDisc || isDeck) then {
//             ctr.assertGameState(
//               ctr.copy(
//                 ctr.getMediator,
//                 ctr.getBrds,
//                 ctr.getDeck,
//                 ctr.getDisc,
//                 ctr.getPldx,
//                 ctr.getdrawn,
//                 ctr.getPhase,
//                 ctr.currState.nextState()
//               )
//             )
//           }
//
//           if isDisc then ctr.currState.pre = "DISC"
//           else if isDeck then
//             ctr.currState.pre = "DECK"
//             aDeck = new Deck(ctr.getDeckCards, ctr, aDeck.turnUpperCard)
//             turned = true
//           else
//             selected = !selected
//             ctr.currState.pre = "BOARD"
//           // println(s"changed to State: ${currentState.toString}")
//           uptCardView
//         }
//         case State.MID => {
//           selected = !selected
//
//           if !isDeck then
//             if ctr.currState.pre.compareTo("DISC") == 0 then
//               if vDiscard.turned then
//                 turned = true
//                 // println("switching Disc / Board")
//                 switchDiscB()
//               else println("Cannot get from Empty DiscardPile")
//             else if ctr.currState.pre.compareTo("DECK") == 0 then
//               turned = true
//               if isDisc then
//                 // println("switching Deck / Disc")
//                 vDiscard.switchDeckDisc()
//               else if !isDisc && !isDeck then
//                 // println("switching Deck / Board")
//                 switchDeckB()
//
//             selected = !selected
//             vDiscard.turned = if aDisc.toString() == "Disc" then false else true
//             vDiscard.selected = false
//             vDiscard.uptCardView
//
//             vDeck.selected = false
//             vDeck.turned = false
//             vDeck.uptCardView
//
//             uptCardView
//             if ctr.currState == State.MID then
//               ctr.assertGameState(
//                 ctr.copy(
//                   ctr.getMediator,
//                   ctr.getBrds,
//                   ctr.getDeck,
//                   ctr.getDisc,
//                   ctr.getPldx,
//                   ctr.getdrawn,
//                   ctr.getPhase,
//                   ctr.currState.nextState()
//                 )
//               )
//             // println(s"changed to State: ${ctr.currState.toString}")
//           else // IF DECK was CLICKED BEFORE
//             vDeck.selected = false
//             vDeck.turned = false
//             vDeck.uptCardView
//             ctr.assertGameState(
//               ctr.copy(
//                 ctr.getMediator,
//                 ctr.getBrds,
//                 ctr.getDeck,
//                 ctr.getDisc,
//                 ctr.getPldx,
//                 ctr.getdrawn,
//                 ctr.getPhase,
//                 ctr.currState.reset()
//               )
//             )
//         }
//         case State.END => {
//           endTurn()
//           uptCardView
//         }
//       }
//       // println(ctr.getBoard.flatten)
//       // println(s"aDisc:\n${aDisc.toString()}")
//       // println(s"aDeck:\n${aDeck.toString()}")
//
//       def popup = {
//         val finished = new Alert(AlertType.Information) {
//           // initOwner(stage)
//           title = "finished window"
//         }
//         finished.headerText = "FINISHED"
//         val re = finished.showAndWait()
//         re match {
//           case Some(ButtonType.OK) => {
//             println("closed FINISHED Box")
//             Platform.exit()
//           }
//           case _ => {}
//         }
//       }
//
//       if ctr.getBoard.forall(row => row.forall(_.isTurned)) then popup
//       Gui.update
//     }
//
//     def uptCardView: Unit = {
//       uptLabel()
//       cardShape.fill = {
//         if (selected && turned == false) {
//           Color.LightGray
//         }
//         if selected then Color.LightSalmon
//         else {
//           if !turned then colour else Color.LightGray
//         }
//       }
//     }
//
//     val view: StackPane = new StackPane {
//       layoutX = x_pos
//       layoutY = y_pos
//       children = Seq(cardShape, label)
//     }
//   }
//
//   // BOARDVIEW
//   var manyCards: Seq[BoardView#CardView] = BOARD_INIT(false)
//
//   def BOARD_INIT(begin: Boolean = false): Seq[BoardView#CardView] = {
//     // println(ctr.getBoard.flatten.map(c => c.trueCopy))
//     val (xSize, ySize) = termBoard.getSize
//     val br: Seq[CardView] = {
//       for {
//         row <- 0 until ySize
//         col <- 0 until xSize
//       } yield {
//         new CardView(
//           x_pos = (padding + ((padding + 132) * col)),
//           y_pos = (padding + ((padding + 198) * row)),
//           cCard = ctr.getBoard.flatten.apply((ySize * row) + col + row),
//           turned =
//             if begin then false
//             else
//               ctr.getBoard.flatten
//                 .apply((ySize * row) + col + row)
//                 .isTurned
//           ,
//           switchDeckDisc = () => {},
//           switchDiscB = () => {
//             val preDisc = ctr.toCard(aDisc.toString())
//             val (tmpCard: CardInterface, tmpBoard: BoardInterface) =
//               (termBoard
//                 .switch(preDisc, (ySize * row) + col + row): @unchecked)
//             val tmpDisc  = new DiscardPile(ctr, tmpCard.toString())
//             aDisc = tmpDisc
//             vDiscard.cCard =
//               ctr.getBoard.flatten.apply((ySize * row) + col + row)
//             val preBoard = termBoard
//             termBoard = tmpBoard
//             manyCards.apply((ySize * row) + col + row).cCard = preDisc
//
//             // memStack.save(
//             //   Memento(
//             //     false,
//             //     // aDeck.getUpperCard(),
//             //     preDisc,
//             //     (termBoard.ySize * row) + col + row,
//             //     getBoardCard(preBoard, (preBoard.ySize * row) + col + row),
//             //     DiscardPile(_med, preDisc.toString()),
//             //     getBoardCard(
//             //       preBoard,
//             //       (preBoard.ySize * row) + col + row
//             //     ).turned
//             //   )
//             // )
//
//             // val preStage = ctr.getGameState.copy(
//             //   boards = ctr.getGameState.boards
//             //     .updated(ctr.getGameState.playerIdx, preBoard),
//             //   disc = DiscardPile(ctr, preDisc.toString()),
//             //   drawnCard = Some(preDisc)
//             // )
//             val oldState = ctr.copy(
//               ctr.getMediator,
//               ctr.getBrds,
//               ctr.getDeck,
//               DiscardPile(ctr,preDisc.toString()),
//               ctr.getPldx,
//               Some(preDisc),
//               ctr.getPhase,
//               ctr.currState
//               )
//             ctr.save(oldState)
//
//             ctr.assertGameState(
//               ctr.copy(
//                 ctr.getMediator,
//                 ctr.getBrds.updated(ctr.getPldx, preBoard),
//                 ctr.getDeck,
//                 DiscardPile(ctr, preDisc.toString()),
//                 ctr.getPldx,
//                 Some(preDisc),
//                 ctr.getPhase,
//                 ctr.currState.reset()
//               )
//             )
//           },
//           switchDeckB = () => {
//             val preDisc: DiscardPileInterface = aDisc
//             aDisc = DiscardPile(
//               ctr,
//               termBoard
//                 .getBoardCard(((ySize * row) + col + row))
//                 .toString()
//             )
//             val tmpDiffDeck = ctr.draw()
//             val turnedDeck: DeckInterface =
//               new Deck(ctr.getDeckCards, ctr, tmpDiffDeck._1.toString())
//             val (tmpCard: CardInterface, tmpBoard: Board) = (termBoard.switch(
//               ctr.toCard(turnedDeck.toString()),
//               (ySize * row) + col + row
//             ): @unchecked)
//             val tmpDeck: Deck = new Deck(ctr.getDeckCards, ctr, tmpCard.toString())
//             vDiscard.cCard = ctr.toCard(aDisc.toString())
//             val preBoard = termBoard
//             termBoard = tmpBoard
//             manyCards.apply((ySize * row) + col + row).cCard =
//               ctr.toCard(aDeck.toString())
//             aDeck = new Deck(tmpDeck.getDeckCards, ctr)
//             vDeck.cCard = ctr.toCard(aDeck.turnUpperCard)
//
//             // memStack.save(
//             //   Memento(
//             //     true,
//             //     turnedDeck.getUpperCard(),
//             //     (termBoard.ySize * row) + col + row,
//             //     getBoardCard(preBoard, (preBoard.ySize * row) + col + row),
//             //     preDisc,
//             //     getBoardCard(
//             //       preBoard,
//             //       (preBoard.ySize * row) + col + row
//             //     ).turned
//             //   )
//             // )
//
//             // val preStage = ctr.getGameState.copy(
//             //   boards = ctr.getGameState.boards
//             //     .updated(ctr.getGameState.playerIdx, preBoard),
//             //   deck = turnedDeck,
//             //   disc = DiscardPile(ctr, preDisc.toString()),
//             //   drawnCard = Some(ctr.toCard(turnedDeck))
//             // )
//             val oldState = ctr.copy(
//               ctr.getMediator,
//               ctr.getBrds,
//               turnedDeck,
//               ctr.getDisc,
//               ctr.getPldx,
//               Some(tmpCard),
//               ctr.getPhase,
//               ctr.currState
//               )
//             ctr.save(oldState)
//
//             ctr.assertGameState(
//               ctr.copy(
//                 ctr.getMediator,
//                 ctr.getBrds.updated(ctr.getPldx, tmpBoard),
//                 tmpDiffDeck._2,
//                 DiscardPile(ctr, preDisc.toString()),
//                 ctr.getPldx,
//                 Some(ctr.toCard(turnedDeck)),
//                 ctr.getPhase,
//                 ctr.currState.reset()
//               )
//             )
//           },
//           endTurn = () => {
//             termBoard = termBoard.turnBoardCard((ySize * row) + col + row)
//             manyCards.apply((ySize * row) + col + row).turned = true
//             // ctr.state = ctr.getGameState.copy(
//             //   currentState = ctr.currState.reset()
//             // )
//             ctr.assertGameState(
//               ctr.copy(
//                 ctr.getMediator,
//                 ctr.getBrds,
//                 ctr.getDeck,
//                 ctr.getDisc,
//                 ctr.getPldx,
//                 ctr.getdrawn,
//                 ctr.getPhase,
//                 ctr.currState.reset()
//               )
//             )
//           }
//         )
//       }
//     }
//     br
//   }
//
//   def viewBoard(): Seq[StackPane] = {
//     manyCards.map(_.view) :+ viewDisc() :+ viewDeck()
//   }
//
//   // VIEW DISC
//
//   val vDiscard: CardView =
//     (if aDisc.toString() != "Disc" then
//        CardView(
//          100,
//          720,
//          cCard = ctr.toCard(aDisc.toString()).falseCopy,
//          med = this.med,
//          isDisc = true,
//          switchDeckDisc = () => {
//            val turnedDeck =
//              new Deck(ctr.getDeckCards, ctr, ctr.draw()._1.toString())
//            val toDisc = aDisc.putToDiscardPile(turnedDeck)
//            aDisc = toDisc._1
//            aDeck = toDisc._2
//
//            vDiscard.cCard = ctr.toCard(aDisc.toString())
//            vDeck.cCard = ctr.toCard(aDeck.turnUpperCard)
//
// //            val tmpState: GameState =
// //             new GameState(ctr.getGameState.copy(
// //              currentState = ctr.currState.reset()
// //            )
// // )
// //
// //            ctr.state = ctr.getGameState.copy(
// //              currentState = ctr.currState.reset()
// //            )
//            ctr.assertGameState(
//              ctr.copy(
//                ctr.getMediator,
//                ctr.getBrds,
//                ctr.getDeck,
//                ctr.getDisc,
//                ctr.getPldx,
//                ctr.getdrawn,
//                ctr.getPhase,
//                ctr.currState.nextState()
//              )
//            )
//            ctr.currState.pre = "BOARD"
//          },
//          switchDiscB = () => {},
//          switchDeckB = () => {},
//          endTurn = () => {}
//        )
//      else
//        CardView(
//          100,
//          720,
//          colour = Color.SteelBlue,
//          cCard = ctr.toCard(0).falseCopy,
//          med = this.med,
//          isDisc = true,
//          switchDeckDisc = () => {
//            val turnedDeck =
//              new Deck(ctr.getDeckCards, ctr, ctr.draw()._1.toString())
//            val toDisc = aDisc.putToDiscardPile(turnedDeck)
//            aDisc = toDisc._1
//            aDeck = toDisc._2
//
//            vDiscard.cCard = ctr.toCard(aDisc)
//            vDeck.cCard = ctr.toCard(aDeck.getCard.toString())
//
//            // ctr.state = ctr.getGameState.copy(
//            //   currentState = ctr.currState.reset()
//            // )
//            ctr.assertGameState(ctr.copy(ctr.getMediator, ctr.getBrds, ctr.getDeck, ctr.getDisc,ctr.getPldx, ctr.getdrawn, ctr.getPhase, ctr.currState.nextState()))
//            ctr.currState.pre = "BOARD"
//          },
//          switchDiscB = () => {},
//          switchDeckB = () => {},
//          endTurn = () => {}
//        )
//     )
//
//   def viewDisc(): StackPane = {
//     new StackPane {
//       layoutX = vDiscard.x_pos
//       layoutY = vDiscard.y_pos
//       children = Seq(vDiscard.cardShape, vDiscard.label)
//     }
//   }
//
//   val vDeck = vDECKINIT
//
//   def vDECKINIT: CardView = {
//     CardView(
//       400,
//       720,
//       colour = Color.SteelBlue,
//       cCard = ctr.toCard(ctr.draw()._1.toString()).falseCopy,
//       med = this.med,
//       isDeck = true,
//       switchDeckDisc = () => {},
//       switchDiscB = () => {},
//       switchDeckB = () => {},
//       endTurn = () => {}
//     )
//   }
//
//   def viewDeck(): StackPane = {
//     new StackPane {
//       layoutX = vDeck.x_pos
//       layoutY = vDeck.y_pos
//       children = Seq(vDeck.cardShape, vDeck.label)
//     }
//   }
// }
