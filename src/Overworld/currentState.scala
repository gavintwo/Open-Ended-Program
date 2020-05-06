package Overworld

abstract class currentState (gameMap: GameMap) {

  def shiftRight(): Unit

  def shiftLeft(): Unit

  def shiftUp(): Unit

  def shiftDown(): Unit

  def stateToString(): String

}
