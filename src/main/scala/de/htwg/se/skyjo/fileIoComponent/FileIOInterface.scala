package de.htwg.se.skyjo.fileIoComponent

import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface

trait FileIOInterface {
  def load: Option[ControllerInterface]
  def save(ctr: ControllerInterface): Unit
}
