package com.neogame.console2d.ui

import android.content.Context
import android.graphics.Canvas
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.neogame.console2d.engine.GameEngine
import com.neogame.console2d.utils.Constants
import com.neogame.console2d.utils.Logger
import kotlin.concurrent.thread

class GameCanvasView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private var gameEngine: GameEngine? = null
    private var isRunning = false
    private var gameThread: Thread? = null
    private var lastFrameTime = System.currentTimeMillis()
    private var frameCount = 0
    private var fps = 0

    init {
        holder.addCallback(this)
        setZOrderMediaOverlay(true)
    }

    fun setGameEngine(engine: GameEngine) {
        gameEngine = engine
    }

    fun startGameLoop() {
        isRunning = true
        gameThread = thread(name = "GameThread") {
            while (isRunning && gameEngine?.isRunning == true) {
                val currentTime = System.currentTimeMillis()
                val deltaTime = currentTime - lastFrameTime
                lastFrameTime = currentTime

                gameEngine?.update(deltaTime)

                try {
                    val canvas = holder.lockCanvas()
                    canvas?.let {
                        synchronized(holder) {
                            gameEngine?.render(it)
                        }
                        holder.unlockCanvasAndPost(it)
                    }
                } catch (e: Exception) {
                    Logger.e("Error rendering frame", e)
                }

                frameCount++
                if (currentTime % 1000 < Constants.FRAME_DELAY) {
                    fps = frameCount
                    frameCount = 0
                }

                Thread.sleep(Constants.FRAME_DELAY.toLong())
            }
            Logger.d("Game loop ended")
        }
        Logger.d("Game loop started")
    }

    fun stopGameLoop() {
        isRunning = false
        gameThread?.join(5000)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Logger.d("Surface created")
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Logger.d("Surface changed: $width x $height")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Logger.d("Surface destroyed")
        stopGameLoop()
    }
}