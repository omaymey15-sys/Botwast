package com.neogame.console2d

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.neogame.console2d.engine.GameEngine
import com.neogame.console2d.input.GamepadManager
import com.neogame.console2d.input.InputManager
import com.neogame.console2d.ui.GameCanvasView
import com.neogame.console2d.utils.Logger

class GameActivity : AppCompatActivity() {

    private lateinit var gameCanvasContainer: FrameLayout
    private lateinit var gameCanvas: GameCanvasView
    private lateinit var inputManager: InputManager
    private lateinit var gamepadManager: GamepadManager

    // D-Pad Buttons
    private lateinit var dpadUp: Button
    private lateinit var dpadDown: Button
    private lateinit var dpadLeft: Button
    private lateinit var dpadRight: Button

    // Action Buttons
    private lateinit var buttonA: Button
    private lateinit var buttonB: Button
    private lateinit var buttonX: Button
    private lateinit var buttonY: Button

    // Special Buttons
    private lateinit var buttonL1: Button
    private lateinit var buttonR1: Button
    private lateinit var buttonSelect: Button
    private lateinit var buttonStart: Button

    // Joystick
    private lateinit var joystickStick: View
    private var joystickCenterX = 0f
    private var joystickCenterY = 0f
    private val joystickRadius = 40f
    private var gameName: String = "platform_game"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameName = intent.getStringExtra("game_name") ?: "platform_game"
        Logger.d("GameActivity created with game: $gameName")

        initViews()
        initManagers()
        initGameEngine()
        setupButtonListeners()
    }

    private fun initViews() {
        gameCanvasContainer = findViewById(R.id.gameCanvasContainer)

        // D-Pad
        dpadUp = findViewById(R.id.dpadUp)
        dpadDown = findViewById(R.id.dpadDown)
        dpadLeft = findViewById(R.id.dpadLeft)
        dpadRight = findViewById(R.id.dpadRight)

        // Action Buttons
        buttonA = findViewById(R.id.buttonA)
        buttonB = findViewById(R.id.buttonB)
        buttonX = findViewById(R.id.buttonX)
        buttonY = findViewById(R.id.buttonY)

        // Special Buttons
        buttonL1 = findViewById(R.id.buttonL1)
        buttonR1 = findViewById(R.id.buttonR1)
        buttonSelect = findViewById(R.id.buttonSelect)
        buttonStart = findViewById(R.id.buttonStart)

        // Joystick
        joystickStick = findViewById(R.id.joystickStick)
    }

    private fun initManagers() {
        inputManager = InputManager(GameEngine)
        gamepadManager = GamepadManager(inputManager)
    }

    private fun initGameEngine() {
        gameCanvas = GameCanvasView(this)
        gameCanvasContainer.addView(gameCanvas, 0)

        try {
            GameEngine.loadGame(this, gameName)
            gameCanvas.setGameEngine(GameEngine)
            gameCanvas.startGameLoop()
            Logger.d("Game engine initialized")
        } catch (e: Exception) {
            Logger.e("Error initializing game engine", e)
            Toast.makeText(this, "Erreur lors du chargement du jeu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupButtonListeners() {
        // D-Pad listeners
        dpadUp.setOnClickListener { sendAction("DPAD_UP") }
        dpadDown.setOnClickListener { sendAction("DPAD_DOWN") }
        dpadLeft.setOnClickListener { sendAction("DPAD_LEFT") }
        dpadRight.setOnClickListener { sendAction("DPAD_RIGHT") }

        // Action buttons listeners
        buttonA.setOnClickListener { sendAction("BUTTON_A") }
        buttonB.setOnClickListener { sendAction("BUTTON_B") }
        buttonX.setOnClickListener { sendAction("BUTTON_X") }
        buttonY.setOnClickListener { sendAction("BUTTON_Y") }

        // Special buttons listeners
        buttonL1.setOnClickListener { sendAction("L1") }
        buttonR1.setOnClickListener { sendAction("R1") }
        buttonSelect.setOnClickListener { sendAction("SELECT") }
        buttonStart.setOnClickListener { sendAction("START") }

        // Joystick listener
        joystickStick.setOnTouchListener { _, event ->
            handleJoystickTouch(event)
        }
    }

    private fun sendAction(action: String) {
        inputManager.sendAction(action)
        Logger.d("Action sent: $action")
    }

    private fun handleJoystickTouch(event: MotionEvent): Boolean {
        val joystickContainer = joystickStick.parent as? View ?: return false

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val x = event.x
                val y = event.y

                val dx = (x - joystickContainer.width / 2) / joystickRadius
                val dy = (y - joystickContainer.height / 2) / joystickRadius

                val normalizedDx = dx.coerceIn(-1f, 1f)
                val normalizedDy = dy.coerceIn(-1f, 1f)

                inputManager.sendJoystickInput(normalizedDx, normalizedDy)
            }

            MotionEvent.ACTION_UP -> {
                inputManager.sendJoystickInput(0f, 0f)
            }
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

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        event?.let { gamepadManager.onMotionEvent(it) }
        return super.onGenericMotionEvent(event)
    }

    override fun onPause() {
        super.onPause()
        Logger.d("GameActivity paused")
    }

    override fun onDestroy() {
        super.onDestroy()
        GameEngine.stop()
        gameCanvas.stopGameLoop()
        Logger.d("GameActivity destroyed")
    }
}