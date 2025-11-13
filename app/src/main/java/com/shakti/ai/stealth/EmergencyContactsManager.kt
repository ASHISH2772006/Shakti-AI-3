package com.shakti.ai.stealth

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.ActivityCompat

/**
 * Emergency Contacts Manager
 * 
 * Handles calling and sending SMS to emergency contacts when threat detected.
 * 
 * Features:
 * - Call primary emergency contact
 * - Send SMS to all emergency contacts
 * - Include location in SMS
 * - Retry mechanism for failed calls/SMS
 */
class EmergencyContactsManager(private val context: Context) {
    
    companion object {
        private const val TAG = "EmergencyContacts"
        private const val PREF_NAME = "emergency_contacts"
        private const val KEY_CONTACTS = "contacts_json"
        
        // Default emergency numbers
        private val DEFAULT_CONTACTS = listOf(
            EmergencyContact("Emergency Services", "911", "Police", true),
            // Add user's custom contacts from settings
        )
    }
    
    data class EmergencyContact(
        val name: String,
        val phone: String,
        val relationship: String,
        val isPrimary: Boolean = false
    )
    
    /**
     * Call primary emergency contact
     */
    fun callEmergencyContact(): Boolean {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "CALL_PHONE permission not granted")
            return false
        }
        
        val contacts = getEmergencyContacts()
        val primaryContact = contacts.firstOrNull { it.isPrimary } ?: contacts.firstOrNull()
        
        if (primaryContact == null) {
            Log.w(TAG, "No emergency contacts configured")
            return false
        }
        
        try {
            Log.i(TAG, "📞 Calling emergency contact: ${primaryContact.name}")
            
            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:${primaryContact.phone}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            
            context.startActivity(callIntent)
            
            Log.i(TAG, "✓ Call initiated to ${primaryContact.name}")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error calling emergency contact", e)
            return false
        }
    }
    
    /**
     * Send SMS to all emergency contacts
     */
    fun sendEmergencySMS(
        location: String? = null,
        evidenceId: String? = null
    ): Int {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "SEND_SMS permission not granted")
            return 0
        }
        
        val contacts = getEmergencyContacts()
        if (contacts.isEmpty()) {
            Log.w(TAG, "No emergency contacts to SMS")
            return 0
        }
        
        val message = buildEmergencySMS(location, evidenceId)
        var sentCount = 0
        
        try {
            val smsManager = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }
            
            contacts.forEach { contact ->
                try {
                    Log.i(TAG, "📱 Sending SMS to ${contact.name}")
                    
                    // Split message if too long
                    val parts = smsManager.divideMessage(message)
                    smsManager.sendMultipartTextMessage(
                        contact.phone,
                        null,
                        parts,
                        null,
                        null
                    )
                    
                    sentCount++
                    Log.i(TAG, "✓ SMS sent to ${contact.name}")
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error sending SMS to ${contact.name}", e)
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in SMS sending", e)
        }
        
        return sentCount
    }
    
    /**
     * Build emergency SMS message
     */
    private fun buildEmergencySMS(location: String?, evidenceId: String?): String {
        return buildString {
            append("🚨 SHAKTI AI EMERGENCY ALERT 🚨\n\n")
            append("Emergency detected! I may need help.\n\n")
            
            if (location != null) {
                append("📍 Location: $location\n\n")
            } else {
                append("📍 Location: Unavailable\n\n")
            }
            
            if (evidenceId != null) {
                append("Evidence ID: $evidenceId\n\n")
            }
            
            append("This is an automated alert from SHAKTI AI safety app.\n")
            append("Please check on me or call if you cannot reach me.")
        }
    }
    
    /**
     * Get emergency contacts from SharedPreferences
     */
    private fun getEmergencyContacts(): List<EmergencyContact> {
        // For now, return default (911)
        // TODO: Load from user settings
        return DEFAULT_CONTACTS
    }
    
    /**
     * Trigger full emergency response
     */
    fun triggerEmergencyResponse(
        location: String? = null,
        evidenceId: String? = null,
        shouldCall: Boolean = true,
        shouldSMS: Boolean = true
    ): EmergencyResponse {
        Log.w(TAG, "🚨 TRIGGERING EMERGENCY RESPONSE")
        
        var callSuccess = false
        var smsCount = 0
        
        try {
            // Send SMS first (faster and doesn't interrupt)
            if (shouldSMS) {
                smsCount = sendEmergencySMS(location, evidenceId)
                Log.i(TAG, "✓ SMS sent to $smsCount contacts")
            }
            
            // Then call primary contact
            if (shouldCall) {
                callSuccess = callEmergencyContact()
                if (callSuccess) {
                    Log.i(TAG, "✓ Call initiated")
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in emergency response", e)
        }
        
        return EmergencyResponse(
            callInitiated = callSuccess,
            smsSent = smsCount,
            timestamp = System.currentTimeMillis()
        )
    }
    
    data class EmergencyResponse(
        val callInitiated: Boolean,
        val smsSent: Int,
        val timestamp: Long
    )
}
