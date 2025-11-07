import scala.collection.immutable.Vector
import scala.util.Random
import scala.math
import scala.collection.immutable.Seq
case class Field(val value: Int) {
  override def toString(): String = s"${value}"
}
def toField(x: Any): Field = {
  val val1d =
    (for { j <- -2 to 12 } yield j.toString()).toVector
  x match {
    case a: Int                         => Field(a.toInt)
    case b: String if val1d.contains(b) => Field(Integer.parseInt(b))
    case other =>
      throw new IllegalArgumentException(s"Invalid input:$other")
  }
}

// val str = "-2"
// val n = toField(str)

// val a4thDeck = new dDeck(a3rdhDeck.deck, a3rdhDeck.turnUpper())
// println(a4thDeck.upper)
// val aHand = new hHand("Hand")
// val a5thDeck = new dDeck(aDeck.deck, aDeck.turnUpper())
// println(a5thDeck.upper)
// val (a2ndHand, a6thDeck) = aHand.takeFromDeck(a5thDeck)
// println(a6thDeck.deck.size)
// val a7thDeck = new dDeck(a6thDeck.deck, a6thDeck.turnUpper())
// println(a7thDeck.upper)
// val (a3rdHand, a8thDeck) = aHand.takeFromDeck(a7thDeck)
// println(a8thDeck.deck.size)
val v1: Vector[Field] =
  (for {
    i <- 1 to 10
    j <- -2 to 12
    if j != -2
  } yield Field(j)).toVector

val v2: Vector[Field] =
  (for {
    i <- 1 to 5
    j <- -2 to 0
    if j == -2 || j == 0
  } yield Field(j)).toVector

val v: Vector[Field] = v1 ++ v2

val x = v.groupBy(identity).map(t => (t._1, t._2.size))

def shuffle(deck: Vector[Field]): Vector[Field] = Random.shuffle(deck)
val v3 = shuffle(v)
case class dDeck(deck: Vector[Field], upper: String) {
  def turnUpper(): String =
    upper.compareTo("Deck") match {
      case 0 => deck.last.toString()
      case _ => "Deck"
    }
  def remove(amount: Int): Vector[Field] = {
    val nDeck = deck.dropRight(amount)
    nDeck
  }
  def leftOf(worth: Int): Int = deck.count(_ == (new Field(worth)))
  def getUpper(): Field = if (upper.compareTo("Deck") != 0) {
    toField(upper.toInt)
  } else { throw new IllegalArgumentException(s"Invalid upper:${upper}") }
}
case class hHand(card: String) {
  val handCard: String = card

  def takeFromDeck(d: dDeck): (hHand, dDeck) =
    (new hHand(d.upper), dDeck(d.remove(1), "Deck"))
}
val aDeck = new dDeck(v3, "Deck")
// println(aDeck.upper)
val a2ndDeck = new dDeck(v3, aDeck.turnUpper())
// println(a2ndDeck.upper)
val a3rdhDeck = new dDeck(a2ndDeck.remove(1), "Deck")
println(a3rdhDeck.upper)
// println(a3rdhDeck.deck.size)
val seqCards = Seq.empty[Field]
val diffs: Vector[Field] = v3.diff(seqCards)
val shuffled = Random.shuffle(diffs)
// def fillDeck(seqCards: Seq[Field]): Vector[Field] =
//   shuffled

def fillBoard(xSize: Int, ySize: Int, d: dDeck): Vector[Vector[Field]] =
  if (d.deck.isEmpty) {
    val deck2 = /*fillDeck(Seq.empty[Field])*/ shuffled
    fillBoard(xSize, ySize, dDeck(deck2, "Deck"))
  } else {
    // draw one Field and produce updated deck
    def drawField(deck: dDeck): (Field, dDeck) = {
      // if upper is "Deck", flip it and make a new dDeck
      val turnedDeck =
        if (deck.upper == "Deck") dDeck(deck.deck, deck.turnUpper())
        else deck

      val topCard = turnedDeck.getUpper()
      val newDeck = dDeck(turnedDeck.remove(1), "Deck") // reset state to "Deck" maybe?
      (topCard, newDeck)
    }

    // fill one row
    def fillRow(deck: dDeck, n: Int): (Vector[Field], dDeck) =
      if (n == 0) (Vector.empty, deck)
      else {
        val (field, nextDeck) = drawField(deck)
        val (rest, finalDeck) = fillRow(nextDeck, n - 1)
        (field +: rest, finalDeck)
      }

    // fill all rows
    def fillRows(deck: dDeck, n: Int): (Vector[Vector[Field]], dDeck) =
      if (n == 0) (Vector.empty, deck)
      else {
        val (row, nextDeck) = fillRow(deck, xSize)
        val (rows, finalDeck) = fillRows(nextDeck, n - 1)
        (row +: rows, finalDeck)
      }

    // build full board
    val (board, _) = fillRows(d, ySize)
    board
  }

val s = fillBoard(4, 3, aDeck)
s.flatten.foreach(t=> printf(s"${t} | "))
 s.flatten.toSeq.map(t => s"${t}")
val l1 = s.flatten.toSeq.map(t => t.value)
s.flatten.toSeq.map(t => s" ${t} |").mkString
// val nLine = l1.zipWithIndex.map {case(value,idx) => if (idx%4==0) s"${value}\n" else s"${value}"}
