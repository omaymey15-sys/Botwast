package com.neogame.psp

import android.util.Log

object Logger {
    private const val TAG = "ConsolePSP"
    fun d(msg: String) = Log.d(TAG, msg)
    fun i(msg: String) = Log.i(TAG, msg)
    fun w(msg: String) = Log.w(TAG, msg)
    fun e(msg: String) = Log.e(TAG, msg)
}