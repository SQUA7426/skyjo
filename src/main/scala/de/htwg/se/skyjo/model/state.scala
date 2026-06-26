package de.htwg.se.skyjo.model

import scala.xml.Node
import play.api.libs.json.{Json, JsObject}

enum State(str: String = "BEGIN", var pre: String = "BOARD") {
  def getStr: String = str
  def nextState(): State = {
    if str == "BEGIN" then State.MID
    else State.END
  }
  case BEGIN extends State()
  case MID extends State("MID")
  case END extends State("END")
  case ASSERT(s:String, p:String) extends State(s,p)
  def reset(): State = BEGIN

  // FILEIO //

  def toXml: Node =
    <state>
      <str>{str}</str>
      <pre>{pre}</pre>
    </state>
  def fromXml(n: Node): State =
    val stateXml = {n \ "state"}
    val strXml = { stateXml \ "str"}.text.toString
    val preXml = { stateXml \ "pre"}.text.toString
    State.ASSERT(strXml, preXml)

  def toJson: JsObject = Json.obj(
    "str" -> str,
    "pre" -> pre
    )
}
