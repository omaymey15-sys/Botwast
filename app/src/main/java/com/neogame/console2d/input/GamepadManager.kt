package com.neogame.console2d.input

import android.view.KeyEvent
import android.view.MotionEvent
import com.neogame.console2d.utils.Logger
import com.neogame.console2d.utils.Constants

class GamepadManager(private val inputManager: InputManager) {

    private var lastMotionTime = 0L
    private val motionDelay = 50L // ms between motion events

    fun onKeyDown(keyCode: Int, event: KeyEvent?) {
        val action = mapKeyCodeToAction(keyCode)
        if (action != null) {
            inputManager.sendButtonPress(action)
            Logger.d("GamepadManager - KeyDown: $action (keyCode: $keyCode)")
        }
    }

    fun onKeyUp(keyCode: Int, event: KeyEvent?) {
        Logger.d("GamepadManager - KeyUp: $keyCode")
    }

    fun onMotionEvent(event: MotionEvent) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastMotionTime < motionDelay) {
            return
        }
        lastMotionTime = currentTime

        val dx = event.getAxisValue(MotionEvent.AXIS_X)
        val dy = event.getAxisValue(MotionEvent.AXIS_Y)
        
        val absDx = Math.abs(dx)
        val absDy = Math.abs(dy)

        if (absDx > Constants.JOYSTICK_DEADZONE || absDy > Constants.JOYSTICK_DEADZONE) {
            inputManager.sendJoystickInput(dx, dy)
            Logger.d("GamepadManager - Motion: dx=$dx, dy=$dy")
        }
    }

    private fun mapKeyCodeToAction(keyCode: Int): String? {
        return when (keyCode) {
            KeyEvent.KEYCODE_BUTTON_A -> "BUTTON_A"
            KeyEvent.KEYCODE_BUTTON_B -> "BUTTON_B"
            KeyEvent.KEYCODE_BUTTON_X -> "BUTTON_X"
            KeyEvent.KEYCODE_BUTTON_Y -> "BUTTON_Y"
            KeyEvent.KEYCODE_BUTTON_START -> "START"
            KeyEvent.KEYCODE_BUTTON_SELECT -> "SELECT"
            KeyEvent.KEYCODE_BUTTON_L1 -> "L1"
            KeyEvent.KEYCODE_BUTTON_R1 -> "R1"
            KeyEvent.KEYCODE_BUTTON_L2 -> "L2"
            KeyEvent.KEYCODE_BUTTON_R2 -> "R2"
            KeyEvent.KEYCODE_DPAD_UP -> "DPAD_UP"
            KeyEvent.KEYCODE_DPAD_DOWN -> "DPAD_DOWN"
            KeyEvent.KEYCODE_DPAD_LEFT -> "DPAD_LEFT"
            KeyEvent.KEYCODE_DPAD_RIGHT -> "DPAD_RIGHT"
            else -> null
        }
    }
}