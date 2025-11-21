import scala.collection.immutable.Vector
import scala.util.Random
import scala.math
import scala.collection.immutable.Seq
case class Field(val value:Int, turned:Boolean = false) {
  override def toString(): String = if(!turned) "#" else s"${value}"
}
case class Brd(val xSize:Int, val ySize:Int, val brd: /*Vector[*/Vector[Field]/*]*/ = Vector.empty[Field])
val xSize:Int = 4
val ySize: Int = 3
val b: Vector[Vector[Field]] = Vector(Vector(Field(1),Field(2)),Vector(Field(3),Field(4)))
def printBrd(b: Vector[Vector[Field]]): Unit = b.flatten.foreach(t => print(s"${t} "))
printBrd(b)

