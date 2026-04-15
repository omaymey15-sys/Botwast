package com.neogame.console2d.engine

import android.content.Context
import android.graphics.Canvas
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

object GameEngine {
    
    var currentGame: JSONObject? = null
    var currentScene: String = ""
    var isRunning = false
    var playerX = 100f
    var playerY = 100f
    var playerWidth = 50f
    var playerHeight = 50f
    
    private val sceneManager = SceneManager()
    private val scriptEngine = ScriptEngine()
    private val gameRenderer = GameRenderer()

    fun loadGame(context: Context, gameName: String) {
        try {
            val gameJson = loadGameFromAssets(context, gameName)
            currentGame = gameJson
            currentScene = gameJson.getString("start_scene")
            isRunning = true
            playerX = 100f
            playerY = 100f
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onInput(action: String, dx: Float = 0f, dy: Float = 0f) {
        if (!isRunning) return

        when (action) {
            "MOVE_LEFT" -> playerX -= 10
            "MOVE_RIGHT" -> playerX += 10
            "MOVE_UP" -> playerY -= 10
            "MOVE_DOWN" -> playerY += 10
            "JOYSTICK" -> {
                playerX += dx * 2
                playerY += dy * 2
            }
        }

        // Exécuter les scripts associés
        val scene = getCurrentSceneData()
        scene?.let {
            val inputs = it.optJSONObject("inputs")
            if (inputs?.has(action) == true) {
                scriptEngine.executeScript(inputs.getString(action), this)
            }
        }
    }

    fun getCurrentSceneData(): JSONObject? {
        return currentGame?.optJSONObject("scenes")?.optJSONObject(currentScene)
    }

    fun changeScene(sceneName: String) {
        currentScene = sceneName
    }

    fun render(canvas: Canvas) {
        gameRenderer.render(canvas, this)
    }

    fun update(deltaTime: Long) {
        // Logique de mise à jour du jeu
    }

    fun stop() {
        isRunning = false
    }

    private fun loadGameFromAssets(context: Context, gameName: String): JSONObject {
        return try {
            val inputStream = context.assets.open("games/$gameName.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.readText()
            JSONObject(jsonString)
        } catch (e: Exception) {
            // Si pas trouvé dans assets, charger depuis Download
            loadGameFromDownloads(context, gameName)
        }
    }

    private fun loadGameFromDownloads(context: Context, gameName: String): JSONObject {
        val downloadsDir = context.getExternalFilesDir(null)
        val gameFile = java.io.File(downloadsDir, "games/$gameName.json")
        val jsonString = gameFile.readText()
        return JSONObject(jsonString)
    }
}