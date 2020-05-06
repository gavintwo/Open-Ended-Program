package server


import Overworld.GameMap

import scala.concurrent.duration._
import akka.actor.{Actor, ActorRef, ActorSystem, Cancellable, PoisonPill, Props}
import com.corundumstudio.socketio.listener.ConnectListener
import com.corundumstudio.socketio.{AckRequest, Configuration, SocketIOClient, SocketIOServer}
import play.api.libs.json.{JsValue, Json}
import player.{Enemy, Player, attackTarget, endEncounter, endEncounterDead,engageEnemy, gameOver, initialize, moveDown, moveLeft, moveRight, moveTo, moveUp, pause, playerMovement, printline, resume, shiftDownMessage, shiftLeftMessage, shiftRightMessage, shiftUpMessage, startEncounter, updateEnemyValues, updateValues, usePotion}

import scala.collection.mutable


class Server extends Actor {

  import playerSystem.dispatcher

  var locations: Map[SocketIOClient, PhysicsVector] = Map ()
  var healths: Map[SocketIOClient, Int] = Map ()
  var potions: Map[SocketIOClient, Int] = Map ()
  var armors: Map[SocketIOClient, Int] = Map ()
  var attacks: Map[SocketIOClient, Int] = Map ()
  var printlnMap: Map[SocketIOClient, mutable.Queue[String]] = Map()
  var pausedStates: Map[SocketIOClient, Boolean] = Map ()

  var socketToPlayerActor: Map[SocketIOClient, ActorRef] = Map ()
  var playerActorToSocket: Map[ActorRef, SocketIOClient] = Map ()

  var socketToEnemyActor: Map[SocketIOClient, ActorRef] = Map ()
  var enemyActorToSocket: Map[ActorRef, SocketIOClient] = Map()
  var enemyActorToScheduler: Map[ActorRef, Cancellable] = Map()

  var enemyHealths: Map[SocketIOClient, Int] = Map ()
  var enemyArmors: Map[SocketIOClient, Int] = Map ()
  var enemyAttacks: Map[SocketIOClient, Int] = Map ()

  var maps: Map[SocketIOClient, GameMap] = Map()

  val playerSystem: ActorSystem = ActorSystem()

  val config: Configuration = new Configuration {
    setHostname("localhost")
    setPort(8081)
  }

  val server: SocketIOServer = new SocketIOServer(config)

  server.addConnectListener(new ConnectionListener(this))

  server.addDisconnectListener(
    (socket: SocketIOClient) => {
      val playerRef: ActorRef = socketToPlayerActor(socket)
      if (socketToEnemyActor.contains(socket)) {
        val enemyRef = socketToEnemyActor(socket)
        enemyRef ! PoisonPill
        enemyActorToSocket -= enemyRef
        socketToEnemyActor -= socket
      }
      playerRef ! PoisonPill
      socketToPlayerActor -= socket
      playerActorToSocket -= playerRef
      locations -= socket
      healths -= socket
      potions -= socket
      armors -= socket
      attacks -= socket
      pausedStates -= socket
      printlnMap -= socket
      maps -= socket
      enemyHealths -= socket
      enemyArmors -= socket
      enemyAttacks -= socket


      println("A player has disconnected and their values have been removed")
    }
  )


  server.addEventListener("attack", classOf[Nothing],
    (socket: SocketIOClient, _: Nothing, _: AckRequest) => {
      //This will be used for encounters to deal damage to enemies
      val player: ActorRef = socketToPlayerActor(socket)
      player ! attackTarget
      //println("The server received a message  from the HTML to attack")
    }
  )


  server.addEventListener("left", classOf[Nothing],
    (socket: SocketIOClient, _: Nothing, _: AckRequest) => {
      //println("The server has received a message to move left")
      val movementPaused = pausedStates(socket)
      if (!movementPaused) {
        socketToPlayerActor(socket) ! moveLeft
      }
    }
  )

  server.addEventListener("right", classOf[Nothing],
    (socket: SocketIOClient, _: Nothing, _: AckRequest) => {
      //println("The server has received a message to move right")
      val movementPaused = pausedStates(socket)
      if (!movementPaused) {
        socketToPlayerActor(socket) ! moveRight
      }
    }
  )

  server.addEventListener("up", classOf[Nothing],
    (socket: SocketIOClient, _: Nothing, _: AckRequest) => {
      //println("The server has received a message to move up")
      val movementPaused = pausedStates(socket)
      if (!movementPaused) {
        socketToPlayerActor(socket) ! moveUp
      }
    }
  )

  server.addEventListener("down", classOf[Nothing],
    (socket: SocketIOClient, _: Nothing, _: AckRequest) => {
      //println("The server has received a message to move down")
      val movementPaused = pausedStates(socket)
      if (!movementPaused) {
        socketToPlayerActor(socket) ! moveDown
      }
    }
  )

  server.addEventListener("usePotion", classOf[Nothing],
    (socket:SocketIOClient, _: Nothing, _: AckRequest) => {
      socketToPlayerActor(socket) ! usePotion
    }
  )

  server.start()


  override def postStop(): Unit = {
    println("stopping server")
    server.stop()
  }

  def gameState(socket: SocketIOClient): String = {
      val socketGameState: Map[String, JsValue] = Map(
        "tiles" -> maps(socket).tilesJSON(),
        "location" -> Json.toJson(List(locations(socket).x, locations(socket).y)),
        "health" -> Json.toJson(healths(socket)),
        "potion" -> Json.toJson(potions(socket)),
        "armor" -> Json.toJson(armors(socket)),
        "attack" -> Json.toJson(attacks(socket)),
        "printlns" -> Json.toJson(printlnMap(socket)),
        "enemyHealth" -> Json.toJson(enemyHealths(socket)),
        "enemyAttack" -> Json.toJson(enemyAttacks(socket)),
        "enemyArmor" -> Json.toJson(enemyArmors(socket))


      )
      Json.stringify(Json.toJson(socketGameState))
  }


  override def receive: Receive = {

    case `endEncounter` =>
      //This will be sent by the enemy upon reaching 0 health
      val socket: SocketIOClient = enemyActorToSocket(sender)

      val scheduler: Cancellable = enemyActorToScheduler(sender)
      scheduler.cancel()

      sender ! PoisonPill

      socketToEnemyActor -= socket
      enemyActorToSocket -= sender

      pausedStates += (socket -> false)

    case `shiftUpMessage` =>
      val socket: SocketIOClient = playerActorToSocket(sender)
      val currentMap: GameMap = maps(socket)
      currentMap.shiftUp()
      val newLocation = new PhysicsVector(14.5,19.5)

      sender ! moveTo(newLocation)
      locations += socket -> newLocation


    case `shiftDownMessage` =>
      val socket: SocketIOClient = playerActorToSocket(sender)
      val currentMap: GameMap = maps(socket)
      currentMap.shiftDown()
      val newLocation = new PhysicsVector(14.5,0.5)

      sender ! moveTo(newLocation)
      locations += socket -> newLocation


    case `shiftLeftMessage` =>
      val socket: SocketIOClient = playerActorToSocket(sender)
      val currentMap: GameMap = maps(socket)
      currentMap.shiftLeft()
      val newLocation = new PhysicsVector(29.5,10.5)

      sender ! moveTo(newLocation)
      locations += socket -> newLocation


    case `shiftRightMessage` =>
      val socket: SocketIOClient = playerActorToSocket(sender)
      val currentMap: GameMap = maps(socket)
      currentMap.shiftRight()
      val newLocation = new PhysicsVector(0.5,10.5)

      sender ! moveTo(newLocation)
      locations += socket -> newLocation


    case Update =>
      //println("The server has received an update message")
      for ((socket, _) <- socketToPlayerActor) {
        socket.sendEvent("gameState", gameState(socket))

      }

    case received: startEncounter =>
      val tileType: String = received.tileType
      val socket: SocketIOClient = playerActorToSocket(sender)

      pausedStates += socket -> true

      //In here is where the type of enemy will be determined, I need to populate a few maps of tile to enemy stats/drops
      //Add a true/false as to whether the enemy is elite or not

      val enemyStats: Map[String, Any] = enemyTypes.getEnemyStats(tileType)
      val name: String = enemyStats("name").toString
      val introText: String = enemyStats("introText").toString
      val enemyAttack: Int = enemyStats("attack").toString.toInt
      val enemyArmor: Int = enemyStats("armor").toString.toInt
      val enemyHealth: Int = enemyStats("health").toString.toInt
      val enemyDrops: Map[String, String] = enemyStats("drops").asInstanceOf[Map[String,String]]


      val newEnemyActor: ActorRef = this.playerSystem.actorOf(Props(classOf[Enemy], name, introText, enemyAttack, enemyArmor, enemyHealth, enemyDrops, socket, this.self, sender))

      socketToEnemyActor += socket -> newEnemyActor
      enemyActorToSocket += newEnemyActor -> socket

      sender ! engageEnemy(newEnemyActor)

      //newEnemyActor ! attackTarget

      val cancellableScheduler: Cancellable = this.playerSystem.scheduler.schedule(0.milliseconds, 1000.milliseconds, newEnemyActor, attackTarget)
      enemyActorToScheduler += newEnemyActor -> cancellableScheduler

    case received: updateValues =>
      //println("server has successfully updated values")
      locations += received.socket -> received.location
      healths += received.socket -> received.health
      armors += received.socket -> received.armor
      potions += received.socket -> received.potions
      attacks += received.socket -> received.attack

    case received: updateEnemyValues =>
      //println("updatedEnemyValues")
      val socket: SocketIOClient = enemyActorToSocket(sender)
      enemyHealths += socket -> received.health
      enemyAttacks += socket -> received.attack
      enemyArmors += socket -> received.armor

    case received: printline =>
      val socket = received.socket
      val message: String = received.input
      val JSONmessage: String = Json.stringify(Json.toJson(message))
      socket.sendEvent("printline", JSONmessage)

    case `pause` =>
      val socket: SocketIOClient = playerActorToSocket(sender)
      pausedStates += (socket -> true)

    case `resume` =>
      val socket: SocketIOClient = playerActorToSocket(sender)
      pausedStates += (socket -> false)

    case `gameOver` =>
      val socket: SocketIOClient = playerActorToSocket(sender)
      //socketToPlayerActor(socket) ! Update
      pausedStates += (socket -> true)
      //Do something death worthy. Put something in the printline, maybe make the player fade away or something, idk

    case `endEncounterDead` =>
      val socket: SocketIOClient = enemyActorToSocket(sender)

      val scheduler: Cancellable = enemyActorToScheduler(sender)
      scheduler.cancel()

      sender ! PoisonPill

      socketToEnemyActor -= socket
      enemyActorToSocket -= sender
  }
}

class ConnectionListener(server: Server) extends ConnectListener {
  override def onConnect(socket: SocketIOClient): Unit = {

    println("A socket has being established")
    println("Populating values...")

    val newGameActor: ActorRef = server.playerSystem.actorOf(Props(classOf[Player]))

    server.pausedStates += (socket -> false)
    server.maps += socket -> GameMap()
    server.locations += socket -> new PhysicsVector(server.maps(socket).startingLocation.x + 0.5, server.maps(socket).startingLocation.y + 0.5)
    server.healths += socket -> 100
    server.potions += socket -> 1
    server.armors += socket -> 0
    server.attacks += socket -> 1
    server.printlnMap += socket -> mutable.Queue()
    server.enemyArmors += socket -> 0
    server.enemyAttacks += socket -> 0
    server.enemyHealths += socket -> 0
    server.socketToPlayerActor += (socket -> newGameActor)
    server.playerActorToSocket += (newGameActor -> socket)

    socket.sendEvent("initialize")

    newGameActor ! initialize(server.locations(socket),server.maps(socket), socket, server.self, server.healths(socket), server.potions(socket), server.armors(socket), server.attacks(socket), server.enemyHealths(socket), server.enemyAttacks(socket), server.enemyArmors(socket))
  }
}


object Server {

  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem()

    import actorSystem.dispatcher
    import scala.concurrent.duration._

    val server = actorSystem.actorOf(Props(classOf[Server]))

    actorSystem.scheduler.schedule(0.milliseconds, 33.milliseconds, server, Update)
  }

}