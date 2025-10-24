package de.htwg.se

def len(x: Int): Int = x.toString().size

class Card(v: Int) {
  val value = v
  override def toString(): String = if len(v)==2 then s"| ${v}  | " else s"|  ${v}  | "
}

def isCard(c: Any): Boolean = c match { case _: Card => true case _ => false}
