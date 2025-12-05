package de.htwg.se.skyjo.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._
import de.htwg.se.skyjo.model.{Card, isCard, len, toCard}
import de.htwg.se.skyjo.util.ConcreteMediator

class CardTest extends AnyWordSpec with Matchers {
  "A Card" when {
    val med = new ConcreteMediator()
    "has the value -3" should:
      "not be acceptable as Card" in:
        val lowerCardErr = the [IllegalArgumentException] thrownBy(Card(med,-3))
    "has the value 20" should:
      "not be acceptable as Card" in:
        val highCardErr = the [IllegalArgumentException] thrownBy(Card(med,20))
    val betweenCard = Card(med,5)
    "has the value 11" should:
      val num: Int = 11
      val card11: Card = Card(med,num)
      "as string" in:
        card11.toString() shouldBe (f"${num}")
    "A Card with value 9" should:
      val num9: Int = 9
      val card9: Card = Card(med,num9)
      "as string 9 be converted correctly" in:
        val n9 = "9"
        toCard(med,n9) shouldBe Card(med,9)
      "as int 9 be converted correctly" in:
        toCard(med,num9) shouldBe Card(med,9)
      "return # if a False Copy of it is created" in:
        card9.falseCopy().toString() should be ("#")
      "return the number if a True Copy of it is created" in:
        card9.trueCopy() shouldBe Card(med,9, true)
      "not be acceptable from boolean" in:
        val highCardErr = the [IllegalArgumentException] thrownBy(toCard(med,true))
      "it's digit length" in:
        len(card9.value) should (be (1) or be (2))
      "is from type: Card" in:
        isCard(card9) shouldBe true
    // "A Type T" should:
    //   val liste = List(23)
    //   "is not from type: Card" in:
    //     isCard(liste) shouldBe false
  }
}
