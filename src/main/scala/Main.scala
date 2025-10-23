package de.htwg.se
<<<<<<< HEAD

//aaaaaaaaaaaaaaaaaaaaaaaa
object main {
  def main(args: Array[String]): Unit = {


    val scal:Int = 5
    val hor: String = "_"
    val vert: String = "|"
    val plus: String = "+"
    val empt: String = " "


    for(i<- 1 to 3) {
      for (i <- 0 to scal) {
        println(empt * scal + vert + empt * scal + vert)
      }
      if(i!=3) {
        println(hor * (scal * 3))
      }
    }
  }
}
=======

import scala.io.StdIn.readInt

@main def starting(): Unit =
  println("Enter number of players:")
  val playerCount = readInt()
  println("ok")
>>>>>>> origin/main
