package de.htwg.se

import de.htwg.se.Card
import de.htwg.se.DiscardPile

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._

class DiscardPileTest extends AnyWordSpec with Matchers {
  "A DiscardPile" when {
    val disc = DiscardPile()
    "initialized" should:
      "as original String" in:
        disc.toString() should (be (s"|${disc.discPile}| ") or be (s"${disc.discPile}"))
    "discPile uppperCard changed" should:
      "as changed String" in:
        disc.discPile = Card(6).toString()
        disc.toString() should (be (s"|${disc.discPile}| ") or be (s"${disc.discPile}"))
  }
}
