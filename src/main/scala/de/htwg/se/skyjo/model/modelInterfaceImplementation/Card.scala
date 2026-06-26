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

  //  CTRL //
  def getValue: Int = value

  def isVal: Boolean =
      if !(value > -3 && value < 13) then true else false
  def isTurned: Boolean = turned

  def trueCopy: CardInterface = new Card(value, true)
  def falseCopy: CardInterface = new Card(value, false)

  def turn: Unit = { turned = !turned }

  // FILEIO //

  def toJson: JsObject = Json.obj(
    "value"   -> value,
    "turned"  -> turned
    )

  def fromJson(js: JsObject): CardInterface =
    val v = (js \ "value").as[Int]
    val t = (js \ "turned").as[Boolean]
    Card(v,t)

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
    ns.head.text.replace(" ", "").toBoolean
  private def Node2Int(ns: NodeSeq): Int =
    ns.head.text.replace(" ", "").toInt

object Card:
  def apply(value: Int): CardInterface =
    if value > (-3) && value < (13) then new Card(value, true)
    else throw new IllegalArgumentException(s"Invalid Card number: ${value}")

