package Overworld

import play.api.libs.json.{JsValue, Json}
import server.{GridLocation, MapTile, PhysicsVector}


object GameMap {

  def apply(): GameMap = {
    new GameMap {
      tiles = List(


        MapTile.generateRow("OOOOOOOOOOOOOO#OOOOOOOOOOOOOOO"),
        MapTile.generateRow("OLLLLLLH------#-----WWWWWWWWWO"),
        MapTile.generateRow("OLLLLLLH------#------WWWWWWWWO"),
        MapTile.generateRow("OLLLLHHH------#-------WWWWWWWO"),
        MapTile.generateRow("OLLLLH--------#-------WWWWWWWO"),
        MapTile.generateRow("OHHHHH--------#--------WWWWWWO"),
        MapTile.generateRow("O-------------#----------WWWWO"),
        MapTile.generateRow("O-------------#----------WWWWO"),
        MapTile.generateRow("O-------------#--------------O"),
        MapTile.generateRow("O-------------#--------------O"),
        MapTile.generateRow("##############################"),
        MapTile.generateRow("O-------------#-OOOOOO=OOOOOOO"),
        MapTile.generateRow("O-------------#-O============O"),
        MapTile.generateRow("O..-----------#-O==O=========O"),
        MapTile.generateRow("O.....--------#-O=========O==O"),
        MapTile.generateRow("O........-----#-O============O"),
        MapTile.generateRow("O...........--#-O=O==========O"),
        MapTile.generateRow("O............-#-O=====O==T===O"),
        MapTile.generateRow("O.............#-=========O===O"),
        MapTile.generateRow("OOOOOOOOOOOOOO#OOOOOOOOOOOOOOO")

      )
      startingLocation = new GridLocation(1, 10)
    }
  }

}

/*
case "-" => new MapTile("grass", true)
case "T" => new MapTile("treasure chest", true)
case "O" => new MapTile("wall", false)
case "." => new MapTile("sand", true)
case "#" => new MapTile("path", true)
case "=" => new MapTile("ice", true)
case "L" => new MapTile("lava", true)
case "W" => new MapTile("water", true)

 */

class GameMap {
  var state: currentState = new mainState(this)
  var tiles: List[List[MapTile]] = List()
  var startingLocation = new GridLocation(0, 0)

  def tilesJSON(): JsValue = {
    Json.toJson(tiles.map((row: List[MapTile]) => row.map((tile: MapTile) => Json.toJson(
      Map("type" -> Json.toJson(tile.tileType), "passable" -> Json.toJson(tile.passable))
    ))))
  }

  def physicsVectorToMapTile(inputVector: PhysicsVector): MapTile = {
    val inputY: Int = (inputVector.y - 0.5).toInt
    val inputX: Int = (inputVector.x - 0.5).toInt
    val row: List[MapTile] = tiles.apply(inputY)
    val tile: MapTile = row.apply(inputX)
    tile
  }

  def shiftUp(): Unit = {
    this.state.shiftUp()
  }

  def shiftDown(): Unit = {
    this.state.shiftDown()
  }

  def shiftLeft(): Unit = {
    this.state.shiftLeft()
  }

  def shiftRight(): Unit = {
    this.state.shiftRight()
  }

  def applyUp(): Unit = {
      tiles = List(


        MapTile.generateRow("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOO"),
        MapTile.generateRow("OWWWWWWWWWWO.T.OWWWWWWWWWWWWWO"),
        MapTile.generateRow("OWWWWWWWWWWO...OWWWWWWWWWWWWWO"),
        MapTile.generateRow("OWWWWWWWWWWOW.WOWWWWWWWWWWWWWO"),
        MapTile.generateRow("OWWWWWWWWWWOWWWOOWWWWWWWWWWWWO"),
        MapTile.generateRow("OWWWWWWWWWWOWWWWOOOOOWWWWWWWWO"),
        MapTile.generateRow("OWWWWWWWWWWWOWWWWWWWWWWWWWWWWO"),
        MapTile.generateRow("OWWWWWWWWWWWOOOOOOOOOOWWWWWWWO"),
        MapTile.generateRow("OWWWWWWWWWWWWWWWWWWWWWWWWWWWWO"),
        MapTile.generateRow("OWWWWWWWWOOWWOWWWWWWWWWWWWWWWO"),
        MapTile.generateRow("OWWWWWWWWWWWOOOWWWWWWWWWWWWWWO"),
        MapTile.generateRow("OWWWWWWWWWWWWWWWWWWWWWWWWWWWWO"),
        MapTile.generateRow("OWWWWOOOWWWWWWWWWWWWWWOOWWWWWO"),
        MapTile.generateRow("OWWWWWOWWWWWWWWWWWWWWWWOWWWWWO"),
        MapTile.generateRow("OWWWWWWWWWWWWWWWWWWWWWWWWWWWWO"),
        MapTile.generateRow("OWWWWWWWWWWWWWWWWWWWWWWWWWWWWO"),
        MapTile.generateRow("OWWWWWWWWWWW.......WWWWWWWWWWW"),
        MapTile.generateRow("OWWWWWWWWW....--.....WWWWWWWWW"),
        MapTile.generateRow("OWWWWWW......---.......WWWWWWW"),
        MapTile.generateRow("OOOOOOOOOOOOOO#OOOOOOOOOOOOOOO")

      )
  }

  def applyDown(): Unit = {
    tiles = List(


      MapTile.generateRow("OOOOOOOOOOOOOO#OOOOOOOOOOOOOOO"),
      MapTile.generateRow("OSSSSSSSSSSSSS#SSSSSSSSSSSSSSO"),
      MapTile.generateRow("OSSSSSSSSSSSSSSSSSSSSSSSSSSSSO"),
      MapTile.generateRow("OSSSSSSSSSSSSS#SSSSSSSSSSSSSSO"),
      MapTile.generateRow("OSSSSSSSSSSSSS#SSSSSSSSSSSSSSO"),
      MapTile.generateRow("OSSSSSSSSSSSSSSSSSSSSSSSSSSSSO"),
      MapTile.generateRow("OSSSSSSSSSSSSSSSSSSSSSSSSSSSSO"),
      MapTile.generateRow("OSSSSSSSSSSSSSSSSSSSSSSSSSSSSO"),
      MapTile.generateRow("O=O=OOOOOOOOOOOOOOOO=OOOOOOO=O"),
      MapTile.generateRow("O============================O"),
      MapTile.generateRow("O=========O=====O============O"),
      MapTile.generateRow("O===================O========O"),
      MapTile.generateRow("O==========O=================O"),
      MapTile.generateRow("O==O================O========O"),
      MapTile.generateRow("O=====O=================O====O"),
      MapTile.generateRow("O==O====================TO===O"),
      MapTile.generateRow("O====O==================O====O"),
      MapTile.generateRow("O=========O========O=========O"),
      MapTile.generateRow("O============================O"),
      MapTile.generateRow("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOO")

    )
  }

  def applyLeft(): Unit = {
    tiles = List(

    //Lava
      MapTile.generateRow("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOO"),
      MapTile.generateRow("OHHHHHHHHHHHHHHHHHHLLLLLLLLLLO"),
      MapTile.generateRow("OHHHHHHHHHHHHHLLHHLLLLLLLLLLLO"),
      MapTile.generateRow("OLHHHHHHHHHHHHHHLLHHHLLLLLLLLO"),
      MapTile.generateRow("OHLLLHHHHHHHHHHLHHHHHHLLLLLLLO"),
      MapTile.generateRow("OHHLHHHHHHHHHHHLLHHHHHHHLLLLLO"),
      MapTile.generateRow("OHHHLLHHHHHHHHLHHLHHHHHHHHLLLO"),
      MapTile.generateRow("OHHHHHHHLHHHLHHHHHLHHHHHHHHHHO"),
      MapTile.generateRow("OHHHHHHHHLHLHHHHHHHHHHHHHHHHHO"),
      MapTile.generateRow("OHHHHHHHHHLHHHHHHHHHHHHHHHHH#O"),
      MapTile.generateRow("OHHHHHHHHHHLHHLHHHHHHHHHHHH###"),
      MapTile.generateRow("OHHHHHHHHHHLLLHHHHHHHHHHHHHH#O"),
      MapTile.generateRow("OHHHHHHHHLLHHHLHHHHHHHHHHHHHHO"),
      MapTile.generateRow("OHHHHHHHLHHHHHHLHHHHHHHHHHHHHO"),
      MapTile.generateRow("OHHHHHHHLHHHHHHHLLHHHHHLHHHHHO"),
      MapTile.generateRow("OHHLLHHHLHLLHHHHHHHHHHHLHHHHHO"),
      MapTile.generateRow("OHHHHLHLLLHHHHHHHHHHHHHHLHHLLO"),
      MapTile.generateRow("OHHHHHLLHHHHHHHHHHHHHHHHHLLHHO"),
      MapTile.generateRow("OHHTHHLHHHHHHHHHHHHHHHHHLHHHHO"),
      MapTile.generateRow("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOO")

    )
  }

  def applyRight(): Unit = {
    tiles = List(


      MapTile.generateRow("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOO"),
      MapTile.generateRow("O............................O"),
      MapTile.generateRow("O............................O"),
      MapTile.generateRow("O............................O"),
      MapTile.generateRow("OOOOOOOOOOOOOOOOO.OOOOOOOOOOOO"),
      MapTile.generateRow("O...............O.O..........O"),
      MapTile.generateRow("O.OOOOOOOOOOOOOOO.O.OOOOOOOO.O"),
      MapTile.generateRow("O.................O.O........O"),
      MapTile.generateRow("O.OOOOOOOOO.OOOOOOO.O.OOOOOOOO"),
      MapTile.generateRow("O.O.........O.......O........O"),
      MapTile.generateRow("#.O.OOOOOO.OOOOOOOOOOOOOOOOOOO"),
      MapTile.generateRow("O.O......O.O.................O"),
      MapTile.generateRow("O.O.O.OOOO.O.OOOOOOOOOOOOOOOOO"),
      MapTile.generateRow("O.O.O.O......................O"),
      MapTile.generateRow("OOO.O.O.OOOOOOOOOOOOOOOOOOOO.O"),
      MapTile.generateRow("OOO.O.O.O....................O"),
      MapTile.generateRow("OOOOO.OOO....................O"),
      MapTile.generateRow("O.OOO.O.................T....O"),
      MapTile.generateRow("O.....O......................O"),
      MapTile.generateRow("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOO")

    )
  }

  def applyMain(): Unit = {
      tiles = List(


        MapTile.generateRow("OOOOOOOOOOOOOO#OOOOOOOOOOOOOOO"),
        MapTile.generateRow("OLLLLLLH------#-----WWWWWWWWWO"),
        MapTile.generateRow("OLLLLLLH------#------WWWWWWWWO"),
        MapTile.generateRow("OLLLLHHH------#-------WWWWWWWO"),
        MapTile.generateRow("OLLLLH--------#-------WWWWWWWO"),
        MapTile.generateRow("OHHHHH--------#--------WWWWWWO"),
        MapTile.generateRow("O-------------#----------WWWWO"),
        MapTile.generateRow("O-------------#----------WWWWO"),
        MapTile.generateRow("O-------------#--------------O"),
        MapTile.generateRow("O-------------#--------------O"),
        MapTile.generateRow("##############################"),
        MapTile.generateRow("O-------------#-OOOOOO=OOOOOOO"),
        MapTile.generateRow("O-------------#-O============O"),
        MapTile.generateRow("O..-----------#-O==O=========O"),
        MapTile.generateRow("O.....--------#-O=========O==O"),
        MapTile.generateRow("O........-----#-O============O"),
        MapTile.generateRow("O...........--#-O=O==========O"),
        MapTile.generateRow("O............-#-O=====O==T===O"),
        MapTile.generateRow("O.............#-=========O===O"),
        MapTile.generateRow("OOOOOOOOOOOOOO#OOOOOOOOOOOOOOO")

      )

  }
}
