package de.htwg.se.skyjo.model
enum State(str: String = "BEGIN", var pre: String = "BOARD") {
  def getStr: String = str
  def nextState(): State = {
    if str == "BEGIN" then State.MID
    else State.END
  }
  case BEGIN extends State()
  case MID extends State("MID")
  case END extends State("END")
  def reset(): State = BEGIN
}
