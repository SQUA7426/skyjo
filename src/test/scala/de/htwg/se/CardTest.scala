package de.htwg.se

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._

class CardTest extends AnyWordSpec with Matchers {
  "A Card" when {
    "has the value -3" should:
      "not be acceptable as Card" in:
        val lowerCardErr = the [IllegalArgumentException] thrownBy(Card(-3))
    "has the value 20" should:
      "not be acceptable as Card" in:
        val highCardErr = the [IllegalArgumentException] thrownBy(Card(20))
    val betweenCard = Card(5)
    "has the value 11" should:
      val num: Int = 11
      val card11: Card = Card(num)
      "as string" in:
        card11.toString() shouldBe (f"${num}")
    "A Card with value 9" should:
      val num9: Int = 9
      val card9: Card = Card(num9)
      "as string 9 be converted correctly" in:
        val n9 = "9"
        toCard(n9) shouldBe Card(9)
      "as int 9 be converted correctly" in:
        toCard(num9) shouldBe Card(9)
      "return # if a False Copy of it is created" in:
        card9.falseCopy().toString() should be ("#")
      "return the number if a True Copy of it is created" in:
        card9.trueCopy() shouldBe Card(9, true)
      "not be acceptable from boolean" in:
        val highCardErr = the [IllegalArgumentException] thrownBy(toCard(true))
      "it's digit length" in:
        len(card9.value) should (be (1) or be (2))
      "is from type: Card" in:
        isCard(card9) shouldBe true
    "A Type T" should:
      val liste = List(23)
      "is not from type: Card" in:
        isCard(liste) shouldBe false
  }
}
