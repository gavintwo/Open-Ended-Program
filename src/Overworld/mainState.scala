package Overworld

class mainState(gameMap: GameMap) extends currentState(gameMap) {
  override def shiftUp(): Unit = {
    gameMap.state = new upState(gameMap)
    gameMap.applyUp()
  }
  override def shiftDown(): Unit = {
    gameMap.state = new downState(gameMap)
    gameMap.applyDown()
  }
  override def shiftLeft(): Unit = {
    gameMap.state = new leftState(gameMap)
    gameMap.applyLeft()
  }
  override def shiftRight(): Unit = {
    gameMap.state = new rightState(gameMap)
    gameMap.applyRight()
  }
  override def stateToString(): String = {
    "mainState"
  }

}
