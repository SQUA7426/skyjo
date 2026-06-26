package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.util.{Observer, Observable}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.enablers.Containing

class ObservableSpec extends AnyWordSpec with Matchers {
  "An Observable" should:
    val observable = new Observable
    val s = ""
    val observer = new Observer {
      var updated = false
      def isUpdated: Boolean = updated
      // override def update(s):Boolean = { updated = true; updated }
      override def update(choose: String): Boolean = true
    }
    "add an Observer" in:
      observable.add(observer)
      observable.subscribers should contain(observer)
    "notify the Observer" in:
      observable.notifyObservers
    "remove an Observer" in:
      observable.remove(observer)
      observable.subscribers should not contain (observer)
}
