package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.util.Mediator

trait Colleague {
  val _mediator: Mediator
  def send(msg: String): Unit = { _mediator.send(this, msg) }
  def receive(msg: String): Boolean
}
