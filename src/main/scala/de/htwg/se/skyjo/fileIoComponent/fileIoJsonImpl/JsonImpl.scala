package de.htwg.se.skyjo.fileIoComponent.fileIoJsonImpl

import de.htwg.se.skyjo.fileIoComponent.FileIOInterface
import de.htwg.se.skyjo.model.{GameState, BoardInterface}

import play.api.libs.json._
import java.io.{PrintWriter, File}
import scala.io.Source

class JsonImpl extends FileIOInterface {
  def load: Option[GameState] = {
  }

  def save(gs: GameState): Unit = {

  }
}
