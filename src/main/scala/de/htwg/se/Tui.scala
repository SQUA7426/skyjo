 package de.htwg.se

 import de.htwg.se.Card
 import de.htwg.se.Deck
 import de.htwg.se.Board
 import scala.io.StdIn.readInt

 def chose(): Unit = println("world!")
 def printInteract(
     b: Board,
     discard: DiscardPile,
     d: Deck,
     h: Hand
 ): Unit = {

   b.str()
   println("\n Discard    Deck     Hand")
   Seq("  +----+ ", "  |    | ").foreach(t => println(t * 3))
   println("  " + discard.toString() + "  " + d.toString().substring(0, 5) + "|   " + h.toString().substring(0,5) + "|")
   Seq("  |    | ", "  +----+ ").foreach(t => println(t * 3))
 }

 @main def startingPoint2(): Unit =


   print("Enter playerCounts [between 2 up to 8]:")
     val plCount: Int = readInt()
     println(plCount)
     //val plCount: Int = 3
     val plBoards: Array[Board] = new Array(plCount)
   println("1")
     val plHands: Array[Hand] = new Array(plCount)
   println("2")
     val vector: Vector[Card] = fillDeck()
   println("3")
     val deck: Deck = new Deck(vector,String.valueOf(vector.head))
   println("4")
     val disc: DiscardPile = new DiscardPile()
   println("5")
   println(deck.deck)
     for i <- 0 until plCount do
       plBoards(i) = new Board(4,3,fillBoard(deck))
       println(plBoards(i).toString())
       println("6")
/*
       plHands(i) = new Hand()
       deck.turnUpperCard()
       println("avava")
       printInteract(plBoards(i),disc,deck,plHands(i))
       plHands(i).takeFromDeck(deck)
       printInteract(plBoards(i),disc,deck,plHands(i))

*/