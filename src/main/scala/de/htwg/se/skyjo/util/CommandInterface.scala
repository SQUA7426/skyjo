package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.model.{GameState}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface

trait CommandInterface:
  val cmd: String
  def execute(cmd: String): Boolean
  // def execute(cmd: String, state: GameState): Boolean

