package com.neogame.psp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class GameView(context: Context) : View(context) {

    private var gameEngine: GameEngine? = null
    private var renderer: BaseRenderer? = null
    private var gameThread: Thread? = null
    private var isRunning = false
    private val paint = Paint().apply {
        isAntiAlias = true
        textSize = 24f
        color = Color.WHITE
    }

    fun setGameEngine(engine: GameEngine) {
        gameEngine = engine
    }

    fun setRenderer(render: BaseRenderer) {
        renderer = render
    }

    fun startGameLoop() {
        isRunning = true
        gameThread = Thread {
            var lastTime = System.currentTimeMillis()
            while (isRunning) {
                try {
                    val currentTime = System.currentTimeMillis()
                    val deltaTime = (currentTime - lastTime) / 1000f
                    lastTime = currentTime
                    
                    gameEngine?.update(deltaTime)
                    postInvalidate()
                    Thread.sleep(16)
                } catch (e: Exception) {
                    Logger.e("Loop error: ${e.message}")
                }
            }
        }
        gameThread?.start()
    }

    fun stopGameLoop() {
        isRunning = false
        gameThread?.join()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.parseColor("#87CEEB"))
        
        renderer?.render(canvas, gameEngine)
        
        paint.color = Color.WHITE
        paint.textSize = 20f
        canvas.drawText("🎮 ConsolePSP | ${renderer?.getName()}", 20f, 40f, paint)
    }

    fun onResume() { startGameLoop() }
    fun onPause() { stopGameLoop() }
}