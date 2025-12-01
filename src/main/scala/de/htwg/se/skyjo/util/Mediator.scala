package de.htwg.se.util
import scala.util.Random
class Controller;
class Tui;

trait Mediator {
  def notify(sender: Mediator, event: String): Unit
  def send(msg: String): Unit
}

class Mediating {
  var thresholdingMediator: Vector[Mediator] = Vector()

  def add(m: Mediator): Unit = thresholdingMediator = thresholdingMediator :+ m

  def remove(m: Mediator): Unit = thresholdingMediator =
    thresholdingMediator.filterNot(o => o == m)

  def notifyMediators(): Unit = thresholdingMediator.foreach(o => o.notify())
}

class ConcreteMediator extends Mediator {
  override def notify(sender: Mediator, event: String): Unit = {

  }
  override def send(msg: String): Unit = {

  }
}
