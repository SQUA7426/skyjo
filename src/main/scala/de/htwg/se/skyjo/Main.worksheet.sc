val a = "45"
val b = 9

21 + 9

def f(x: Int): Int = x + 3
f(4)

import scala.collection.immutable.Seq
val xSize = 4
val s1: Seq[String] = Seq("+-----+ ", "|     | ").map(t => t.repeat(xSize))
val s2: Seq[String] = Seq("|     | ", "+-----+ ").map(t => t.repeat(xSize))
val s3 = (s1++s2).foreach(println)

