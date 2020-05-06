package server

object MapTile {

  def generateRow(row: String): List[MapTile] = {
    row.map((ch: Char) => MapTile(ch.toString)).toList
  }

  def apply(tileType: String): MapTile = {
    tileType match {
        //The passability is true/false based on having all items. Exception handlers will be used to later also determine true/false
      case "-" => new MapTile("grass", true)
      case "T" => new MapTile("treasure chest", true)
      case "O" => new MapTile("wall", false)
      case "." => new MapTile("sand", true)
      case "#" => new MapTile("path", true)
      case "=" => new MapTile("ice", true)
      case "L" => new MapTile("lava", true)
      case "H" => new MapTile("hotsand", true)
      case "W" => new MapTile("water", true)
      case "S" => new MapTile("snow", true)
        //For the open ended, some new types must be added...
        //I want 4 different areas...
          //Ice area
          //Lava area
          //Starting grass area
          //Water area


          //Sand: Different type of enemy (Yellow), maybe slow movement
          //Water: Must have some type of swimming gear (Blue)
          //Lava: Must have some type of fire resist (red)
          //Treasure Chest: When walked over, it gives an item, then turns to a normal land (Maybe, might be hard to implement) (Gold)
          //Dirt/grass: Normal (earthy green)
          //Wall: Impassable (black)
          //Path: No enemy encounters (Impassable to enemies) (grey)
          //Ice: Maybe keep moving in straight line until a wall is hit? (Icey blue)


    }
  }

}

class MapTile(val tileType: String, val passable: Boolean) {

}
