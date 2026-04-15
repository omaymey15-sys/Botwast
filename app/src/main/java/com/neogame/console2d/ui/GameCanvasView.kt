package com.neogame.console2d.ui

import android.content.Context
import android.graphics.Canvas
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.neogame.console2d.engine.GameEngine
import kotlin.concurrent.thread

class GameCanvasView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private var gameEngine: GameEngine? = null
    private var isRunning = false
    private var gameThread: Thread? = null
    private var lastFrameTime = System.currentTimeMillis()

    init {
        holder.addCallback(this)
    }

    fun setGameEngine(engine: GameEngine) {
        gameEngine = engine
    }

    fun startGameLoop() {
        isRunning = true
        gameThread = thread {
            while (isRunning && gameEngine?.isRunning == true) {
                val currentTime = System.currentTimeMillis()
                val deltaTime = currentTime - lastFrameTime
                lastFrameTime = currentTime

                gameEngine?.update(deltaTime)

                val canvas = holder.lockCanvas()
                canvas?.let {
                    gameEngine?.render(it)
                    holder.unlockCanvasAndPost(it)
                }

                Thread.sleep(16) // ~60 FPS
            }
        }
    }

    fun stopGameLoop() {
        isRunning = false
        gameThread?.join()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {}
}