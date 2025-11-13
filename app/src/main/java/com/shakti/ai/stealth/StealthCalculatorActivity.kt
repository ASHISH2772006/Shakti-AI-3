package com.shakti.ai.stealth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.shakti.ai.stealth.ui.HiddenCalculatorScreen

/**
 * Stealth Calculator Activity
 *
 * Entry point for hidden bodyguard mode.
 * Appears as a normal calculator app but runs threat detection in background.
 *
 * Launch methods:
 * 1. Direct app icon (if set as launcher)
 * 2. From main app settings
 * 3. Secret gesture (future enhancement)
 *
 * Security features:
 * - No indication of recording in UI
 * - No camera preview (stealth audio-only)
 * - Normal calculator appearance
 * - Real-time protection in background
 */
class StealthCalculatorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface {
                    HiddenCalculatorScreen()
                }
            }
        }
    }
}
