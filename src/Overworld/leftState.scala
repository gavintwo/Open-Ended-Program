package Overworld

class leftState (gameMap: GameMap) extends currentState (gameMap) {
  override def shiftUp(): Unit = {
    //Does nothing
  }
  override def shiftDown(): Unit = {
    //Does nothing
  }
  override def shiftLeft(): Unit = {
    //Does nothing
  }
  override def shiftRight(): Unit = {
    gameMap.state = new mainState(gameMap)
    gameMap.applyMain()
  }
  override def stateToString(): String = {
    "leftState"
  }

}
