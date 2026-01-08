import de.htwg.se.skyjo.util.{ConcreteMediator,Mediator, Colleague}

class Test1Mediator(_med: Mediator) extends Colleague {
  override val _mediator: Mediator = _med
  override def send(msg: String): Unit = { _mediator.send(this, msg) }
  override def receive(msg: String): Unit = println(s"Message received: ${msg}")
}

class Test2Mediator(_med: Mediator) extends Colleague {
  override val _mediator: Mediator = _med
  override def send(msg: String): Unit = { _mediator.send(this, msg) }
  override def receive(msg: String): Unit = println(s"Message received: ${msg}")
}

object MediatorPattern {
  val med = new ConcreteMediator
  val col1 = new Test1Mediator(med)
  val col2 = new Test2Mediator(med)

  med.add(col1)
  med.add(col2)

  med.requestCardFromDeck(col1)
  med.requestGetUpperCard(col2)
  med.requestPutToDisc(col1)
  med.requestRmUpperCard(col2)
  
  med.remove(col2)
}
