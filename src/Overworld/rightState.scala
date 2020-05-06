package Overworld

class rightState (gameMap: GameMap) extends currentState (gameMap) {
  override def shiftUp(): Unit = {
    //Does nothing
  }
  override def shiftDown(): Unit = {
    //Does nothing
  }
  override def shiftLeft(): Unit = {
    gameMap.state = new mainState(gameMap)
    gameMap.applyMain()
  }
  override def shiftRight(): Unit = {
    //Does nothing
  }
  override def stateToString(): String = {
    "rightState"
  }
}