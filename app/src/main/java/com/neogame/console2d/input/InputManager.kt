package com.neogame.console2d.input

import com.neogame.console2d.engine.GameEngine

class InputManager(private val gameEngine: GameEngine) {

    fun sendAction(action: String) {
        gameEngine.onInput(action)
    }

    fun sendJoystickInput(dx: Float, dy: Float) {
        gameEngine.onInput("JOYSTICK", dx, dy)
    }

    fun sendButtonPress(button: String) {
        gameEngine.onInput(button)
    }
}