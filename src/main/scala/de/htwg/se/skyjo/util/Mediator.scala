package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.model.{Card, Board, DiscardPile, Deck}
import scala.util.Random
import de.htwg.se.skyjo.util.Colleague
class Controller;
class Tui;

trait Mediator {
  def send(colleague: Colleague, msg: String): Unit
}

class ConcreteMediator extends Mediator {
  var thresholdingColleague: Vector[Colleague] = Vector()

  def add(c: Colleague): Unit = thresholdingColleague =
    thresholdingColleague :+ c

  def remove(c: Colleague): Unit = thresholdingColleague =
    thresholdingColleague.filterNot(o => o == c)

  override def send(colleague: Colleague, msg: String): Unit = {
    for colleagues <- thresholdingColleague do
      if colleagues != colleague && colleagues.receive(msg) then return
  }

  def requestRmUpperCard(colleague: Colleague): Unit =
    send(colleague, "REQUEST REMOVE UPPERCARD")
  def requestPutToDisc(colleague:Colleague): Unit =
    send(colleague, "REQUEST PUT TO DISCARDPILE")
  def requestGetUpperCard(colleague: Colleague) =
    send(colleague, "REQUEST GET UPPERCARD")
  def requestCardFromDeck(colleague: Colleague) =
    send(colleague, "REQUEST CARD FROM DECK")
}
