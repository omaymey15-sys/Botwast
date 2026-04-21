package com.neogame.psp

import com.google.gson.JsonObject

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
        var width: Float = 32f,
        var height: Float = 32f,
        var depth: Float = 32f,
        val color: Int = 0xFF88FF88.toInt()
    )

    private val gameObjects = mutableListOf<GameObject>()
    private var gameTime = 0f
    private val gravity = 9.8f

    init {
        parseConfig()
    }

    private fun parseConfig() {
        try {
            val objectsArray = config.getAsJsonArray("objects") ?: return
            objectsArray.forEach { obj ->
                val jsonObj = obj.asJsonObject
                val gameObj = GameObject(
                    id = jsonObj.get("id")?.asString ?: "",
                    type = jsonObj.get("type")?.asString ?: "",
                    x = jsonObj.get("x")?.asFloat ?: 0f,
                    y = jsonObj.get("y")?.asFloat ?: 0f,
                    z = jsonObj.get("z")?.asFloat ?: 0f,
                    w = jsonObj.get("w")?.asFloat ?: 0f,
                    width = jsonObj.get("width")?.asFloat ?: 32f,
                    height = jsonObj.get("height")?.asFloat ?: 32f,
                    depth = jsonObj.get("depth")?.asFloat ?: 32f
                )
                gameObjects.add(gameObj)
            }
            Logger.i("Loaded ${gameObjects.size} objects")
        } catch (e: Exception) {
            Logger.e("Parse error: ${e.message}")
        }
    }

    fun update(deltaTime: Float) {
        gameTime += deltaTime
        gameObjects.forEach { obj ->
            when (obj.type) {
                "player" -> {
                    obj.velocityY += gravity * deltaTime
                    obj.y += obj.velocityY * deltaTime
                    obj.x += obj.velocityX * deltaTime
                    if (obj.y > 2000) {
                        obj.y = 0f
                        obj.velocityY = 0f
                    }
                }
                "enemy" -> {
                    obj.x += 2f * deltaTime * 60
                    if (obj.x > 1080) obj.x = 0f
                }
            }
        }
    }

    fun handleInput(action: String, value: Float) {
        val player = gameObjects.find { it.type == "player" } ?: return
        when (action) {
            "LEFT" -> player.velocityX = -5f
            "RIGHT" -> player.velocityX = 5f
            "UP" -> player.velocityY = -15f
            "DOWN" -> player.velocityY = 5f
        }
    }

    fun getGameObjects(): List<GameObject> = gameObjects.toList()
    fun stop() {
        gameObjects.clear()
    }
}