import de.htwg.se.skyjo.controller.ControllerComponent.Controller
import de.htwg.se.skyjo.model.{Board, Deck, DiscardPile}
import de.htwg.se.skyjo.util.{ConcreteMediator, SupportCommand, SupportHandler}
import de.htwg.se.skyjo.aView.Tui
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.geometry.Insets

import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

class Gui(cont: Controller) extends VBox {
  var choose : String = null
  val grid = new GridPane {
    var boardIndex : Int = 0
    hgap = 10
    vgap = 10
    padding = Insets(10)
    var count = 0
    for (row <- 0 until 3; col <- 0 until 4) {
      val btn = new Button(s"($count)") {
        onAction = handle {
          boardIndex = count
          println(s"Button im Grid ($row,$col) geklickt")
        }
      }
      count = count + 1
      add(btn, col, row)
    }
  }

  val undoButton = new Button("Undo") {
    onAction = handle {
    choose= "undo"
    }
  }
  val redoButton = new Button("redo") {
    onAction = handle {
      choose = "redo"    }
  }
  val deck = new Button("Deck") {
    onAction = handle {
      choose = "0"    }
  }
  val discPile = new Button("Discardpile") {
    onAction = handle {
      choose = "1"
    }
  }

  val extraButtons = new HBox {
    spacing = 10
    children = Seq(undoButton, redoButton, deck, discPile)
  }

  spacing = 20
  children = Seq(grid, extraButtons)


  def turn(
            b: Board,
            d: Deck,
            disc: DiscardPile
          ): Option[(Board, Deck, DiscardPile)] = {
    println(b)
    println(s"| ${disc.toString()} |\n")
    println(s"Whatcha want to do?")
    println("[0] Take discard and switch with a board card")
    println("[1] Take deck card and choose:")
    println("\t[1] switch with board card")
    println("\t[2] put on discard and flip board card")
    println("[undo] or [2] Undo to the last step")

    // Try - Catch // WITH HANDLER
    //val choose = readLine()
    val h = SupportHandler(cont, b, d, disc)
    val c = SupportCommand(cont, b, d, disc)

    val action: Try[Option[(Board, Deck, DiscardPile)]] = Try {
      choose match {
        case "0" | "1" | "2" | "undo" | "help" | "redo" | "quit" =>
          //if (choose == "1") then inputRequestDeck(d.turnUpperCard().toString())
          println(choose)
          val return_H = h.handle(choose)
          if (return_H == None) c.execute(choose) else return_H
        case _ =>
          throw new IllegalArgumentException(s"$choose is not valid, doing nothing.")
      }
    }

    action match {
      case Success(result) => result
      case Failure(e) =>
        println(e.getMessage)
        turn(b, d, disc)
    }
  }}


object GridApp extends JFXApp3 {
  override def start(): Unit = {
    val plCount = 1
    val med = new ConcreteMediator()
    val deck: Deck = Deck(med)
    val disc: DiscardPile = new DiscardPile(med, "Disc")
    val plBoards: Array[Board] =
      Array.fill(plCount)(new Board(med, 4, 3, Vector()))
    val Ctr =
      new Controller(med, disBoards = plBoards, disDeck = deck, discard = disc)
    stage = new JFXApp3.PrimaryStage {
      title.value = "ScalaFX Grid Example"
      scene = new Scene(400, 400) {
        root = new Gui(Ctr)
        Ctr.gameLoop(plCount, plBoards, deck, disc)

      }
    }
  }
}