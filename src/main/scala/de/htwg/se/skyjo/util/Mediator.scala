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

  override def send(colleague: Colleague, msg: String): Unit = {
    thresholdingColleague.find{c =>
        c != colleague && c.receive(msg)
        }
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
