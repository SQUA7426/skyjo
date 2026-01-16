package de.htwg.se.skyjo.util

import scalafx.event.Event
trait Observer {
   def update: Boolean
}

class Observable:
  var subscribers: Vector[Observer] = Vector()

  def add(s: Observer): Unit =
    subscribers = subscribers :+ s
  // def add(s: Observer): Int =
  //   subscribers = subscribers :+ s
  //   subscribers.size -1

  def remove(s: Observer): Unit = subscribers = subscribers.filterNot(o => o == s)

  def notifyObservers: Unit = subscribers.foreach(o => o.update)
