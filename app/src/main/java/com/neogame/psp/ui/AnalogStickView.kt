package com.neogame.psp.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.sqrt

/**
 * Analog Stick PSP
 * Sticks analogues gauche et droit
 */
class AnalogStickView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var inputCallback: ((Float, Float) -> Unit)? = null
    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private var touchX = 0f
    private var touchY = 0f
    private val deadZone = 0.15f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = width / 2f - 10f

        // Background
        paint.color = Color.parseColor("#55555588")
        paint.alpha = 200
        canvas.drawCircle(centerX, centerY, radius, paint)

        // Border
        paint.style = Paint.Style.STROKE
        paint.color = Color.parseColor("#44444499")
        paint.strokeWidth = 2f
        canvas.drawCircle(centerX, centerY, radius, paint)

        // Stick
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#88888888")
        val stickRadius = 15f
        canvas.drawCircle(centerX + touchX * 30, centerY + touchY * 30, stickRadius, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val centerX = width / 2f
            val centerY = height / 2f

            val x = (it.x - centerX) / (width / 2f)
            val y = (it.y - centerY) / (height / 2f)

            val distance = sqrt(x * x + y * y)

            if (distance > deadZone) {
                touchX = x.coerceIn(-1f, 1f)
                touchY = y.coerceIn(-1f, 1f)
                inputCallback?.invoke(touchX, touchY)
            } else {
                touchX = 0f
                touchY = 0f
            }

            invalidate()

            if (it.action == MotionEvent.ACTION_UP) {
                touchX = 0f
                touchY = 0f
                invalidate()
            }
        }
        return true
    }

    fun setInputCallback(callback: (Float, Float) -> Unit) {
        inputCallback = callback
    }
}