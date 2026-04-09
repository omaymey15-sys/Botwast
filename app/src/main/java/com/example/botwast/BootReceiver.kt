package com.example.botwast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val dataManager = context?.let { DataManager(it) }
            
            // Restart bot service if it was enabled before reboot
            if (dataManager?.isBotEnabled() == true) {
                // Service will be started automatically by the system
            }
        }
    }
}