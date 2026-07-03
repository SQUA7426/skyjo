package de.htwg.se.skyjo.fileIoComponent.fileIoXmlImpl

import de.htwg.se.skyjo.fileIoComponent.FileIOInterface
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.Controller
import de.htwg.se.skyjo.model.{GameState, State}
import scala.util.Try
import scala.xml.XML

class XmlImpl(ctrl: ControllerInterface) extends FileIOInterface {

  def load(filename: String): GameState =
    val tempState = new GameState(Vector.empty, Vector.empty, null, null, 0, State.BEGIN)
    val loaded = (XML.loadFile(f"${ctrl.path}$filename") \\ "gamestate").head
    val gs = tempState.fromXml(loaded)

    if gs==tempState then println("game_state_data not converted!") else println("game_state_data converted!")
    gs

  def save(gs: GameState,filename: String): Unit =
    val gsXml = gs.toXml
    scala.xml.XML.save(f"${ctrl.path}$filename", gsXml)
}
