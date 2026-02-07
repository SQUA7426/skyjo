package de.htwg.se.skyjo.util

import scala.util.Random
import de.htwg.se.skyjo.util.Colleague

trait Mediator {
  def send(colleague: Colleague, msg: String): Unit
}

class ConcreteMediator extends Mediator {
  var thresholdingColleague: Vector[Colleague] = Vector()

  def add(c: Colleague): Unit = thresholdingColleague =
    thresholdingColleague :+ c

  def remove(c: Colleague): Unit = thresholdingColleague =
    thresholdingColleague.filterNot(o => o == c)

  override def send(fromColleague: Colleague, msg: String): Unit = {
    thresholdingColleague.find{c =>
        c != fromColleague && c.receive(msg)
        }
  }

  def requestRmUpperCard(fromColleague: Colleague): Unit =
    send(fromColleague, "REQUEST REMOVE UPPERCARD")
  def requestPutToDisc(fromColleague:Colleague): Unit =
    send(fromColleague, "REQUEST PUT TO DISCARDPILE")
  def requestGetUpperCard(fromColleague: Colleague) =
    send(fromColleague, "REQUEST GET UPPERCARD")
  def requestCardFromDeck(fromColleague: Colleague) =
    send(fromColleague, "REQUEST CARD FROM DECK")
}
