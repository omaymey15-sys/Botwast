package com.neogame.psp.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.neogame.psp.utils.Logger
import kotlin.math.sqrt

/**
 * Boutons ABXY PSP
 * Boutons d'action couleurs (gris transparent)
 */
class PSPButtonsView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var inputCallback: ((String) -> Unit)? = null
    private val paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 2f
    }

    private val buttonRadius = 35f
    private val buttonDistance = 60f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f

        // X
        drawButton(canvas, cx, cy - buttonDistance, "X", Color.parseColor("#88888888"))
        // Y
        drawButton(canvas, cx - buttonDistance, cy, "Y", Color.parseColor("#88888888"))
        // A
        drawButton(canvas, cx + buttonDistance, cy, "A", Color.parseColor("#88888888"))
        // B
        drawButton(canvas, cx, cy + buttonDistance, "B", Color.parseColor("#88888888"))
    }

    private fun drawButton(canvas: Canvas, x: Float, y: Float, label: String, color: Int) {
        paint.color = color
        paint.alpha = 220
        paint.style = Paint.Style.FILL
        canvas.drawCircle(x, y, buttonRadius, paint)

        paint.style = Paint.Style.STROKE
        paint.color = Color.parseColor("#44444499")
        canvas.drawCircle(x, y, buttonRadius, paint)

        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 28f
        canvas.drawText(label, x, y + 10, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                val cx = width / 2f
                val cy = height / 2f

                val buttons = listOf(
                    Pair("BUTTON_X", Pair(cx, cy - buttonDistance)),
                    Pair("BUTTON_Y", Pair(cx - buttonDistance, cy)),
                    Pair("BUTTON_A", Pair(cx + buttonDistance, cy)),
                    Pair("BUTTON_B", Pair(cx, cy + buttonDistance))
                )

                buttons.forEach { (label, pos) ->
                    val dist = sqrt((it.x - pos.first) * (it.x - pos.first) + (it.y - pos.second) * (it.y - pos.second))
                    if (dist < buttonRadius) {
                        inputCallback?.invoke(label)
                    }
                }
            }
        }
        return true
    }

    fun setInputCallback(callback: (String) -> Unit) {
        inputCallback = callback
    }
}