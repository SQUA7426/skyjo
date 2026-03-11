package de.htwg.se.skyjo.fileIoComponent

import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.GameState

trait FileIOInterface {
  def load(filename: String): GameState
  def save(gs: GameState,filename: String): Unit
}
