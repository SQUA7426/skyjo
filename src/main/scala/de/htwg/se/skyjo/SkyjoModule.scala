package de.htwg.se.skyjo

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import de.htwg.se.skyjo.model.{BoardInterface, CardInterface, DeckInterface, DiscardPileInterface, GameState, State}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.modelInterfaceImplementation.*
import de.htwg.se.skyjo.fileIoComponent.FileIOInterface
import de.htwg.se.skyjo.fileIoComponent.fileIoXmlImpl.{XmlImpl}

import de.htwg.se.skyjo.util.{ConcreteMediator, Mediator}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.Controller
import de.htwg.se.skyjo.fileIoComponent.fileIoJsonImpl.JsonImpl
import com.google.inject.Provides
import com.google.inject.name.Named

class SkyjoModule(count: Int) extends AbstractModule with ScalaModule:

  override def configure(): Unit = {
    bind[Int].annotatedWithName("plCount").toInstance(count)
    bind[GameState].toInstance(new GameState(Vector.empty, Vector.empty, null, null, 0, State.BEGIN))

    bind[Mediator].toInstance(ConcreteMediator())
    // bind[FileIOInterface].toInstance(XmlImpl())
  }

  @Provides
  def provideFileIO(ctrl: ControllerInterface): FileIOInterface = {
    new JsonImpl(ctrl)
  }

  @Provides
  def ProvidesController(gs: GameState, @Named("plCount") count: Int, med: Mediator): ControllerInterface = {
    new Controller(gs, count, med)
  }
