package player

import server.{GridLocation, MapTile, PhysicsVector}
import scala.collection.mutable

object playerMovement {

  def compareWithinEpsilon(a: Double, b: Double): Boolean = {
    val epsilon: Double = 0.1

    if (Math.abs(a - b) < epsilon) {
      true
    }
    else {
      false
    }
  }

  def gridLocationToPhysicsVector(inputGridLocation:GridLocation): PhysicsVector = {
    val outputVector: PhysicsVector = new PhysicsVector(inputGridLocation.x + 0.5, inputGridLocation.y + 0.5)
    outputVector
    //The PhysicsVector represents the center of the tile in space
  }

  def physicsVectorToGridLocation(inputPhysicsVector: PhysicsVector): GridLocation = {
    new GridLocation(inputPhysicsVector.x.toInt,inputPhysicsVector.y.toInt)
    //The GridLocation represents the bottom left corner in space
  }

  def magnitudeNormalization(inputX: Double, inputY: Double): PhysicsVector = {
    //a^2 = b^2 + c^2
    var outputVector: PhysicsVector = new PhysicsVector(0,0)
    val prenormalizedMagnitude: Double = Math.pow(Math.pow(inputX,2) + Math.pow(inputY,2),0.5)
    if (prenormalizedMagnitude != 5) {
      val multiplier: Double = 5 / prenormalizedMagnitude
      outputVector = new PhysicsVector(inputX * multiplier, inputY * multiplier)
    }
    else {
      outputVector = new PhysicsVector(inputX,inputY)
    }
    outputVector
  }

  def gridLocationToIndex (gridLocation:GridLocation, mapHeight: Int, mapWidth: Int): Int = {
    val inputLocationX: Int = gridLocation.x
    val inputLocationY: Int = gridLocation.y
    //println(inputLocationX,inputLocationY)
    val inputIndex: Int = inputLocationX + (inputLocationY) * mapWidth
    //println(inputIndex)
    inputIndex

  }

  def indexToGridLocation (inputIndex: Int, mapHeight: Int, mapWidth: Int): List[Int] = {
    //println(inputIndex)
    val outputY: Int = (Math.floor(inputIndex/mapWidth)).toInt
    val outputX: Int = (inputIndex - ((outputY) * mapWidth)).toInt
    //println(outputX,outputY)
    List(outputX,outputY)
  }

  def generateGraph(map: List[List[MapTile]], startLocation: GridLocation, endLocation: GridLocation): Graph[MapTile] = {
    //An adjacency map holds a maptile -> its valid neighbors

    //This method will take a map and its starting location and make a graph that starts at the starting location and makes a map connecting passable tiles
    //The graph will hold gridlocations, the fact that it is in the graph and connected to other gridlocations means it is passable.
    //Once this graph is generated, finding the best path is a simple process that is already defined in the graph class

    //To generate a graph, the mapTile list should be converted into a map containing the location as a string "(0,0)" -> MapTile. This allows the start location to check the x +- 1 and the y+ - 1
    //This should be done by running through the list in nested loops, keeping track of the locations. The map is generated from top to bottom, so we will start at the top and move left to right
    //Once the map is made, start to generate a graph by asking the map for the passability of the tile. If passable, add it to the graph/


    val mapGraph: Graph[MapTile] = new Graph[MapTile]

    var indexList: List[List[Int]] = List()

    var mapWidth: Int = 0
    var mapHeight: Int = 0

    for (_ <- map) {
      mapHeight += 1
    }

    for (_ <- map.head) {
      mapWidth += 1
    }

    var locationMap: Map[String,MapTile] = Map()
    var currentX: Int = 0
    var currentY: Int = 0
    var tileNumber: Int = 0

    for (row <- map) {
      var rowList: List[Int] = List()
      for (tile <- row) {
        locationMap += ( "(" + currentX.toString + "," + currentY.toString + ")" -> tile)
        mapGraph.addNode(tileNumber,tile)
        rowList = rowList :+ tileNumber
        tileNumber += 1
        currentX += 1
      }
      indexList = indexList :+ rowList
      rowList = List()
      currentX = 0
      currentY += 1
    }



    //The map has been created and the graph now has all the nodes in place. The indexes of the graph nodes are labeled right to left, top to bottom. (Reading order)

    //To add the edges, access the x and y of the gridLocation and add 1 in each of the directions to access the adjacent 4. If the adjacent ones are passable, create an edge.
    //Use an explored set and toExplore queue in order to make sure that each tile is only ever accessed once. This also includes not making a second edge for the same two tiles

    val startIndex: Int = gridLocationToIndex(startLocation, mapHeight, mapWidth)
    //This function can take an x-y coordinate and find the index to which it belongs. This will allow the program to utilize the explored and toExplore function better

    var explored: Set[Int] = Set ()
    val toExplore: mutable.Queue[Int] = mutable.Queue()
    toExplore.enqueue(startIndex)
    var currentNode: Int = startIndex

    while (toExplore.nonEmpty) { //|| (currentNode != gridLocationToIndex(endLocation, mapHeight, mapWidth))) {

      currentNode = toExplore.dequeue()
      explored = explored + currentNode
      val currentNodeCoords: List[Int] = indexToGridLocation(currentNode, mapHeight, mapWidth)

      val leftNodeCoords: List[Int] = List(currentNodeCoords.head - 1, currentNodeCoords.apply(1))
      val rightNodeCoords: List[Int] = List(currentNodeCoords.head + 1, currentNodeCoords.apply(1))
      val upNodeCoords: List[Int] = List(currentNodeCoords.head, currentNodeCoords.apply(1) - 1)
      val downNodeCoords: List[Int] = List(currentNodeCoords.head, currentNodeCoords.apply(1) + 1)

      //locationMap takes "(x,y)" -> mapTile

      if (locationMap("(" + startLocation.x.toString + "," + startLocation.y.toString + ")").passable) {
        if ((0 <= leftNodeCoords.head && leftNodeCoords.head <= mapWidth - 1) && (0 <= leftNodeCoords.apply(1) && leftNodeCoords.apply(1) <= mapHeight - 1)) {
          val leftNodeIndex: Int = gridLocationToIndex(new GridLocation(leftNodeCoords.head, leftNodeCoords.apply(1)), mapHeight, mapWidth)
            if (locationMap("(" + leftNodeCoords.head.toString + "," + leftNodeCoords.apply(1).toString + ")").passable) {
              mapGraph.addEdge(currentNode, leftNodeIndex)
              if (!explored.contains(leftNodeIndex)) {
                toExplore.enqueue(leftNodeIndex)
                explored = explored + leftNodeIndex
            }
          }
        }


        if ((0 <= rightNodeCoords.head && rightNodeCoords.head <= mapWidth - 1) && (0 <= rightNodeCoords.apply(1) && rightNodeCoords.apply(1) <= mapHeight - 1)) {
          val rightNodeIndex: Int = gridLocationToIndex(new GridLocation(rightNodeCoords.head, rightNodeCoords.apply(1)), mapHeight, mapWidth)
          if (locationMap("(" + rightNodeCoords.head.toString + "," + rightNodeCoords.apply(1).toString + ")").passable) {
              mapGraph.addEdge(currentNode, rightNodeIndex)
              if (!explored.contains(rightNodeIndex)) {
              toExplore.enqueue(rightNodeIndex)
              explored = explored + rightNodeIndex
            }
          }
        }

        if ((0 <= upNodeCoords.head && upNodeCoords.head <= mapWidth - 1) && (0 <= upNodeCoords.apply(1) && upNodeCoords.apply(1) <= mapHeight - 1)) {
          val upNodeIndex: Int = gridLocationToIndex(new GridLocation(upNodeCoords.head, upNodeCoords.apply(1)), mapHeight, mapWidth)
            if (locationMap("(" + upNodeCoords.head.toString + "," + upNodeCoords.apply(1).toString + ")").passable) {
              mapGraph.addEdge(currentNode, upNodeIndex)
              if (!explored.contains(upNodeIndex)) {
                toExplore.enqueue(upNodeIndex)
                explored = explored + upNodeIndex

            }
          }
        }

        if ((0 <= downNodeCoords.head && downNodeCoords.head <= mapWidth - 1) && (0 <= downNodeCoords.apply(1) && downNodeCoords.apply(1) <= mapHeight - 1)) {
          val downNodeIndex: Int = gridLocationToIndex(new GridLocation(downNodeCoords.head, downNodeCoords.apply(1)), mapHeight, mapWidth)
            if (locationMap("(" + downNodeCoords.head.toString + "," + downNodeCoords.apply(1).toString + ")").passable) {
              mapGraph.addEdge(currentNode, downNodeIndex)
              if (!explored.contains(downNodeIndex)) {
                toExplore.enqueue(downNodeIndex)
                explored = explored + downNodeIndex
            }
          }
        }
      }
      else
        {}
      }
    mapGraph
  }

  def findPath(start: GridLocation, end: GridLocation, map: List[List[MapTile]]): List[GridLocation] = {
   //This will take a map, a start location, and an end location. This will return a list of grid locations that are the steps to reach the end.
   //First, the start location must branch out to every adjacent tile
      //The same tile may not be visited twice
      //The tiles will be appended to a Map that contains the (tileFound -> tileThatCameBeforeIt)
      //Once tileFound == end, stop the loops and recount the steps.
      //This means run a loop until currentTile == start
        //At each stop, keep track of how many steps have been taken and which steps those are. Make an output list (*Prepend!* to make it the correct order)

    //Some possible exception handlers
      //If the end location is impassable, return empty path
      //If start location is impassable, return empty path


    var mapWidth: Int = 0
    var mapHeight: Int = 0

    for (_ <- map) {
      mapHeight += 1
    }

    for (_ <- map.head) {
      mapWidth += 1
    }

    var outputPath: List[GridLocation] = List()

    if (((0 <= start.x && start.x <= mapWidth - 1 )&& (0 <= start.y && start.y <= mapHeight - 1)) && (((0 <= end.x)&&(end.x <= mapWidth - 1))&&((0 <= end.y)&&(end.y <= mapHeight - 1)))) {

      val mapGraph: Graph[MapTile] = generateGraph(map, start, end)

      val startIndex: Int = gridLocationToIndex(start, mapHeight, mapWidth)
      val endIndex: Int = gridLocationToIndex(end, mapHeight, mapWidth)


      //This checks that we haven't clicked on the same tile
      //if (startIndex != endIndex) {
      //This checks to make sure that both start and end are within the bounds of the map


      val indexPath: List[Int] = mapGraph.pathGenerator(startIndex, endIndex)

      for (node <- indexPath) {
        val nodeCoords: List[Int] = indexToGridLocation(node, mapHeight, mapWidth)
        outputPath = outputPath :+ new GridLocation(nodeCoords.head, nodeCoords.apply(1))
      }
    }
      else {
        outputPath = List(start)
      }
    //}
    println(outputPath)
    outputPath
  }

  def getVelocity(path: List[GridLocation], currentLocation: PhysicsVector): PhysicsVector = {
    //This will take the path that findPath found, then based on a current position determines the direction the player should move

    var outputVelocity: PhysicsVector = new PhysicsVector(0,0)

    if (path.nonEmpty) {
      val currentIndex: Int = path.indexOf(physicsVectorToGridLocation(currentLocation))
      val finalTilePhysicsVector = gridLocationToPhysicsVector(path.last)

      if (currentLocation.distance2d(gridLocationToPhysicsVector(path.last)) < 0.1) {
        outputVelocity = new PhysicsVector(0.0, 0.0)
      }

      else if ((Math.floor(currentLocation.x) == path.last.x) && (Math.floor(currentLocation.y) == path.last.y)) {
        //Velocity should be directed towards center of the tile
        val targetX: Double = finalTilePhysicsVector.x - currentLocation.x
        val targetY: Double = finalTilePhysicsVector.y - currentLocation.y

        outputVelocity = magnitudeNormalization(targetX, targetY)
      }



      else {
        val nextLocation: GridLocation = path.apply(currentIndex + 1)
        val nextPhysicsVector: PhysicsVector = gridLocationToPhysicsVector(nextLocation)
        val targetX: Double = nextPhysicsVector.x - currentLocation.x
        val targetY: Double = nextPhysicsVector.y - currentLocation.y
        outputVelocity = new PhysicsVector(targetX, targetY)

        outputVelocity = outputVelocity.normal2d()

        outputVelocity.x = outputVelocity.x * 5
        outputVelocity.y = outputVelocity.y * 5
        //println("Far from end")
      }
    }
    //println("velocity", outputVelocity)
    //println("currentLocation", currentLocation)
    outputVelocity
  }
}
