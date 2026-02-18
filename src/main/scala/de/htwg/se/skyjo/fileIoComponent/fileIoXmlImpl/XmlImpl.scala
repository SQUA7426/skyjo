package de.htwg.se.skyjo.fileIoComponent.fileIoXmlImpl

import de.htwg.se.skyjo.fileIoComponent.FileIOInterface
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface

class XmlImplImpl extends FileIOInterface {
  def load: ControllerInterface = ???
  def save(ctr: ControllerInterface): Unit = {
    val memXML = <mementos>{ctr.getMementos}</mementos>
    val brdsXML = <boards>{gs.getBrds}</boards>
  }
}
