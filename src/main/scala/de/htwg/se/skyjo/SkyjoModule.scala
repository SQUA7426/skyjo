package  de.htwg.se.skyjo

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import de.htwg.se.skyjo.model.{BoardInterface, CardInterface, DeckInterface, DiscardPileInterface, GameState, State}
import de.htwg.se.skyjo.model.modelInterfaceImplementation
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.modelInterfaceImplementation.*

import de.htwg.se.skyjo.util.{ConcreteMediator, Mediator}
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerImplementation.Controller

class SkyjoModule extends AbstractModule with ScalaModule:

  override def configure(): Unit =
    bind[Mediator].toInstance(ConcreteMediator())
