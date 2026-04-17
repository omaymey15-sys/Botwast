package com.neogame.psp.utils

/**
 * Extensions Kotlin pour ConsolePSP
 */

fun Float.coerceIn(min: Float, max: Float): Float {
    return when {
        this < min -> min
        this > max -> max
        else -> this
    }
}