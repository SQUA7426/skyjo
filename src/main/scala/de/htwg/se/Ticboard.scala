package de.htwg.se
import scala.io.StdIn.readInt

val plus: String = "+"
val minus: String = "-"
val sep: String = "|"
val space: String = " "

case class TicBoard(
    rows: Int,
    rl: Int,
    cols: Int,
    cl: Int
) {

  private def printRow1(): Unit =
    for r: Int <- 0 until rows do print(plus + minus * rl * 2)
     println(plus)
  private def printRow2(): Unit =
    for r2: Int <- 0 until rows do print(sep + space * rl * 2)
     println(sep)

  def cReg(): Unit =
    for c: Int <- 0 until cols + 1 do
      printRow1()
      if (c != cols) then for c2: Int <- 0 to cl do printRow2()
}

@main def Tic1(): Unit =
  println("Enter numbers of rows:")
  val rr = scala.io.StdIn.readInt()
  println("Enter row-height:")
  val rh = scala.io.StdIn.readInt()
  println("Enter numbers of columns:")
  val ll = scala.io.StdIn.readInt()
  println("Enter column-width:")
  val lw = scala.io.StdIn.readInt()
  val t = TicBoard(rr,rh,ll,lw)
  t.cReg()
