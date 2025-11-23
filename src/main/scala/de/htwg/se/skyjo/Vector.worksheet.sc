import scala.collection.immutable.Vector
import scala.util.Random
import scala.math
import scala.collection.immutable.Seq
val initSize = 2*2
val arr: Array[Int] = Random.shuffle({(for {i <- 0 until initSize} yield i)}).toArray
arr.foreach(println)
