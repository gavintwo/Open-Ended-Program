package player

import akka.actor.{Actor, ActorRef}
import com.corundumstudio.socketio.SocketIOClient


class Enemy (var name: String, var introText: String, var attack: Int, var AC: Int, var health: Int, var drops: Map[String, String], socket: SocketIOClient, server: ActorRef, player: ActorRef) extends Actor {

  var dead = false


  server ! printline(introText, socket)
  server ! updateEnemyValues(health, attack, AC)


  def randRarity(): String = {
    val randomNumber = Math.random()
    if (0 <= randomNumber && randomNumber < 0.5) {
      "common"
    }
    else if (0.5 <= randomNumber && randomNumber < 0.75) {
      "uncommon"
    }
    else if (0.75 <= randomNumber && randomNumber < 0.875) {
      "rare"
    }
    else {
      "legendary"
    }
  }

  def receive: Receive = {
    case `attackTarget` =>
      //println("Enemy attacked")
      if (!dead) {
        //val timeToWait: Double = (Math.random() + 1) * 1000

        //Thread.sleep(timeToWait.toInt)
        player ! takeDamage(this.attack)

        //self ! attackTarget
      }

    case received: `takeDamage` =>
      println("Enemy took damage")
      val playerDamageGiven: Int = received.damage
      var reducedDamage: Int = playerDamageGiven - AC
      if (!dead) {
        if (reducedDamage <= 0) {
          reducedDamage = 1
        }
        health = health - reducedDamage
        if (health <= 0) {
          health = 0
          server ! updateEnemyValues(0,0,0)
          dead = true
          server ! endEncounter
          player ! disengageEnemy
          server ! printline("The " + name + " falls to the ground with a thud", socket)
          self ! dropItem

        }
        else {
          server ! updateEnemyValues(health, attack, AC)
        }
      }

      //Values must be updated

    case `dropItem` =>
      val rarity = randRarity()
      val item = drops(rarity)
      player ! obtained (item)

    case `gameOver` =>
      dead = true
      player ! disengageEnemy
      server ! endEncounterDead
  }


}
