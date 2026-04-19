package com.neogame.psp.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Vue HUD
 * Affichage des informations de jeu (santé, score, etc.)
 */
class HUDView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
    }

    private var speed = 0f
    private var position = 1
    private var lap = 1
    private var boost = 100
    private var nitro = 3

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.textSize = 24f
        paint.color = Color.WHITE
        canvas.drawText("Vitesse: ${speed.toInt()}", 20f, 50f, paint)
        canvas.drawText("Pos: $position", 20f, 100f, paint)
        canvas.drawText("Tour: $lap", 20f, 150f, paint)
        canvas.drawText("Boost: $boost%", 20f, 200f, paint)
        canvas.drawText("Nitro: $nitro", 20f, 250f, paint)
    }

    fun updateStats(speed: Float, position: Int, lap: Int, boost: Int, nitro: Int) {
        this.speed = speed
        this.position = position
        this.lap = lap
        this.boost = boost
        this.nitro = nitro
        invalidate()
    }
}