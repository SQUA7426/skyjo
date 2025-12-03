package de.htwg.se.skyjo.aView
import de.htwg.se.skyjo.Model
import de.htwg.se.skyjo.Model.{Board, Deck, DiscardPile}
import de.htwg.se.skyjo.controller.ControllerComponent.Controller
import scala.io.StdIn.{readInt, readLine}
import de.htwg.se.skyjo.util.SupportHandler

class Tui(cont: Controller) {

  def inputRequest(b: Board, disc: String) =
    (s"Which BoardCard [0-${b.xSize * b.ySize - 1}] do you want to switch with ${disc}?")

  def inputRequestDeck(deckCard: String) = (
    println(s"You took ${deckCard}")
  )
  def cardTurnRq(b: Board) =
    println(s"Which BoardCard [0-${b.xSize * b.ySize - 1}] do you want to turn around?")

  def turnOfPlayer(i: Int) = (println(s"Player ${i}:"))

  def finishedConf() = (
    println(s"someone is finished: \n")
  )

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

    // Try - Catch // WITH HANDLER
    val choose = readLine()
    val h = SupportHandler(cont, b, d, disc)
    try {
      choose match {
        case "0"|"1" => {
          if choose=="1" then inputRequestDeck(d.turnUpperCard().toString())
          h.handle(choose)
        }
      }
    } catch {
      case e: Exception => {
        println(s"${choose} is not valid, doing nothing.")
        turn(b, d, disc)
      }
    }
  }
}
