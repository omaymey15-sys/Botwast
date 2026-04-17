package com.neogame.console2d.utils

import android.util.Log
import com.neogame.console2d.utils.Constants

object Logger {
    
    fun d(message: String) {
        if (Constants.DEBUG) {
            Log.d(Constants.LOG_TAG, message)
        }
    }
    
    fun e(message: String, exception: Exception? = null) {
        if (Constants.DEBUG) {
            Log.e(Constants.LOG_TAG, message, exception)
        } else {
            Log.e(Constants.LOG_TAG, message, exception)
        }
    }
    
    fun i(message: String) {
        if (Constants.DEBUG) {
            Log.i(Constants.LOG_TAG, message)
        }
    }
    
    fun w(message: String) {
        if (Constants.DEBUG) {
            Log.w(Constants.LOG_TAG, message)
        }
    }
}