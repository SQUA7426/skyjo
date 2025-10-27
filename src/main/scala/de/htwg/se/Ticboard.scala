import scala.io.StdIn.readInt

val plus: String = "+"
val minus: String = "-"
val sep: String = "|"
val space: String = " "

def printRow1(rows: Int, rl: Int): Unit = {
  for r: Int <- 0 until rows do
    print(plus + minus * rl * 2)
  println(plus)
}

def printRow2(rows: Int, rl: Int): Unit = {
  for r2: Int <- 0 until rows do
    print(sep + space * rl * 2)
  println(sep)
}

def cReg(rows: Int, cols: Int, rl: Int, cl: Int): Unit =
  for c: Int <- 0 until cols+1 do
    printRow1(rows, rl)
    if (c != cols) then
      for c2: Int <- 0 to cl do
        printRow2(rows, rl)

@main def TicBoard(): Unit =
  println("Enter numbers of rows:")
  val rr = scala.io.StdIn.readInt()
  println("Enter row-height:")
  val rh = scala.io.StdIn.readInt()
  println("Enter numbers of columns:")
  val ll = scala.io.StdIn.readInt()
  println("Enter column-width:")
  val lw = scala.io.StdIn.readInt()
  cReg(rr, ll, rh, lw)


