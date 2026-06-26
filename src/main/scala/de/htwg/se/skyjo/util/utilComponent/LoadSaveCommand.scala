package de.htwg.se.skyjo.util.utilComponent

import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.{BoardInterface, DeckInterface, DiscardPileInterface}
import de.htwg.se.skyjo.util.CommandInterface

case class LoadSaveCommand(ctrl: ControllerInterface, b: BoardInterface, d: DeckInterface, disc: DiscardPileInterface) extends CommandInterface:
  override val cmd: String = "load xml"
  private val cmd2: String = "load json"
  private val cmd3: String = "save xml"
  private val cmd4: String = "save json"

  override def execute(command: String): Boolean =
    command match {
      case c1 if command == cmd => ctrl.xml_load(""); true
      case c2 if command == cmd2 => ctrl.json_load(""); true
      case c3 if command == cmd3 => ctrl.xml_save; true
      case c4 if command == cmd4 => ctrl.json_save; true
      case _                    => false
    }
