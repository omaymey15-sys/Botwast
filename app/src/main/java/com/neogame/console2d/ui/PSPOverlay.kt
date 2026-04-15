package com.neogame.console2d.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import kotlin.math.hypot

class PSPOverlay(context: Context, private val onAction: (String) -> Unit) : View(context) {

    private val paintButton = Paint().apply {
        color = Color.GRAY
        isAntiAlias = true
    }

    private val paintButtonPressed = Paint().apply {
        color = Color.LTGRAY
        isAntiAlias = true
    }

    private val paintText = Paint().apply {
        color = Color.BLACK
        textSize = 20f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    private var joystickX = 0f
    private var joystickY = 0f

    private val joystickRadius = 60f
    private val joystickCenterX = 150f
    private val joystickCenterY = 800f

    private val buttons = mutableMapOf<String, ButtonInfo>()
    private var pressedButton: String? = null

    data class ButtonInfo(
        val x: Float,
        val y: Float,
        val radius: Float,
        val label: String
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val rightX = w - 200f
        val rightY = h - 200f

        buttons.clear()

        buttons["A"] = ButtonInfo(rightX + 60, rightY + 60, 40f, "A")
        buttons["B"] = ButtonInfo(rightX, rightY + 120, 40f, "B")
        buttons["X"] = ButtonInfo(rightX, rightY, 40f, "X")
        buttons["Y"] = ButtonInfo(rightX + 60, rightY, 40f, "Y")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 🎮 Joystick base
        canvas.drawCircle(joystickCenterX, joystickCenterY, joystickRadius, paintButton)

        // 🎮 Joystick stick
        canvas.drawCircle(
            joystickCenterX + joystickX * joystickRadius,
            joystickCenterY + joystickY * joystickRadius,
            30f,
            paintButtonPressed
        )

        // 🔘 Buttons
        for ((key, button) in buttons) {
            val paint = if (key == pressedButton) paintButtonPressed else paintButton
            canvas.drawCircle(button.x, button.y, button.radius, paint)
            canvas.drawText(button.label, button.x, button.y + 10f, paintText)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val x = event.x
        val y = event.y

        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {

                // 🎮 joystick distance
                val distJoy = hypot(
                    x - joystickCenterX,
                    y - joystickCenterY
                )

                if (distJoy < joystickRadius * 1.5f) {

                    val dx = (x - joystickCenterX) / (joystickRadius * 1.5f)
                    val dy = (y - joystickCenterY) / (joystickRadius * 1.5f)

                    joystickX = dx.coerceIn(-1f, 1f)
                    joystickY = dy.coerceIn(-1f, 1f)

                    onAction("JOYSTICK")
                    invalidate()
                    return true
                }

                // 🔘 buttons check
                for ((key, button) in buttons) {

                    val dist = hypot(
                        x - button.x,
                        y - button.y
                    )

                    if (dist < button.radius * 1.5f) {
                        pressedButton = key
                        onAction("BUTTON_$key")
                        invalidate()
                        return true
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                joystickX = 0f
                joystickY = 0f
                pressedButton = null
                invalidate()
            }
        }

        return true
    }
}