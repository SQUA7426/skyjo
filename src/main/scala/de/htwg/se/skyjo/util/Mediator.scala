package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.Model.{Card, Board, DiscardPile, Deck}
import scala.util.Random
import de.htwg.se.skyjo.util.Colleague
class Controller;
class Tui;

trait Mediator {
  def send(colleague: Colleague, msg: String): Unit
}

class ConcreteMediator extends Mediator {
  var thresholdingColleague: Vector[Colleague] = Vector()

  def add(c: Colleague): Unit = thresholdingColleague = thresholdingColleague :+ c

  def remove(c: Colleague): Unit = thresholdingColleague =
    thresholdingColleague.filterNot(o => o == c)

  override def send(colleague: Colleague, msg: String): Unit = {
    for colleagues <- thresholdingColleague do
      if colleagues != colleague then colleagues.receive(msg)
  }
}
