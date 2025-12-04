package de.htwg.se.skyjo.util

import de.htwg.se.skyjo.model.{Board, Card, Deck, DiscardPile, fullDeck}

import scala.collection.mutable
import scala.collection.mutable.Stack

case class Memento(
                        fromDeck: Boolean,      // true = Deck, false = DiscardPile
                        takenCard: Card,
                        boardIndex: Int,
                        replacedCard: Card
                      )



class MoveCaretaker {
  private val undoStack = Stack[Memento]()
  private val redoStack = Stack[Memento]()
  private var updtDeck : Deck = new Deck(fullDeck()._1,fullDeck()._2)
  var memAct : Boolean = false
  
  
  def save(m: Memento): Unit = {
    undoStack.push(m)
    redoStack.clear()
  }

  def undo(memento : Memento, deck : Deck, board :Board, disc : DiscardPile) =   {
    if(memento.fromDeck){
        val tempV : Vector[Card] = memento.takenCard +: deck.deck;
        updtDeck =  Deck(tempV, memento.takenCard.toString())
        board.switch(memento.replacedCard, memento.boardIndex)
        setTrue()
    } else 
    disc.putToDiscardPile(memento.takenCard)
    board.switch(memento.replacedCard,memento.boardIndex)
    setTrue()
  }

  def redo(memento : Memento, deck : Deck, board :Board, disc : DiscardPile) =   {
    if(memento.fromDeck){
    updtDeck =  Deck(deck.deck.tail, memento.takenCard.toString())
    board.switch(memento.takenCard, memento.boardIndex)
    setTrue()
  } else
    disc.putToDiscardPile(memento.replacedCard)
    board.switch(memento.takenCard,memento.boardIndex)
    setTrue() 
  }
  def setTrue(): Unit = memAct = true
  def setFalse(): Unit = memAct = false
  def checkMemAct(): Boolean = memAct
  def getNewDeck(): Deck = updtDeck
}