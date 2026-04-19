package com.neogame.psp.emulator

import com.google.gson.JsonObject
import com.neogame.psp.utils.Constants
import com.neogame.psp.utils.Logger
import kotlin.math.sqrt

/**
 * Moteur de jeu universel 2D/3D/4D
 * Gère la logique et l'état du jeu
 */
class GameEngine(private val config: JsonObject) {

    data class GameObject(
        val id: String,
        val type: String,
        var x: Float = 0f,
        var y: Float = 0f,
        var z: Float = 0f,
        var w: Float = 0f,
        var velocityX: Float = 0f,
        var velocityY: Float = 0f,
        var velocityZ: Float = 0f,
        var rotationX: Float = 0f,
        var rotationY: Float = 0f,
        var rotationZ: Float = 0f,
        var width: Float = 32f,
        var height: Float = 32f,
        var depth: Float = 32f,
        val properties: MutableMap<String, Any> = mutableMapOf()
    )

    private val gameObjects = mutableListOf<GameObject>()
    private val physics = PhysicsEngine()
    private val gameState = GameState()

    private var gameTime = 0f
    private val gameType = config.get("metadata")?.asJsonObject?.get("type")?.asString ?: Constants.GAME_TYPE_2D

    init {
        parseConfig()
        Logger.i("GameEngine initialisé pour $gameType")
    }

    private fun parseConfig() {
        try {
            val objectsArray = config.getAsJsonArray("objects")
            objectsArray?.forEach { obj ->
                val jsonObj = obj.asJsonObject
                val gameObj = GameObject(
                    id = jsonObj.get("id")?.asString ?: "",
                    type = jsonObj.get("type")?.asString ?: "",
                    x = jsonObj.get("x")?.asFloat ?: 0f,
                    y = jsonObj.get("y")?.asFloat ?: 0f,
                    z = jsonObj.get("z")?.asFloat ?: 0f,
                    width = jsonObj.get("width")?.asFloat ?: 32f,
                    height = jsonObj.get("height")?.asFloat ?: 32f
                )
                gameObjects.add(gameObj)
            }
            Logger.d("${gameObjects.size} objets chargés")
        } catch (e: Exception) {
            Logger.e("Erreur parse: ${e.message}")
        }
    }

    fun update(deltaTime: Float) {
        gameTime += deltaTime
        gameObjects.forEach { obj ->
            when (obj.type) {
                Constants.OBJECT_TYPE_PLAYER -> updatePlayer(obj, deltaTime)
                Constants.OBJECT_TYPE_ENEMY -> updateEnemy(obj, deltaTime)
            }
        }
        physics.update(gameObjects, deltaTime)
    }

    private fun updatePlayer(obj: GameObject, deltaTime: Float) {
        val gravity = 9.8f
        obj.velocityY += gravity * deltaTime
        obj.y += obj.velocityY * deltaTime
        obj.x += obj.velocityX * deltaTime
        obj.z += obj.velocityZ * deltaTime
    }

    private fun updateEnemy(obj: GameObject, deltaTime: Float) {
        val speed = (obj.properties["speed"] as? Number)?.toFloat() ?: 2f
        obj.x += speed * deltaTime
    }

    fun handleInput(action: String, value: Float) {
        val player = gameObjects.find { it.type == Constants.OBJECT_TYPE_PLAYER } ?: return
        when (action) {
            "move_left" -> player.velocityX = -5f
            "move_right" -> player.velocityX = 5f
            "jump" -> player.velocityY = -15f
        }
    }

    fun getGameObjects(): List<GameObject> = gameObjects.toList()
    fun getGameType(): String = gameType
    fun stop() {
        gameObjects.clear()
        Logger.i("Moteur arrêté")
    }
}