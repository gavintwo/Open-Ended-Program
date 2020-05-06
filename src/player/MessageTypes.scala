package player

import Overworld.GameMap
import akka.actor.ActorRef
import com.corundumstudio.socketio.SocketIOClient
import server.PhysicsVector


//Sent to server
case class updateValues(location: PhysicsVector, health: Int, potions: Int, armor: Int, attack: Int, socket: SocketIOClient)
case object update
case class GameState(state: String)
case class printline(input: String, socket: SocketIOClient)
case class startEncounter(tileType: String)
case object endEncounter
case class updateEnemyValues(health: Int, attack: Int, armor: Int)

case object shiftUpMessage
case object shiftDownMessage
case object shiftLeftMessage
case object shiftRightMessage


//Sent to player
case object moveLeft
case object moveRight
case object moveUp
case object moveDown
case class initialize(currentLocation: PhysicsVector, map: GameMap, socket: SocketIOClient, server: ActorRef, health: Int, potions: Int, armor: Int, attack: Int, enemyHealth: Int, enemyAttack: Int, enemyArmor: Int)
case class engageEnemy(enemyRef: ActorRef)
case object disengageEnemy

case class obtained (itemObtained: String) //~~

case class takeDamage (damage: Int)
case class heal (healthRegained: Int) //~~
case object usePotion

case object pause
case object resume
case object gameOver
case object endEncounterDead

case class moveTo(location: PhysicsVector)

//Sent to enemy
case object attackTarget
case object dropItem


