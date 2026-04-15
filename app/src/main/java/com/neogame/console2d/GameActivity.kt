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
        
        // Créer le canvas de jeu
        gameCanvas = GameCanvasView(this)
        gameContainer.addView(gameCanvas)

        // Créer l'overlay PSP
        pspOverlay = PSPOverlay(this) { action ->
            inputManager.sendAction(action)
        }
        gameContainer.addView(pspOverlay)

        // Initialiser les managers
        inputManager = InputManager(GameEngine)
        gamepadManager = GamepadManager(inputManager)

        // Charger le jeu
        val gameName = intent.getStringExtra("game_name") ?: "default"
        GameEngine.loadGame(this, gameName)
        
        // Lancer la boucle de rendu
        gameCanvas.setGameEngine(GameEngine)
        gameCanvas.startGameLoop()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        pspOverlay.onTouchEvent(event)
        return super.onTouchEvent(event)
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