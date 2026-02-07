package de.htwg.se.skyjo.model.modelInterfaceImplementation

import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.CardInterface
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface

import jakarta.inject.Inject
import scala.util.{Try, Success, Failure}

case class Card @Inject() (
    val value: Int,
    var turned: Boolean,
    val ctrl: ControllerInterface
) extends Colleague with CardInterface:

  override def toString(): String = if turned then s"${value}" else "#"

  // MEDIATOR //
  val _mediator = ctrl.getMediator

  override def send(msg: String): Unit = ctrl.getMediator.send(this, msg)
  override def receive(msg: String): Boolean = {
    msg match
      case "REQUEST GET UPPERCARD" => {
        println(s"Card Received Message: ${msg}"); true
      }
      case _ => false
  }

  //  CTRL //
  def getValue: Int = value

  def isVal: Boolean =
      if !(value > -3 && value < 13) then true else false
  def isTurned: Boolean = turned

  def trueCopy: CardInterface = new Card(value, true, ctrl)
  def falseCopy: CardInterface = new Card(value, false, ctrl)

  def turn: Unit = { turned = !turned }

object Card:
  def apply(value: Int, ctrl:ControllerInterface): CardInterface =
    if value > (-3) && value < (13) then new Card(value, true, ctrl)
    else throw new IllegalArgumentException(s"Invalid Card number: ${value}")

