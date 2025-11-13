package com.shakti.ai.stealth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

/**
 * Boot Receiver
 *
 * Automatically starts the StealthTriggerService when:
 * - Device boots up (BOOT_COMPLETED)
 * - App is updated (MY_PACKAGE_REPLACED)
 *
 * This ensures 24/7 protection is always active after user enables it once.
 */
class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootReceiver"
        private const val PREF_NAME = "stealth_prefs"
        private const val KEY_SERVICE_ENABLED = "service_enabled"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                Log.i(TAG, "Received: ${intent.action}")

                // Check if user had enabled the service before
                val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                val wasEnabled = prefs.getBoolean(KEY_SERVICE_ENABLED, false)

                if (wasEnabled) {
                    Log.i(TAG, "Auto-starting StealthTriggerService")
                    startStealthService(context)
                } else {
                    Log.d(TAG, "Service was not enabled, not starting")
                }
            }
        }
    }

    private fun startStealthService(context: Context) {
        val intent = Intent(context, StealthTriggerService::class.java).apply {
            action = StealthTriggerService.ACTION_START_MONITORING
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            Log.i(TAG, "✓ StealthTriggerService started")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting service", e)
        }
    }
}
