package de.htwg.se
import scala.io.StdIn.readInt

object TicBoard:
  def apply(rw: Int, rlen: Int, lw: Int, lwid: Int) = {
    require(rw > 0, "Numbers of Rows should be > 0")
    require(rlen > 0, "Size of Row-Length should be > 0")
    require(lw > 0, "Numbers of Columns should be > 0")
    require(lwid > 0, "Size of Column-Length should be > 0")
    new TicBoard(rw,rlen,lw,lwid)
  }

case class TicBoard(
    rows: Int,
    rl: Int,
    cols: Int,
    cl: Int
) {
  def printRow1(): Unit =
    for r: Int <- 0 until rows do print("+" + "-" * rl * 2)
     println("+")
  def printRow2(): Unit =
    for r2: Int <- 0 until rows do print("|" + " " * rl * 2)
     println("|")

  def cReg(): Unit =
    for c: Int <- 0 until cols + 1 do
      printRow1()
      if (c != cols) then for c2: Int <- 0 until cl do printRow2()
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
  val t = new TicBoard(rr,rh,ll,lw)
  t.cReg()
