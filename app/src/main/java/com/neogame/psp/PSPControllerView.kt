package com.neogame.psp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import kotlin.math.sqrt

class PSPControllerView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private var inputCallback: ((String, Float) -> Unit)? = null
    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#88888888")
        strokeWidth = 2f
    }

    fun setInputCallback(callback: (String, Float) -> Unit) {
        inputCallback = callback
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 4f
        val cy = height - 150f
        
        // D-Pad
        paint.color = Color.parseColor("#44444499")
        canvas.drawCircle(cx, cy, 60f, paint)
        
        // Buttons
        val bx = width * 3f / 4f
        val by = height - 150f
        paint.color = Color.parseColor("#88888888")
        canvas.drawCircle(bx - 80f, by, 30f, paint)
        canvas.drawCircle(bx, by - 80f, 30f, paint)
        canvas.drawCircle(bx + 80f, by, 30f, paint)
        canvas.drawCircle(bx, by + 80f, 30f, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    val cx = width / 4f
                    val cy = height - 150f
                    val bx = width * 3f / 4f
                    val by = height - 150f

                    val dDist = sqrt((it.x - cx) * (it.x - cx) + (it.y - cy) * (it.y - cy))
                    if (dDist < 80f) {
                        val angle = Math.atan2((it.y - cy).toDouble(), (it.x - cx).toDouble())
                        when {
                            angle > -Math.PI / 4 && angle <= Math.PI / 4 -> inputCallback?.invoke("RIGHT", 1f)
                            angle > Math.PI / 4 && angle <= 3 * Math.PI / 4 -> inputCallback?.invoke("DOWN", 1f)
                            angle > -3 * Math.PI / 4 && angle <= -Math.PI / 4 -> inputCallback?.invoke("UP", 1f)
                            else -> inputCallback?.invoke("LEFT", 1f)
                        }
                    }

                    listOf(
                        "BUTTON_A" to Pair(bx + 80f, by),
                        "BUTTON_B" to Pair(bx, by + 80f),
                        "BUTTON_X" to Pair(bx - 80f, by),
                        "BUTTON_Y" to Pair(bx, by - 80f)
                    ).forEach { (label, pos) ->
                        val dist = sqrt((it.x - pos.first) * (it.x - pos.first) + (it.y - pos.second) * (it.y - pos.second))
                        if (dist < 40f) inputCallback?.invoke(label, 1f)
                    }
                }
            }
        }
        return true
    }
}