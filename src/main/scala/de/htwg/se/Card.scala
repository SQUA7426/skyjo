package de.htwg.se

def len(x: Int): Int = x.toString().size

class Card(v: Int) {
  val value: Int = {
    require(v >= -2 && v <= 12, "Card value should be >= -2 and <= 12")
    // v.toString()
    v
  }
  override def toString(): String =
    // value match {
    //   case _: Int     => s"${value}"
    // case _: String  =>
    if len(v) == 2 then s"| ${v}  | "
    else s"|  ${v}  | "
    // }
}

def isCard(c: Any): Boolean = c match {
  case _: Card => true
  case _       => false
}
