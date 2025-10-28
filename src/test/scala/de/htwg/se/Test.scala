package de.htwg.se

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._

class Test extends AnyWordSpec with Matchers {
  "A StringTest with 9" should:
    "be between 2 and 8" in:
      val str = List("2","3","4","5","6","7","8")
      val nine = "9"
      // nine should be (equal ("2") or equal ("3") or equal ("4") or equal ("5") or equal ("6") or equal ("7") or equal ("8"))
      val err = the [Exception] thrownBy(str should contain (nine)) should have message "List(\"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\") did not contain element \"9\""

  "A StringTest with 6" should:
    "be between 2 and 8" in:
      val str = List("2","3","4","5","6","7","8")
      val six = "6"
      str should contain (six)
}
