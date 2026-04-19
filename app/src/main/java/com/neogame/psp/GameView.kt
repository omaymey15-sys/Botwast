package com.neogame.psp.renderer

import android.content.Context
import android.graphics.Canvas
import android.view.View
import com.neogame.psp.emulator.GameEngine
import com.neogame.psp.utils.Logger

/**
 * Vue de jeu
 * Gère l'affichage et la boucle de jeu
 */
class GameView(context: Context) : View(context) {

    private var gameEngine: GameEngine? = null
    private var renderEngine: RenderEngine? = null
    private var gameThread: Thread? = null
    private var isRunning = false

    fun setGameEngine(engine: GameEngine) {
        gameEngine = engine
    }

    fun setRenderEngine(renderer: RenderEngine) {
        renderEngine = renderer
    }

    fun startGameLoop() {
        isRunning = true
        gameThread = Thread {
            while (isRunning) {
                try {
                    Thread.sleep(16) // ~60 FPS
                    postInvalidate()
                    gameEngine?.update(0.016f)
                } catch (e: Exception) {
                    Logger.e("Erreur boucle: ${e.message}")
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
        gameEngine?.let { engine ->
            renderEngine?.render(canvas, engine, 0.016f)
        }
    }

    fun onResume() {
        startGameLoop()
    }

    fun onPause() {
        stopGameLoop()
    }
}