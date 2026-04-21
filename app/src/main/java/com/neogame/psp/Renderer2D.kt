package com.neogame.psp

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

abstract class BaseRenderer {
    abstract fun render(canvas: Canvas?, gameEngine: GameEngine?)
    abstract fun dispose()
    abstract fun getName(): String
}

class Renderer2D : BaseRenderer() {
    
    private val paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 2f
    }

    override fun render(canvas: Canvas?, gameEngine: GameEngine?) {
        if (canvas == null || gameEngine == null) return
        
        gameEngine.getGameObjects().forEach { obj ->
            paint.color = obj.color
            paint.style = Paint.Style.FILL
            canvas.drawRect(obj.x, obj.y, obj.x + obj.width, obj.y + obj.height, paint)
            
            paint.color = Color.BLACK
            paint.style = Paint.Style.STROKE
            canvas.drawRect(obj.x, obj.y, obj.x + obj.width, obj.y + obj.height, paint)
            
            paint.color = Color.WHITE
            paint.textSize = 12f
            canvas.drawText(obj.type, obj.x + 5, obj.y + 20, paint)
        }
    }

    override fun dispose() {}
    override fun getName(): String = "2D Canvas Renderer"
}