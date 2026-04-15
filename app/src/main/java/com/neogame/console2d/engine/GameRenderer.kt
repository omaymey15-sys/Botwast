package com.neogame.console2d.engine

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class GameRenderer {

    private val paintPlayer = Paint().apply {
        color = Color.RED
        isAntiAlias = true
    }

    private val paintBackground = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
    }

    private val paintText = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        isAntiAlias = true
    }

    fun render(canvas: Canvas, engine: GameEngine) {
        // Dessiner le fond
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), paintBackground)

        // Dessiner le joueur
        canvas.drawRect(
            engine.playerX,
            engine.playerY,
            engine.playerX + engine.playerWidth,
            engine.playerY + engine.playerHeight,
            paintPlayer
        )

        // Dessiner les objets de la scène
        val scene = engine.getCurrentSceneData()
        scene?.let {
            val sceneManager = SceneManager()
            val objects = sceneManager.getSceneObjects(it)
            for (obj in objects) {
                val paint = Paint().apply {
                    color = obj.color
                    isAntiAlias = true
                }
                canvas.drawRect(obj.x, obj.y, obj.x + obj.width, obj.y + obj.height, paint)
            }
        }
    }
}