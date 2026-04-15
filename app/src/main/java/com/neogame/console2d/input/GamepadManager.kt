package com.neogame.console2d.input

import android.view.KeyEvent
import android.util.Log

class GamepadManager(private val inputManager: InputManager) {

    fun onKeyDown(keyCode: Int, event: KeyEvent?) {
        val action = mapKeyCodeToAction(keyCode)
        if (action != null) {
            inputManager.sendButtonPress(action)
            Log.d("GamepadManager", "Key pressed: $action")
        }
    }

    fun onKeyUp(keyCode: Int, event: KeyEvent?) {
        Log.d("GamepadManager", "Key released: $keyCode")
    }

    fun onMotionEvent(event: MotionEvent) {
        val dx = event.getAxisValue(MotionEvent.AXIS_X)
        val dy = event.getAxisValue(MotionEvent.AXIS_Y)
        
        if (dx != 0f || dy != 0f) {
            inputManager.sendJoystickInput(dx, dy)
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
            KeyEvent.KEYCODE_DPAD_UP -> "DPAD_UP"
            KeyEvent.KEYCODE_DPAD_DOWN -> "DPAD_DOWN"
            KeyEvent.KEYCODE_DPAD_LEFT -> "DPAD_LEFT"
            KeyEvent.KEYCODE_DPAD_RIGHT -> "DPAD_RIGHT"
            else -> null
        }
    }
}

import android.view.MotionEvent