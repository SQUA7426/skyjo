package de.htwg.se.skyjo.fileIoComponent.fileIoXmlImpl

import de.htwg.se.skyjo.fileIoComponent.FileIOInterface
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.Controller
import de.htwg.se.skyjo.model.{GameState, State}
import scala.util.Try

class XmlImpl extends FileIOInterface {
  def load: GameState =
    val tempState = new GameState(Vector.empty, Vector.empty, null, null, 0, State.BEGIN)
    val gs: GameState = Try(scala.xml.XML.loadFile("./game_state_data.xml")).map(tempState.fromXml).getOrElse(tempState)
    gs

  def save(gs: GameState): Unit =
    val gsXml = gs.toXml
    scala.xml.XML.save("./game_state_data.xml", gsXml)
}
