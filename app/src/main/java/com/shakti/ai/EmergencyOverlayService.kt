package com.shakti.ai

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView

/**
 * Emergency Overlay Service
 *
 * Shows a system-level overlay on top of all apps when emergency is detected.
 * This ensures the user sees the emergency alert even if they're using other apps.
 */
class EmergencyOverlayService : Service() {

    companion object {
        private const val TAG = "EmergencyOverlay"
        const val ACTION_SHOW_EMERGENCY = "com.shakti.ai.SHOW_EMERGENCY"
        const val ACTION_HIDE_EMERGENCY = "com.shakti.ai.HIDE_EMERGENCY"
        const val EXTRA_EVIDENCE_ID = "evidence_id"
        const val EXTRA_TRIGGER_TYPE = "trigger_type"
        const val EXTRA_LOCATION = "location"
    }

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var isShowing = false

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        Log.d(TAG, "Emergency Overlay Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SHOW_EMERGENCY -> {
                val evidenceId = intent.getStringExtra(EXTRA_EVIDENCE_ID) ?: "Unknown"
                val triggerType = intent.getStringExtra(EXTRA_TRIGGER_TYPE) ?: "EMERGENCY"
                val location = intent.getStringExtra(EXTRA_LOCATION) ?: "Unknown"
                showEmergencyOverlay(evidenceId, triggerType, location)
            }

            ACTION_HIDE_EMERGENCY -> {
                hideEmergencyOverlay()
                stopSelf()
            }

            else -> {
                // Handle evidence data from StealthBodyguardManager
                val evidenceId = intent?.getStringExtra("evidenceId") ?: "Unknown"
                val latitude = intent?.getDoubleExtra("locationLatitude", 0.0) ?: 0.0
                val longitude = intent?.getDoubleExtra("locationLongitude", 0.0) ?: 0.0
                val threatType = intent?.getStringExtra("threatType") ?: "EMERGENCY"
                val location = if (latitude != 0.0 && longitude != 0.0) {
                    String.format("%.4f, %.4f", latitude, longitude)
                } else {
                    "Unknown"
                }
                showEmergencyOverlay(evidenceId, threatType, location)
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun showEmergencyOverlay(evidenceId: String, triggerType: String, location: String) {
        if (isShowing) {
            Log.d(TAG, "Overlay already showing")
            return
        }

        try {
            // Inflate the overlay layout
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            overlayView = inflater.inflate(R.layout.emergency_overlay, null)

            // Setup overlay layout params
            val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                PixelFormat.TRANSLUCENT
            )

            params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            params.y = 100 // 100px from top

            // Update overlay content
            overlayView?.apply {
                findViewById<TextView>(R.id.evidence_id_text)?.text = "Evidence ID: $evidenceId"
                findViewById<TextView>(R.id.trigger_type_text)?.text = "Trigger: $triggerType"
                findViewById<TextView>(R.id.location_text)?.text = "Location: $location"

                // Open app button
                findViewById<Button>(R.id.btn_open_app)?.setOnClickListener {
                    val launchIntent =
                        Intent(this@EmergencyOverlayService, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                    startActivity(launchIntent)
                }

                // Stop recording button
                findViewById<Button>(R.id.btn_stop_recording)?.setOnClickListener {
                    // Stop recording via bodyguard manager
                    val stopIntent = Intent(ACTION_HIDE_EMERGENCY)
                    stopIntent.setClass(
                        this@EmergencyOverlayService,
                        EmergencyOverlayService::class.java
                    )
                    startService(stopIntent)
                }

                // Dismiss button
                findViewById<Button>(R.id.btn_dismiss)?.setOnClickListener {
                    hideEmergencyOverlay()
                }
            }

            // Add overlay to window
            windowManager?.addView(overlayView, params)
            isShowing = true

            Log.i(TAG, "✓ Emergency overlay displayed")

        } catch (e: Exception) {
            Log.e(TAG, "Error showing emergency overlay", e)
        }
    }

    private fun hideEmergencyOverlay() {
        try {
            if (isShowing && overlayView != null) {
                windowManager?.removeView(overlayView)
                overlayView = null
                isShowing = false
                Log.i(TAG, "✓ Emergency overlay hidden")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error hiding emergency overlay", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hideEmergencyOverlay()
        Log.d(TAG, "Emergency Overlay Service destroyed")
    }
}
