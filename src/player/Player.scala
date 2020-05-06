package player

import Overworld.GameMap
import akka.actor.{Actor, ActorRef}
import com.corundumstudio.socketio.SocketIOClient
import server.{MapTile, PhysicsVector}

class Player extends Actor {

  var initialized: Boolean = false
  var enemy: ActorRef = _
  var engaged: Boolean = false

  var currentLocation: PhysicsVector = new PhysicsVector(0,0)
  var map: GameMap = _
  var socket: SocketIOClient = _
  var server: ActorRef = _
  var paused: Boolean = false
  var health: Int = 100
  var healthPotionsObtained: Int = 0
  var armor: Int = 0
  var attack: Int = 1

  var itemsObtainedMap: Map[String, Boolean] = Map(
    "woodenSwordObtained" -> false,
    "copperSwordObtained" -> false,
    "bronzeSwordObtained" -> false,
    "ironSwordObtained" -> false,
    "steelSwordObtained" -> false,
    "legendarySwordObtained" -> false,
    "woodenShieldObtained" -> false,
    "ironShieldObtained" -> false,
    "copperShieldObtained" -> false,
    "bronzeShieldObtained" -> false,
    "steelShieldObtained" -> false,
    "reinforcedShieldObtained" -> false,
    "leatherArmorObtained" -> false,
    "woodenArmorObtained" -> false,
    "copperArmorObtained" -> false,
    "bronzeArmorObtained" -> false,
    "ironArmorObtained" -> false,
    "steelArmorObtained" -> false,
    "reinforcedArmorObtained" -> false,
    "sandShoesObtained" -> false,
    "flippersObtained" -> false,
    "obsidianCrystalObtained" -> false,
    "iceCrystalObtained" -> false
  )

  val armorStatMap: Map[String,Int] = Map (
    "woodenShieldObtained" -> 1,
    "copperShieldObtained" -> 4,
    "bronzeShieldObtained" -> 6,
    "ironShieldObtained" -> 8,
    "steelShieldObtained" -> 10,
    "reinforcedShieldObtained" -> 15,
    "leatherArmorObtained" -> 2,
    "woodenArmorObtained" -> 6,
    "copperArmorObtained" -> 8,
    "bronzeArmorObtained" -> 10,
    "ironArmorObtained" -> 15,
    "steelArmorObtained" -> 18,
    "reinforcedArmorObtained" -> 25,
  )
  val weaponStatMap: Map[String, Int] = Map (
    "woodenSwordObtained" -> 2,
    "copperSwordObtained" -> 6,
    "bronzeSwordObtained" -> 15,
    "ironSwordObtained" -> 25,
    "steelSwordObtained" -> 35,
    "legendarySwordObtained" -> 50,
  )

  //False -> the item begins with a consonant
  //True -> the item begins with a vowel
  var itemIdToName: Map[String, (String, Boolean)] = Map(
    "woodenSwordObtained" -> ("Wooden Sword", false),
    "copperSwordObtained" -> ("Copper Sword", false),
    "bronzeSwordObtained" -> ("Bronze Sword", false),
    "ironSwordObtained" -> ("Iron Sword", true),
    "steelSwordObtained" -> ("Steel Sword", false),
    "legendarySwordObtained" -> ("Legendary Sword", false),
    "woodenShieldObtained" -> ("Wooden Shield", false),
    "ironShieldObtained" -> ("Iron Shield", true),
    "copperShieldObtained" -> ("Copper Shield", false),
    "bronzeShieldObtained" -> ("Bronze Shield", false),
    "steelShieldObtained" -> ("Steel Shield", false),
    "reinforcedShieldObtained" -> ("Reinforced Shield", false),
    "leatherArmorObtained" -> ("Suit of Leather Armor", false),
    "woodenArmorObtained" -> ("Suit of Wooden Armor", false),
    "copperArmorObtained" -> ("Suit of Copper Armor", false),
    "bronzeArmorObtained" -> ("Suit of Bronze Armor", false),
    "ironArmorObtained" -> ("Suit of Iron Armor", false),
    "steelArmorObtained" -> ("Suit of Steel Armor", false),
    "reinforcedArmorObtained" -> ("Suit of Reinforced Armor", false),
    "sandShoesObtained" -> ("Pair of Sand Shoes", false),
    "flippersObtained" -> ("Pair of Flippers", false),
    "obsidianCrystalObtained" -> ("Obsidian Crystal", true),
    "iceCrystalObtained" -> ("Ice Crystal", true)
  )

  //Several items can be obtained...

  //woodenSword (1 damage)
  //copperSword (2 damage)
  //bronzeSword (4 damage)
  //ironSword (6 damage)
  //steelSword (8 damage)
  //katana (10 damage)

  //woodenShield (1 AC)
  //copperShield (2 AC)
  //bronzeShield (3 AC)
  //ironShield   (4 AC)
  //steelShield  (5 AC)
  //reinforcedSteelShield (6 AC)

  //leatherArmor (1 AC)
  //woodenArmor (2 AC)
  //copperArmor (3 AC)
  //ironArmor (4 AC)
  //steelArmor (5 AC)
  //reinforcedSteelArmor (6 AC)

  //Some overworld-traversing items:

  //Sand-Shoes (Removes slow effect from sand)
  //Flippers (Allows movement through water)
  //Obsidian Crystal (Flame resist)
  //Ice Crystal (Ice resist)

  def isPassable(inputTile:MapTile): Boolean = {
    if (inputTile.tileType == "grass") {
      true
    }
    else if (inputTile.tileType == "treasure chest") {
      true
    }
    else if (inputTile.tileType == "wall") {
      false
    }
    else if (inputTile.tileType == "sand") {
      true
    }
    else if (inputTile.tileType == "path") {
      true
    }
    else if (inputTile.tileType == "ice") {
      true
    }
    else if (inputTile.tileType == "lava") {
      if (!itemsObtainedMap("obsidianCrystalObtained")) {
        self ! takeDamage(10)
      }
      false
    }
    else if (inputTile.tileType == "snow") {
      if (!itemsObtainedMap("iceCrystalObtained")) {
        self ! takeDamage(2)
      }
      true
    }
    else if (inputTile.tileType == "hotsand") {
      if (!itemsObtainedMap("obsidianCrystalObtained")) {
        self ! takeDamage(2)
      }
      true
    }
    else if ((inputTile.tileType == "water") && itemsObtainedMap("flippersObtained")) {
      true
    }
    else {
      false
    }
  }

  def calculateAC(): Int = {
    var bestShield: Int = 0
    var bestArmor: Int = 0
    if (itemsObtainedMap("woodenShieldObtained")) {
      bestShield = armorStatMap("woodenShieldObtained")
    }
    if (itemsObtainedMap("copperShieldObtained")) {
      bestShield = armorStatMap("copperShieldObtained")
    }
    if (itemsObtainedMap("bronzeShieldObtained")) {
      bestShield = armorStatMap("bronzeShieldObtained")
    }
    if (itemsObtainedMap("ironShieldObtained")) {
      bestShield = armorStatMap("ironShieldObtained")
    }
    if (itemsObtainedMap("steelShieldObtained")) {
      bestShield = armorStatMap("steelShieldObtained")
    }
    if (itemsObtainedMap("reinforcedShieldObtained")) {
      bestShield = armorStatMap("reinforcedShieldObtained")
    }
    if (itemsObtainedMap("leatherArmorObtained")) {
      bestArmor = armorStatMap("leatherArmorObtained")
    }
    if (itemsObtainedMap("woodenArmorObtained")) {
      bestArmor = armorStatMap("woodenArmorObtained")
    }
    if (itemsObtainedMap("copperArmorObtained")) {
      bestArmor = armorStatMap("copperArmorObtained")
    }
    if (itemsObtainedMap("ironArmorObtained")) {
      bestArmor = armorStatMap("ironArmorObtained")
    }
    if (itemsObtainedMap("bronzeArmorObtained")) {
      bestArmor = armorStatMap("bronzeArmorObtained")
    }
    if (itemsObtainedMap("steelArmorObtained")) {
      bestArmor = armorStatMap("steelArmorObtained")
    }
    if (itemsObtainedMap("reinforcedArmorObtained")) {
      bestArmor = armorStatMap("reinforcedArmorObtained")
    }


    val outputAC = bestShield + bestArmor
    outputAC
  }

  def calculateAttack(): Int = {
    var bestWeapon: Int = 1

    if (itemsObtainedMap("woodenSwordObtained")){
      bestWeapon = weaponStatMap("woodenSwordObtained")
    }
    if (itemsObtainedMap("copperSwordObtained")){
      bestWeapon = weaponStatMap("copperSwordObtained")
    }
    if (itemsObtainedMap("bronzeSwordObtained")){
      bestWeapon = weaponStatMap("bronzeSwordObtained")
    }
    if (itemsObtainedMap("ironSwordObtained")){
      bestWeapon = weaponStatMap("ironSwordObtained")
    }
    if (itemsObtainedMap("steelSwordObtained")){
      bestWeapon = weaponStatMap("steelSwordObtained")
    }
    if (itemsObtainedMap("legendarySwordObtained")){
      bestWeapon = weaponStatMap("legendarySwordObtained")
    }
    bestWeapon
  }

  def move (direction: String): Unit = {
    val directionMessages: Map[String, Product with Serializable] = Map(
      "down" -> moveDown,
      "up" -> moveUp,
      "left" -> moveLeft,
      "right" -> moveRight
    )

    var possibleMove: PhysicsVector = new PhysicsVector(currentLocation.x, currentLocation.y)

    if (direction == "left") {
      possibleMove = new PhysicsVector(currentLocation.x - 1, currentLocation.y)
    }
    else if (direction == "right") {
      possibleMove = new PhysicsVector(currentLocation.x + 1, currentLocation.y)
    }
    else if (direction == "up") {
      possibleMove = new PhysicsVector(currentLocation.x, currentLocation.y - 1)
    }
    else if (direction == "down") {
      possibleMove = new PhysicsVector(currentLocation.x, currentLocation.y + 1)
    }

    if ((0 < possibleMove.x && possibleMove.x < 30) && (0 < possibleMove.y && possibleMove.y < 20)) {
      val tileType: MapTile = map.physicsVectorToMapTile(possibleMove)
      if (isPassable(tileType)) {
        currentLocation = possibleMove
        server ! updateValues(currentLocation, health, healthPotionsObtained, armor, attack, socket)
        server ! pause
        Thread.sleep(100)
        server! resume

        if (tileType.tileType == "treasure chest") {
          //println("You are over a treasure chest")
          //println(currentLocation.toString, map.state.stateToString(), itemsObtainedMap("iceCrystalObtained"))
          if (currentLocation.toString == new PhysicsVector(13.5,1.5).toString && map.state.stateToString() == "upState" && !itemsObtainedMap("iceCrystalObtained")) {
            //Water area gives ice crystal
            self ! obtained("iceCrystalObtained")
          }
          if (currentLocation.toString == new PhysicsVector(24.5,15.5).toString && map.state.stateToString() == "downState" && !itemsObtainedMap("obsidianCrystalObtained")) {
            //Snow area give lava crystal
            self ! obtained("obsidianCrystalObtained")
          }
          if (currentLocation.toString == new PhysicsVector(3.5,18.5).toString && map.state.stateToString() == "leftState" && !itemsObtainedMap("reinforcedShieldObtained") && !itemsObtainedMap("legendarySwordObtained") && !itemsObtainedMap("reinforcedArmorObtained")) {
            //lava area has good gear
            self ! obtained("reinforcedShieldObtained")
            self ! obtained("legendarySwordObtained")
            self ! obtained("reinforcedArmorObtained")
          }
          if (currentLocation.toString == new PhysicsVector(24.5,17.5).toString && map.state.stateToString() == "rightState" && !itemsObtainedMap("flippersObtained")) {
            //sand area has flipper
            self ! obtained("flippersObtained")
          }
          if (currentLocation.toString == new PhysicsVector(25.5,17.5).toString && map.state.stateToString() == "mainState" && !itemsObtainedMap("sandShoesObtained")) {
            self ! obtained("sandShoesObtained")
          }
        }

        if (tileType.tileType == "ice") {
          server ! pause
          Thread.sleep(100)
          self ! directionMessages(direction)
          server ! resume
        }

        else {
          if (tileType.tileType == "sand" && !itemsObtainedMap("sandShoesObtained")) {
            server ! pause
            Thread.sleep(400)
            server ! resume
          }
          if (Math.random() > 0.9 && tileType.tileType != "path" && tileType.tileType != "treasure chest") {
            //There is a 10% chance that an encounter will start, as long as it is not an ice tile
            //The movement is completed, and will be initialized
            //println("An encounter has started!")
            server ! startEncounter(tileType.tileType)
          }
        }
      }
    }
    else {
      println(possibleMove.x, possibleMove.y)
      if (direction == "up") {
        server ! shiftUpMessage
      }
      else if (direction == "down") {
        server ! shiftDownMessage
      }
      else if (direction == "left") {
        server ! shiftLeftMessage
      }
      else if (direction == "right") {
        server ! shiftRightMessage
      }
    }
  }

  def receive: Receive = {
    case received: moveTo =>
      val newLocation: PhysicsVector = received.location
      currentLocation = newLocation


    case received: initialize =>
      initialized = true
      currentLocation = received.currentLocation
      map = received.map
      socket = received.socket
      server = received.server
      health = received.health
      healthPotionsObtained = received.potions
      armor = calculateAC()
      attack = calculateAttack()

      println("Player location and map initialized")



    case `update` =>
      //println("I am told to update")
      if (initialized) {
        armor = calculateAC()
        attack = calculateAttack()
        server ! updateValues(currentLocation, health, healthPotionsObtained, armor, attack, socket)
        //println("player values updated")
      }

    case `moveLeft` =>
      //println("I am told to move left")
      move("left")

    case `moveRight` =>
      //println("I am told to move right")
      move("right")


    case `moveUp` =>
      //println("I am told to move up")
      move("up")

    case `moveDown` =>
      //println("I am told to move down")
     move("down")

    case `usePotion` =>
      if (healthPotionsObtained > 0) {
        health = health + 25
        if (health > 100) {
          health = 100
        }
        healthPotionsObtained -= 1
        server ! printline("You healed for 25 health", socket)
        server ! updateValues(currentLocation, health, healthPotionsObtained, armor, attack, socket)
      }

    case received: obtained =>
      val itemObtained: String = received.itemObtained
      if (itemObtained == "potion") {
        healthPotionsObtained += 1
        if (healthPotionsObtained > 3) {
          healthPotionsObtained = 3
        }
        server ! printline("You obtained a health potion", socket)
      }
      else {
        itemsObtainedMap += itemObtained -> true
        armor = calculateAC()
        attack = calculateAttack()
        if (itemIdToName(itemObtained)._2) {
          server ! printline("You found an " + itemIdToName(itemObtained)._1, socket)
        }
        else {
          server ! printline("You found a " + itemIdToName(itemObtained)._1, socket)
        }
        if (itemObtained == "sandShoesObtained") {
          server ! printline("These shoes seem like they'd be excellent in braving the harsh sands of the desert...", socket)
        }
        if (itemObtained == "flippersObtained") {
          server ! printline("These flippers should help in traversing water...", socket)
        }
        if (itemObtained == "obsidianCrystalObtained") {
          server ! printline("The crystal seems indestructible. The air around you seems to get colder, making you feel impervious to heat...", socket)
        }
        if (itemObtained == "iceCrystalObtained") {
          server ! printline("The crystal gives you a feeling of warmth in even the harshest cold...", socket)
        }
      }

      server ! updateValues(currentLocation, health, healthPotionsObtained, armor, attack, socket)

    case received: engageEnemy =>
      //println("Enemy has been engaged")
      engaged = true
      enemy = received.enemyRef

    case `disengageEnemy` =>
      //println("Enemy has been disengaged")
      engaged = false
      enemy = null

    case received: takeDamage =>
      val damageTaken = received.damage
      val AC: Int = calculateAC()
      var reducedDamage: Int = damageTaken - AC
      if (reducedDamage <= 0) {
        reducedDamage = 1
      }
      val outputHealth = health - reducedDamage
      health = outputHealth
      server ! updateValues(currentLocation, health, healthPotionsObtained, armor, attack, socket)
      //println(health)
      if (outputHealth > 0) {
        //server ! currentHealth(outputHealth, socket)
        //server ! printline("You took " + damageTaken.toString + " damage!", socket)
      }
      else {
        health = 0
        //server ! currentHealth(outputHealth, socket)
        server ! printline("As the world fades around you, you take your last breath...", socket)
        if (engaged) {
          enemy ! gameOver
        }
        server ! gameOver
      }

    case `attackTarget` =>
      println("I am told to attack")
      if (engaged) {
        //println("Player sent message to attack", cooldown)
        enemy ! takeDamage(this.attack)

      }
  }
}
