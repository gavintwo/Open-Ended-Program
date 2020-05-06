package Overworld

class upState (gameMap: GameMap) extends currentState (gameMap) {
  override def shiftUp(): Unit = {
    //Does nothing
  }
  override def shiftDown(): Unit = {
    gameMap.state = new mainState(gameMap)
    gameMap.applyMain()
  }
  override def shiftLeft(): Unit = {
    //Does nothing
  }
  override def shiftRight(): Unit = {
    //Does nothing
  }
  override def stateToString(): String = {
    "upState"
  }
}
