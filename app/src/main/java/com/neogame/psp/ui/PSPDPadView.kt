package com.neogame.psp.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.neogame.psp.utils.Logger

/**
 * D-Pad PSP
 * Boutons directionnels haut/bas/gauche/droite
 */
class PSPDPadView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var inputCallback: ((String) -> Unit)? = null
    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#88888888")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        canvas.drawCircle(centerX, centerY, 40f, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    val centerX = width / 2f
                    val centerY = height / 2f
                    val dx = it.x - centerX
                    val dy = it.y - centerY

                    when {
                        dy < -30 -> inputCallback?.invoke("UP")
                        dy > 30 -> inputCallback?.invoke("DOWN")
                        dx < -30 -> inputCallback?.invoke("LEFT")
                        dx > 30 -> inputCallback?.invoke("RIGHT")
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