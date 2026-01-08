package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.util.*

trait Colleague {
  val _mediator: Mediator
  def send(msg: String): Unit
  def receive(msg: String): Boolean
}
