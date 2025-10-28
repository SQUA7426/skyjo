package de.htwg.se

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._

class TuiTest extends AnyWordSpec with Matchers {
  val l = List(2, 3, 4, 5, 6, 7, 8)
  "A Tui PlayerCount" when:
    "between 2 and 8" should:
      "throw no Exceptions" in:
        l should contain(4)
    "lower 2" should:
      "throw an Exception" in:
        val err = the[Exception] thrownBy (l should contain (9)) should have message "List(2, 3, 4, 5, 6, 7, 8) did not contain element 9"
}
