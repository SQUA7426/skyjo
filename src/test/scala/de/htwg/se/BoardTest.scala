// package de.htwg.se
//
// import de.htwg.se.Card
// import de.htwg.se.Deck
// import de.htwg.se.Board
// import org.scalatest.matchers.should.Matchers
// import org.scalatest.wordspec.AnyWordSpec
// import org.scalactic.StringNormalizations._
// import java.io.ByteArrayOutputStream
//
// class BoardTest extends AnyWordSpec with Matchers {
//   "A Board" when {
//     "Initialized" should:
//       val fullDeck = fillDeck(Seq.empty[Card])
//       val d:Deck = Deck(fullDeck, "Deck")
//       val b = Board(4,3, fillBoard(4,3,d))
//       "have xSize=4" in:
//         b.xSize shouldBe 4
//       "have ySize=3" in:
//         b.ySize shouldBe 3
//       "have a brd: Vector[Vector[Card]]" in:
//         b.brd shouldBe a[Vector[Vector[Card]]]
//   }
// }
