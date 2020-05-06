package Overworld

class downState (gameMap: GameMap) extends currentState (gameMap) {
  override def shiftUp(): Unit = {
    gameMap.state = new mainState(gameMap)
    gameMap.applyMain()
  }
  override def shiftDown(): Unit = {
    //Does nothing
  }
  override def shiftLeft(): Unit = {
    //Does nothing
  }
  override def shiftRight(): Unit = {
    //Does nothing
  }

  override def stateToString(): String = {
    "downState"
  }
}