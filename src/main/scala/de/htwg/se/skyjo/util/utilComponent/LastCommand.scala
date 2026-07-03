package de.htwg.se.skyjo.util.utilComponent

import de.htwg.se.skyjo.util.CommandInterface

class LastCommand extends CommandInterface:
  override val cmd: String = "LAST"

  override def execute(command: String): Boolean = {
    false
  }


