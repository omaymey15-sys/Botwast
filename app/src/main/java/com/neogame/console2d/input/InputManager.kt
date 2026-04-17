package com.neogame.console2d.input

import com.neogame.console2d.engine.GameEngine
import com.neogame.console2d.utils.Logger

class InputManager(private val gameEngine: GameEngine) {

    fun sendAction(action: String) {
        Logger.d("InputManager - Action: $action")
        gameEngine.onInput(action)
    }

    fun sendJoystickInput(dx: Float, dy: Float) {
        if (dx != 0f || dy != 0f) {
            gameEngine.onInput("JOYSTICK", dx, dy)
        }
    }

    fun sendButtonPress(button: String) {
        Logger.d("InputManager - Button: $button")
        gameEngine.onInput(button)
    }
}