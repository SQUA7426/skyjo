package de.htwg.se.skyjo.fileIoComponent.fileIoXmlImpl

import de.htwg.se.skyjo.fileIoComponent.FileIOInterface
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.Controller
import de.htwg.se.skyjo.model.{GameState, State}
import scala.util.Try

class XmlImpl(ctrl: ControllerInterface) extends FileIOInterface {
  private val filename = "game_state_data.xml"

  def load(filename: String): GameState =
    val tempState = new GameState(Vector.empty, Vector.empty, null, null, 0, State.BEGIN)
    val gs: GameState = Try(scala.xml.XML.loadFile(f"${ctrl.path}$filename")).map(tempState.fromXml).getOrElse(tempState)
    gs

  def save(gs: GameState,filename: String): Unit =
    val gsXml = gs.toXml
    scala.xml.XML.save(f"${ctrl.path}$filename", gsXml)
}
