package server

object enemyTypes {
  //Must add types of enemies that can appear in the certain map types
  //Must add stats for all enemies, which are...
    //Name
    //Introtext
    //Attack
    //Armor
    //Health
    //Drops (map[String,String])
      //"common"
      //"uncommon"
      //"rare"
      //"legendary"

  val tileToEnemyTypes: Map[String, String] = Map (
    "grass" -> "wolf",
    "sand" -> "sandWorm",
    "hotsand" -> "hellHound",
    "water" -> "seaSerpent",
    "snow" -> "iceGolem",
  )

  val enemyDrops: Map[String, Map[String,String]] = Map (
    "wolf" -> Map("common" -> "potion", "uncommon" -> "woodenSwordObtained", "rare" -> "woodenShieldObtained", "legendary" -> "woodenArmorObtained"),
    "sandWorm" -> Map("common" -> "potion", "uncommon" -> "copperSwordObtained", "rare" -> "copperShieldObtained", "legendary" -> "copperArmorObtained"),
    "seaSerpent" -> Map("common" -> "potion", "uncommon" -> "bronzeSwordObtained", "rare" -> "bronzeShieldObtained", "legendary" -> "bronzeArmorObtained"),
    "iceGolem" -> Map("common" -> "potion", "uncommon" -> "ironSwordObtained", "rare" -> "ironShieldObtained", "legendary" -> "ironArmorObtained"),
    "hellHound" -> Map("common" -> "potion", "uncommon" -> "steelSwordObtained", "rare" -> "steelShieldObtained", "legendary" -> "steelArmorObtained")
  )

  val enemyStats: Map[String, Map[String, Any]] = Map (
    "wolf" -> Map("name" -> "Wolf", "introText" -> "A wild wolf blocks you path!", "attack" -> 2, "armor" -> 0, "health" -> 5, "drops" -> enemyDrops("wolf")),
    "sandWorm" -> Map("name" -> "Sand Worm", "introText" -> "A Sand Worm bursts from the ground below you!", "attack" -> 5, "armor" -> 5, "health" -> 25, "drops" -> enemyDrops("sandWorm")),
    "seaSerpent" -> Map("name" -> "Sea Serpent", "introText" -> "A Sea Serpent explodes out from the water!", "attack" -> 10, "armor" -> 10, "health" -> 50, "drops" -> enemyDrops("seaSerpent")),
    "iceGolem" -> Map("name" -> "Ice Golem", "introText" -> "An Ice Golem awakens to fight you!", "attack" -> 20, "armor" -> 20, "health" -> 100, "drops" -> enemyDrops("iceGolem")),
    "hellHound" -> Map("name" -> "Hell Hound", "introText" -> "A Hell Hound manifests itself in a fiery blast!", "attack" -> 30, "armor" -> 30, "health" -> 200, "drops" -> enemyDrops("hellHound")),
  )

  def getEnemyStats(tileType: String): Map[String, Any] = {
    val enemyID: String = tileToEnemyTypes(tileType)
    val stats: Map[String,Any] = enemyStats(enemyID)
    stats
  }


}
