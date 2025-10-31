import scala.collection.immutable.Vector
import scala.util.Random
import scala.compiletime.ops.string
import scala.math
case class Field(n: Int)
// val v: Vector[Field] = Vector.empty
// val v1: Vector[Field] = (for (i <- 0 to 12) yield Field(i)).toVector
// val v2: Vector[Field] = (for (i <- 0 to 12) yield Field(i)).toVector
// val v3 = v1 ++ v2
// val v4: Vector[Field] = (for (i <- 0 to 12) yield Field(i)).toVector
// v.appended(v3 :+ v4)
// def leftOf(x: Int): Int = v.filter(c => c.n == x).size
//
// val ySize = 4
// val xSize = 3
//
// val v5: Vector[Vector[Field]] =
//   Vector.tabulate(ySize, xSize) { (x,y) =>
//     var i = Random.between(-2,12)
//     while (leftOf(i)>0) { i = Random.between(-2,12) }
//     Field(i)
//   }

val v1: Vector[Field] =
  (for {
    i <- 1 to 10
    j <- -2 to 12
    if j != -2
  } yield Field(j)).toVector

val v2: Vector[Field] =
  (for {
    i <- 1 to 5
    j <- -2 to 0
    if j == -2 || j == 0
  } yield Field(j)).toVector

val v: Vector[Field] = v1++v2

val x = v.groupBy(identity).map(t => (t._1, t._2.size))
