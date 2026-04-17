package com.neogame.psp.utils

import android.util.Log

/**
 * Utilitaire de logging centralisé
 */
object Logger {
    private const val TAG = "ConsolePSP"

    fun d(message: String) {
        Log.d(TAG, "[DEBUG] $message")
    }

    fun i(message: String) {
        Log.i(TAG, "[INFO] $message")
    }

    fun w(message: String) {
        Log.w(TAG, "[WARN] $message")
    }

    fun e(message: String) {
        Log.e(TAG, "[ERROR] $message")
    }
}