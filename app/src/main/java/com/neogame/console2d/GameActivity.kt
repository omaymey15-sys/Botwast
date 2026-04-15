package com.neogame.console2d

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.neogame.console2d.engine.GameEngine
import com.neogame.console2d.input.GamepadManager
import com.neogame.console2d.input.InputManager
import com.neogame.console2d.ui.GameCanvasView
import com.neogame.console2d.ui.PSPOverlay

class GameActivity : AppCompatActivity() {

    private lateinit var gameContainer: FrameLayout
    private lateinit var gameCanvas: GameCanvasView
    private lateinit var pspOverlay: PSPOverlay
    private lateinit var inputManager: InputManager
    private lateinit var gamepadManager: GamepadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameContainer = findViewById(R.id.gameContainer)

        // 🎮 Input system
        inputManager = InputManager(GameEngine)
        gamepadManager = GamepadManager(inputManager)

        // 🎮 Game canvas
        gameCanvas = GameCanvasView(this)
        gameContainer.addView(gameCanvas)

        // 🎮 PSP overlay
        pspOverlay = PSPOverlay(this) { action ->
            inputManager.sendAction(action)
        }
        gameContainer.addView(pspOverlay)

        // 🎮 Load game
        val gameName = intent.getStringExtra("game_name") ?: "default"
        GameEngine.loadGame(this, gameName)

        // 🎮 Start loop
        gameCanvas.setGameEngine(GameEngine)
        gameCanvas.startGameLoop()
    }

    // ✅ FIX TOUCH (IMPORTANT)
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            pspOverlay.onTouchEvent(it)
        }
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        gamepadManager.onKeyDown(keyCode, event)
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        gamepadManager.onKeyUp(keyCode, event)
        return super.onKeyUp(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        GameEngine.stop()
        gameCanvas.stopGameLoop()
    }
}