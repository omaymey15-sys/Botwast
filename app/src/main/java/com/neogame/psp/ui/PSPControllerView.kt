package com.neogame.psp.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import com.neogame.psp.utils.Logger

/**
 * Contrôleur PSP complet
 * Gère la manette virtuelle complète PSP
 */
class PSPControllerView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private var inputCallback: ((String, Float) -> Unit)? = null

    private val dpadView: PSPDPadView
    private val buttonsView: PSPButtonsView
    private val leftAnalog: AnalogStickView
    private val rightAnalog: AnalogStickView

    init {
        // D-Pad (bas-gauche)
        dpadView = PSPDPadView(context, attrs).apply {
            layoutParams = LayoutParams(200, 200).apply {
                gravity = android.view.Gravity.BOTTOM or android.view.Gravity.LEFT
                setMargins(16, 0, 0, 16)
            }
            setInputCallback { direction ->
                inputCallback?.invoke(direction, 1f)
            }
        }
        addView(dpadView)

        // Boutons ABXY (bas-droite)
        buttonsView = PSPButtonsView(context, attrs).apply {
            layoutParams = LayoutParams(250, 250).apply {
                gravity = android.view.Gravity.BOTTOM or android.view.Gravity.RIGHT
                setMargins(0, 0, 16, 16)
            }
            setInputCallback { button ->
                inputCallback?.invoke(button, 1f)
            }
        }
        addView(buttonsView)

        // Left Analog
        leftAnalog = AnalogStickView(context, attrs).apply {
            layoutParams = LayoutParams(150, 150).apply {
                gravity = android.view.Gravity.BOTTOM or android.view.Gravity.LEFT
                setMargins(220, 0, 0, 16)
            }
            setInputCallback { x, y ->
                if (kotlin.math.abs(x) > 0.1f) inputCallback?.invoke("STEER", x)
                if (y > 0.1f) inputCallback?.invoke("ACCELERATE", y)
                if (y < -0.1f) inputCallback?.invoke("BRAKE", -y)
            }
        }
        addView(leftAnalog)

        // Right Analog
        rightAnalog = AnalogStickView(context, attrs).apply {
            layoutParams = LayoutParams(150, 150).apply {
                gravity = android.view.Gravity.BOTTOM or android.view.Gravity.RIGHT
                setMargins(0, 0, 270, 16)
            }
            setInputCallback { x, y ->
                inputCallback?.invoke("CAMERA_X", x)
                inputCallback?.invoke("CAMERA_Y", y)
            }
        }
        addView(rightAnalog)

        Logger.d("PSPControllerView initialisée")
    }

    fun setInputCallback(callback: (String, Float) -> Unit) {
        inputCallback = callback
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            super.onTouchEvent(it)
        }
        return true
    }
}