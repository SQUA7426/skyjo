package de.htwg.se

import de.htwg.se.Card
import de.htwg.se.DiscardPile

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalactic.StringNormalizations._

class DiscardPileTest extends AnyWordSpec with Matchers {
  "A DiscardPile" when:
    val discard = DiscardPile("Disc")
    "initialized" should:
      "as original String" in:
        discard.toString() should (be (s"${discard.disc}"))
}
