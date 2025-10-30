val a = "45"
val b = 9

21 + 9

def f(x: Int): Int = x + 3
f(4)

import scala.collection.immutable.Seq
// import scala.util.Random
// import de.htwg.se.Deck
// import de.htwg.se.Card
// case class Board(board: Vector[Vector[Card]], d: Deck) {
//   val ySize = 3
//   val xSize = 4
//   var brd: Vector[Vector[Card]] = fillBoard(d)
//
//   def fillBoard(d: Deck): Vector[Vector[Card]] =
//     Vector.tabulate(ySize, xSize) { (y, x) =>
//       {
//         var ran: Int = Random.between(-2, 12)
//         while (d.cardsLeftOf(ran) > 0) do ran = Random.between(-2, 12)
//         d.removeCardFromDeck(ran, d.deck)
//         Card(ran)
//       }
//     }
//
//   // override def toString(): String = {
//   def str():Unit = {
//     val s1: Seq[String] = Seq("+-----+ ", "|     | ").foreach(t => t.map(l => l * xSize))
//     val s2: Seq[String] = Seq("|     | ", "+-----+ ").foreach(t => t.map(l => l * xSize))
//     // s1 + s2
//   }
// }

val xSize = 4
val s1: Seq[String] = Seq("+-----+ ", "|     | ").map(t => t.repeat(xSize))
val s2: Seq[String] = Seq("|     | ", "+-----+ ").map(t => t.repeat(xSize))
val s3 = (s1++s2).foreach(println)

