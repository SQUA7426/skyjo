package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.aView.Tui
import de.htwg.se.skyjo.controller.ControllerComponent.ControllerInterface
import de.htwg.se.skyjo.model.modelInterfaceImplementation.{Card, Deck, DiscardPile, Board}
import de.htwg.se.skyjo.util.{Mediator, ConcreteMediator, Colleague}
import de.htwg.se.skyjo.model.GameState
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.enablers.Containing
import java.io.ByteArrayInputStream

import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.skyjo.SkyjoModule
import net.codingwell.scalaguice.InjectorExtensions.*

class ConcreteMediatorSpec extends AnyWordSpec with Matchers {
  "A ConcreteMediator" should:
    val plCount = 1

    val injector = Guice.createInjector(SkyjoModule(plCount))

    val ctr = injector.getInstance(classOf[ControllerInterface])
    val med = injector.getInstance(classOf[ConcreteMediator])
    val deck = ctr.getDeck
    val disc = ctr.getDisc

    ctr.setup()

    case class Col() extends Colleague {
      override val _mediator: Mediator = med
      override def send(msg: String): Unit = println(f"send: $msg")
      override def receive(msg: String): Boolean = {
        println(f"received: $msg")
        true
      }
    }

    val col1 = new Col()
    val col2 = new Col()
    med.add(col1)
    med.add(col2)
    med.send(col1, "Hello")

    med.requestCardFromDeck(col1)
    med.requestGetUpperCard(col1)
    med.requestPutToDisc(col2)
    med.requestRmUpperCard(col2)

}
