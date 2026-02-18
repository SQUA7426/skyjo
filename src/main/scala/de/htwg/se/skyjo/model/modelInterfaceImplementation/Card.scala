package de.htwg.se.skyjo.model.modelInterfaceImplementation

import de.htwg.se.skyjo.util.*
import de.htwg.se.skyjo.model.CardInterface
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface

import scala.util.{Try, Success, Failure}
import play.api.libs.json._
import scala.xml.{Node, NodeSeq}

case class Card (
    val value: Int,
    var turned: Boolean
    // val ctrl: ControllerInterface
) extends CardInterface:

  override def toString(): String = if turned then s"${value}" else "#"

  // MEDIATOR //
  // val _mediator = ctrl.getMediator

  // override def send(msg: String): Unit = ctrl.getMediator.send(this, msg)
  // override def receive(msg: String): Boolean = {
  //   msg match
  //     case "REQUEST GET UPPERCARD" => {
  //       println(s"Card Received Message: ${msg}"); true
  //     }
  //     case _ => false
  // }

  //  CTRL //
  def getValue: Int = value

  def isVal: Boolean =
      if !(value > -3 && value < 13) then true else false
  def isTurned: Boolean = turned

  def trueCopy: CardInterface = new Card(value, true)
  def falseCopy: CardInterface = new Card(value, false)

  def turn: Unit = { turned = !turned }

  // FILEIO //

  def toXml: Node = {
    <card>
      <value>{value}</value>
      <turned>{turned}</turned>
    </card>
  }
  def fromXml(element: Node): CardInterface =
    Card(Node2Int(element \ "value"),
      Node2Bool(element \ "turned"))

  private def Node2Bool(ns: NodeSeq): Boolean =
    n.head.text.replace(" ", "").toBoolean
  private def Node2Int(ns: NodeSeq): Int =
    n.head.text.replace(" ", "").toInt

object Card:
  def apply(value: Int): CardInterface =
    if value > (-3) && value < (13) then new Card(value, true)
    else throw new IllegalArgumentException(s"Invalid Card number: ${value}")

