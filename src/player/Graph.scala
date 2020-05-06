package player

import scala.collection.mutable


class Graph [A]{

  var adjacencyList: Map[Int, List[Int]] = Map()
  var nodes: Map[Int, A] = Map()


  def addNode(index: Int, a: A): Unit = {
    nodes += index -> a
    adjacencyList += index -> List()
  }

  def addEdge(index1: Int, index2: Int): Unit = {
    adjacencyList += index1 -> (index2 :: adjacencyList(index1))
    adjacencyList += index2 -> (index1 :: adjacencyList(index2))
  }

  def connected(index1: Int, index2: Int): Boolean = {
    adjacencyList(index1).contains(index2)
  }

  def isPath(path: List[Int]): Boolean = {
    // initialize prev to an invalid node id
    var prev = nodes.keys.min - 1
    var valid = true
    for (index <- path) {
      if (prev != nodes.keys.min - 1) {
        if (!connected(prev, index)) {
          valid = false
        }
      }
      prev = index
    }
    valid
  }

  def areConnected(index1: Int, index2: Int): Boolean = {
    // TODO: Return true if the two nodes are connected by a path, false otherwise
    var connected: Boolean = false

    val startID: Int = index1

    var explored: Set[Int] = Set(startID)

    var distance: Map[Int, Int] = Map()
    distance += startID -> -1

    val toExplore: mutable.Queue[Int] = new mutable.Queue()
    toExplore.enqueue(startID)

    while (toExplore.nonEmpty) {
      val nodeToExplore = toExplore.dequeue()
      for (node <- this.adjacencyList(nodeToExplore)) {
        if(!explored.contains(node)){
          //println("exploring: " + this.nodes(node))
          distance += node -> nodeToExplore
          toExplore.enqueue(node)
          explored = explored + node
        }
      }
    }

    if (explored.contains(index2)) {
      connected = true
    }
    connected
  }

  def pathGenerator(index1: Int, index2: Int): List[Int] = {
    // TODO: Return the distance between index1 and index2 in this graph
    // You may assume that the two nodes are connected
    var outputPath: List[Int] = List()
    //Make a new map that maps the node to the node that found it
    //if (areConnected(index1,index2)) {
      var distance: Map[Int, Int] = Map()
      distance += index1 -> 0

      var explored: Set[Int] = Set(index1)

      var discoveryMap: Map[Int, Int] = Map()

      val toExplore: mutable.Queue[Int] = new mutable.Queue()
      toExplore.enqueue(index1)

      while (toExplore.nonEmpty) {
        //May be time-out cause
        val nodeToExplore = toExplore.dequeue()
        for (node <- this.adjacencyList(nodeToExplore))
          if (!explored.contains(node)) {
            discoveryMap += node -> nodeToExplore
            toExplore.enqueue(node)
            //println(toExplore)
            explored = explored + node
          }
      }

      //println(adjacencyList)

      if (explored.contains(index2)) {

        var currentNode: Int = index2
        while (index1 != currentNode) {
          //May cause time-out
          outputPath = currentNode :: outputPath
          currentNode = discoveryMap(currentNode)
        }
        outputPath = index1 :: outputPath
      }
      else {
        outputPath = List(index1)
    }
    outputPath
  }
}
