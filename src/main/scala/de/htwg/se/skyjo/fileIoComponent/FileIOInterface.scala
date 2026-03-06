package de.htwg.se.skyjo.fileIoComponent

import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.GameState

trait FileIOInterface {
  def load: GameState
  def save(ctr: ControllerInterface): Unit
}
