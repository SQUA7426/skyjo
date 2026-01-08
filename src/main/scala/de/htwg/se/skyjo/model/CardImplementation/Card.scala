package de.htwg.se.skyjo.model.CardImplementation

import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.CardInterface
import de.htwg.se.skyjo.controller.ControllerComponent.*

case class Card(
    val value: Int,
    var turned: Boolean,
    val ctrl: ControllerInterface
) extends Colleague, CardInterface {
  val _mediator = ctrl.getMediator
  override def receive(msg: String): Boolean = {
    msg match
      case "REQUEST GET UPPERCARD" => {
        println(s"Card Received Message: ${msg}"); true
      }
      case _ => false
  }
  override def send(msg: String): Unit = ctrl.getMediator.send(this, msg)

  def isTurned: Boolean = turned

  def falseCopy: Card = new Card(value, false, ctrl)

  def trueCopy: Card = new Card(value, true, ctrl)

  def turn: Unit = { turned = !turned }

  override def toString(): String = if turned then s"${value}" else "#"
}
object Card:
  def apply(value: Int, ctrl:ControllerInterface): CardInterface =
    if value > (-3) && value < (13) then new Card(value, true, ctrl)
    else throw new IllegalArgumentException(s"Invalid Card number: ${value}")

