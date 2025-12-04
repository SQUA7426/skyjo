package de.htwg.se.skyjo

import de.htwg.se.skyjo.model.{Board, Card, Deck, DiscardPile, fullDeck}
import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.Controller
import de.htwg.se.skyjo.util.{ConcreteMediator, Mediator}

def main(args: Array[String]): Unit = {
  // println("Enter a number of players:")
  // val plCount = readInt()
  val plCount = 1
  val med = new ConcreteMediator()
  val deck: Deck = Deck(med)
  val disc: DiscardPile = new DiscardPile(med, "Disc")
  val plBoards: Array[Board] =
    Array.fill(plCount)(new Board(med, 2, 2, Vector()))
  val Ctr =
    new Controller(med, disBoards = plBoards, disDeck = deck, discard = disc)
  val t = new Tui(Ctr)
  Ctr.gameLoop(plCount, plBoards, deck, disc)
  println("After finished")
  val simpleCard = new Card(med, 3, true)
  med.add(deck)
  med.add(disc)
  med.add(simpleCard)
  med.add(plBoards(0))
  med.send(deck, "REQUEST GET UPPERCARD")
  med.send(plBoards(0), "REQUEST PUT TO DISCARDPILE")
  med.send(disc, "REQUEST CARD FROM DECK")
}
