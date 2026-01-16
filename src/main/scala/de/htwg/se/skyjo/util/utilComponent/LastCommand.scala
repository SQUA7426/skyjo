package de.htwg.se.skyjo.util.utilComponent

import de.htwg.se.skyjo.util.CommandInterface

class LastCommand extends CommandInterface:
  override val cmd: String = "LAST"
  // override val next: CommandInterface = this

  // override def execute(command: String): Option[(Board, Deck, DiscardPile)] = {
  override def execute(command: String): Boolean = {
    println(s"The command: '${command}' arrived at the LastCommand")
    // None
    false
  }


