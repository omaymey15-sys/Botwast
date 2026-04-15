package com.neogame.console2d.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View

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

    init {
        // Position des boutons
        val rightX = width - 200f
        val rightY = height - 200f

        buttons["A"] = ButtonInfo(rightX + 60, rightY + 60, 40f, "A")
        buttons["B"] = ButtonInfo(rightX, rightY + 120, 40f, "B")
        buttons["X"] = ButtonInfo(rightX, rightY, 40f, "X")
        buttons["Y"] = ButtonInfo(rightX + 60, rightY, 40f, "Y")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Dessiner joystick
        canvas.drawCircle(joystickCenterX, joystickCenterY, joystickRadius, paintButton)
        canvas.drawCircle(
            joystickCenterX + joystickX * joystickRadius,
            joystickCenterY + joystickY * joystickRadius,
            30f,
            paintButtonPressed
        )

        // Dessiner boutons
        for ((key, button) in buttons) {
            val paint = if (key == pressedButton) paintButtonPressed else paintButton
            canvas.drawCircle(button.x, button.y, button.radius, paint)
            canvas.drawText(button.label, button.x, button.y + 10, paintText)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    val x = it.x
                    val y = it.y

                    // Vérifier joystick
                    val distToJoystick = Math.sqrt(
                        Math.pow((x - joystickCenterX).toDouble(), 2.0) +
                                Math.pow((y - joystickCenterY).toDouble(), 2.0)
                    )

                    if (distToJoystick < joystickRadius * 1.5) {
                        val dx = (x - joystickCenterX) / (joystickRadius * 1.5)
                        val dy = (y - joystickCenterY) / (joystickRadius * 1.5)
                        joystickX = dx.coerceIn(-1f, 1f)
                        joystickY = dy.coerceIn(-1f, 1f)
                        onAction("JOYSTICK")
                        invalidate()
                        return true
                    }

                    // Vérifier boutons
                    for ((key, button) in buttons) {
                        val dist = Math.sqrt(
                            Math.pow((x - button.x).toDouble(), 2.0) +
                                    Math.pow((y - button.y).toDouble(), 2.0)
                        )
                        if (dist < button.radius * 1.5) {
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
        }
        return super.onTouchEvent(event)
    }
}