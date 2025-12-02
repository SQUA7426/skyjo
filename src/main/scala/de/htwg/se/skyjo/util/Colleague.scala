package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.util.Mediator

trait Colleague {
  val _mediator: Mediator
  def send(msg: String): Unit = { _mediator.send(this, msg) }
  def receive(msg: String): Unit = println(s"Message received: ${msg}")
}

// class ConcreteColleague extends Colleague {
//   override def notifyMediator(msg: String): Unit = {}
//   override def send(msg: String): Unit = {}
//   override def receive(msg: String): Unit = {}
// }
