package de.htwg.se.skyjo

import de.htwg.se.skyjo.model.DeckImplementation.*
import de.htwg.se.skyjo.model.DiscardPileImplementation.DiscardPile
import de.htwg.se.skyjo.model.CardImplementation.*
import de.htwg.se.skyjo.model.BoardImplementation.*
import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.Controller
import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.{GameState, BoardInterface, CardInterface, DeckInterface, DiscardPileInterface}
import de.htwg.se.skyjo.aView.Gui.Gui
import scalafx.application.JFXApp3

object Main:
  def main(args: Array[String]): Unit = {
      val plCount = 1
      val med = new ConcreteMediator()

      val tempState = new GameState(med, Vector.empty, null, null, 0, None)
      val ctr = new Controller(tempState)

      val deck = new Deck(ctr.fullDeck(), ctr) 
      val disc = new DiscardPile(ctr)

      val plBoards = Vector.fill(plCount)(new Board(med, 4, 3, Vector.empty))

      ctr.state = new GameState(med, plBoards, deck, disc, 0, None)

      ctr.setup()
      // val t = new Tui(ctr)

      println("input => g for GUI")
      val choose = "g"
      if choose == "g" then
        Gui.ctr = ctr
        Gui.main(args)
      else
        val t = new Tui(ctr)
        // println("GAMESTATE")
        // println(ctr.getGameState.toString())

        t.startGame

        println("After finished")
        val simpleCard = new Card(3, true, ctr)
        med.add(deck)
        med.add(disc)
        med.add(simpleCard)
        med.add(plBoards(0))
        med.send(deck, "REQUEST GET UPPERCARD")
        med.send(plBoards(0), "REQUEST PUT TO DISCARDPILE")
        med.send(disc, "REQUEST CARD FROM DECK")
    }
