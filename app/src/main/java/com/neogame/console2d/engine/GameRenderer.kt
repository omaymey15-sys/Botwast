package com.neogame.console2d.engine

import android.graphics.*
import com.neogame.console2d.utils.Logger

class GameRenderer {

    private val paintPlayer = Paint().apply {
        color = Color.RED
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val paintPlayerBorder = Paint().apply {
        color = Color.YELLOW
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private val paintBackground = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
    }

    private val paintText = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    }

    private val paintObject = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    fun render(canvas: Canvas, engine: GameEngine) {
        try {
            // Draw background
            canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), paintBackground)

            // Draw scene objects
            val scene = engine.getCurrentSceneData()
            scene?.let {
                val sceneManager = SceneManager()
                val objects = sceneManager.getSceneObjects(it)
                
                for (obj in objects) {
                    paintObject.color = obj.color
                    canvas.drawRect(obj.x, obj.y, obj.x + obj.width, obj.y + obj.height, paintObject)
                }
            }

            // Draw player
            canvas.drawRect(
                engine.playerX,
                engine.playerY,
                engine.playerX + engine.playerWidth,
                engine.playerY + engine.playerHeight,
                paintPlayer
            )
            
            // Draw player border
            canvas.drawRect(
                engine.playerX,
                engine.playerY,
                engine.playerX + engine.playerWidth,
                engine.playerY + engine.playerHeight,
                paintPlayerBorder
            )

            // Draw HUD
            drawHUD(canvas, engine)
        } catch (e: Exception) {
            Logger.e("Error rendering frame", e)
        }
    }

    private fun drawHUD(canvas: Canvas, engine: GameEngine) {
        // Draw position info
        val posText = "X: ${engine.playerX.toInt()} Y: ${engine.playerY.toInt()}"
        canvas.drawText(posText, 20f, 50f, paintText)
        
        // Draw scene name
        val sceneName = engine.currentScene
        canvas.drawText("Scene: $sceneName", 20f, 100f, paintText)
    }
}