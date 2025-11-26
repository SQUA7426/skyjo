import de.htwg.se.skyjo.util.{Observable, Observer}

class TestObserver extends Observer {
  def update:Unit = println("Ping")
}

class TestObservable extends Observable

val observable = new TestObservable()
val observer = new TestObserver

observable.add(observer)
observable.notifyObservers
