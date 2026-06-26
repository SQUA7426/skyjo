package de.htwg.se.skyjo

import com.google.inject.{Guice, Injector}
import de.htwg.se.skyjo.aView.Gui.Gui
import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import net.codingwell.scalaguice.InjectorExtensions.*

object Main {
  def main(args: Array[String]): Unit = {
    val plCount = 1
    val injector: Injector = Guice.createInjector(SkyjoModule(plCount))

    val ctr = injector.getInstance(classOf[ControllerInterface])
    ctr.setup()

    new Tui(ctr, interactive = false)

    Gui.init(ctr)
    Gui.main(args)
  }
}