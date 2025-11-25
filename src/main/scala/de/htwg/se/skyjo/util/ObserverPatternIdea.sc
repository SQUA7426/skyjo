<<<<<<< HEAD
import de.htwg.se.skyjo.util.{Observable, Observer}

class TestObserver extends Observer {
  def update:Unit = println("Ping")
}

class TestObservable extends Observable

val observable = new TestObservable()
val observer = new TestObserver

observable.add(observer)
observable.notifyObservers
=======
// import de.htwg.se.skyjo.util.{Observable, Observer}
//
// class TestObserver extends Observer {
//   def update:Unit = println("Ping")
// }
//
// object ObservablePattern:
//   val observable = new TestObservable()
//   val observer = new TestObserver
//
//   observable.add(observer)
//   observable.notifyObservers
>>>>>>> ac24e81204d6d5a282c8026e64969bb6ef21ba5e
