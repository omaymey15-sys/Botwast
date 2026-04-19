package com.neogame.psp.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

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

        // Dessin du D-Pad (simple cercle)
        canvas.drawCircle(centerX, centerY, 40f, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {

                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE -> {
                    val centerX = width / 2f
                    val centerY = height / 2f

                    val dx = it.x - centerX
                    val dy = it.y - centerY

                    // Détection direction intelligente
                    if (abs(dx) > abs(dy)) {
                        when {
                            dx < -30 -> inputCallback?.invoke("LEFT")
                            dx > 30 -> inputCallback?.invoke("RIGHT")
                            else -> {}
                        }
                    } else {
                        when {
                            dy < -30 -> inputCallback?.invoke("UP")
                            dy > 30 -> inputCallback?.invoke("DOWN")
                            else -> {}
                        }
                    }
                }

                MotionEvent.ACTION_UP -> {
                    // Optionnel : arrêter le mouvement
                    inputCallback?.invoke("STOP")
                }

                else -> {}
            }
        }
        return true
    }

    fun setInputCallback(callback: (String) -> Unit) {
        inputCallback = callback
    }
}