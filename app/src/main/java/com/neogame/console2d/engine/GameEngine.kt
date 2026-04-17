package com.neogame.console2d.engine

import android.content.Context
import android.graphics.Canvas
import org.json.JSONObject
import com.neogame.console2d.utils.Logger
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
    var playerVelX = 0f
    var playerVelY = 0f
    
    private val sceneManager = SceneManager()
    private val scriptEngine = ScriptEngine()
    private val gameRenderer = GameRenderer()

    fun loadGame(context: Context, gameName: String) {
        try {
            Logger.d("Loading game: $gameName")
            val gameJson = loadGameFromAssets(context, gameName)
            currentGame = gameJson
            currentScene = gameJson.getString("start_scene")
            isRunning = true
            playerX = 100f
            playerY = 100f
            playerVelX = 0f
            playerVelY = 0f
            Logger.d("Game loaded successfully: ${gameJson.getString("name")}")
        } catch (e: Exception) {
            Logger.e("Error loading game", e)
            isRunning = false
        }
    }

    fun onInput(action: String, dx: Float = 0f, dy: Float = 0f) {
        if (!isRunning) return

        when (action) {
            "DPAD_LEFT" -> playerX = (playerX - 15).coerceAtLeast(0f)
            "DPAD_RIGHT" -> playerX = (playerX + 15).coerceAtMost(800f)
            "DPAD_UP" -> playerY = (playerY - 15).coerceAtLeast(0f)
            "DPAD_DOWN" -> playerY = (playerY + 15).coerceAtMost(600f)
            "JOYSTICK" -> {
                playerX = (playerX + dx * 20).coerceIn(0f, 800f)
                playerY = (playerY + dy * 20).coerceIn(0f, 600f)
            }
            "BUTTON_A" -> {
                playerY = (playerY - 100).coerceAtLeast(0f)
                Logger.d("Jump executed")
            }
        }

        // Execute scripts
        val scene = getCurrentSceneData()
        scene?.let {
            val inputs = it.optJSONObject("inputs")
            if (inputs?.has(action) == true) {
                try {
                    scriptEngine.executeScript(inputs.getString(action), this)
                } catch (e: Exception) {
                    Logger.e("Error executing script", e)
                }
            }
        }
    }

    fun getCurrentSceneData(): JSONObject? {
        return currentGame?.optJSONObject("scenes")?.optJSONObject(currentScene)
    }

    fun changeScene(sceneName: String) {
        Logger.d("Changing scene to: $sceneName")
        currentScene = sceneName
    }

    fun render(canvas: Canvas) {
        gameRenderer.render(canvas, this)
    }

    fun update(deltaTime: Long) {
        if (!isRunning) return
        
        // Apply gravity
        playerVelY += 0.5f
        playerY += playerVelY
        
        // Boundary check
        if (playerY > 600f) {
            playerY = 600f
            playerVelY = 0f
        }
    }

    fun stop() {
        Logger.d("Game stopped")
        isRunning = false
    }

    private fun loadGameFromAssets(context: Context, gameName: String): JSONObject {
        return try {
            val inputStream = context.assets.open("games/$gameName.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.readText()
            reader.close()
            inputStream.close()
            JSONObject(jsonString)
        } catch (e: Exception) {
            Logger.e("Error loading from assets, trying downloads", e)
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