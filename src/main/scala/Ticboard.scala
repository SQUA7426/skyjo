import scala.io.StdIn.readInt

val plus: String = "+"
val minus: String = "-"
val sep: String = "|"
val space: String = " "

def printRow1(rows: Int, rl: Int): Unit =
  for r: Int <- 1 to rows do print(plus + minus * rl * 2)
  println(plus)

def printRow2(rows: Int, rl: Int): Unit =
  for r2: Int <- 1 to rows do print(sep + space * rl * 2)
  println(sep)

def cReg(rows: Int, cols: Int, rl: Int, cl: Int): Unit =
  for c: Int <- 0 to cols do
    printRow1(rows, rl)
    if (c != cols) then for c2: Int <- 0 to cl do printRow2(rows, rl)

@main def Ticb(): Unit =
  println("Enter numbers of rows:")
  var rr = readInt()
  println("Enter row-height:")
  var rh = readInt()
  println("Enter numbers of columns:")
  var ll = readInt()
  println("Enter column-width:")
  var lw = readInt()
  cReg(rr, ll, rh, lw)
