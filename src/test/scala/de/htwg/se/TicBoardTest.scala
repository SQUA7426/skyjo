// package de.htwg.se
//
// import de.htwg.se.TicBoard
// import org.scalatest.matchers.should.Matchers
// import org.scalatest.wordspec.AnyWordSpec
// import org.scalactic.StringNormalizations._
// import java.io.ByteArrayOutputStream
//
// class TicBoardTest extends AnyWordSpec with Matchers {
//   "A TicBoard" when {
//     "Initialized with 3,3,3,3" should:
//       "be acceptable" in:
//         TicBoard.apply(3, 3, 3, 3)
//     "Initialized with 0,0,0,0" should:
//       "not be acceptable" in:
//         val TicErr =
//           the[IllegalArgumentException] thrownBy (TicBoard.apply(0, 0, 0, 0))
//   }
// }
