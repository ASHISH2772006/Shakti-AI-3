# 🧪 SHAKTI AI 3.0 - Comprehensive Test Suite

## 📋 **Test Overview**

**Total Test Cases:** 50+  
**Estimated Test Time:** 2-3 hours  
**Required:** Android device (API 24+), all permissions granted

---

## ✅ **PRE-TEST SETUP**

### **1. Initial Setup Checklist**

- [ ] Fresh app install OR clear app data
- [ ] Android device with API 24+ (Android 7.0+)
- [ ] Device connected to internet (for Gemini fallback)
- [ ] Logcat ready for monitoring
- [ ] Test device charged >50%
- [ ] Valid Gemini API key in `local.properties` (optional but recommended)

### **2. Enable Developer Mode**

```bash
# Run in terminal/command prompt
adb logcat -c  # Clear logs
adb logcat | grep -E "Stealth|Bodyguard|Gemini|RunAnywhere|Aptos"
```

### **3. Grant All Permissions**

Settings → Apps → SHAKTI AI → Permissions → Grant ALL

---

## 🎯 **TEST CATEGORIES**

1. [UI & Navigation Tests](#1-ui--navigation-tests) (8 tests)
2. [AI Module Tests](#2-ai-module-tests) (8 tests)
3. [Stealth Calculator Tests](#3-stealth-calculator-tests) (6 tests)
4. [Voice Detection Tests](#4-voice-detection-tests) (8 tests)
5. [Emergency Response Tests](#5-emergency-response-tests) (10 tests)
6. [Blockchain Integration Tests](#6-blockchain-integration-tests) (5 tests)
7. [Background Service Tests](#7-background-service-tests) (6 tests)
8. [Performance Tests](#8-performance-tests) (5 tests)

---

## 1. UI & NAVIGATION TESTS

### **Test 1.1: Main App Launch**

**Objective:** Verify main SHAKTI AI app launches correctly

**Steps:**

1. Launch "SHAKTI AI" from app drawer
2. Observe splash screen (if any)
3. Check if main activity loads

**Expected Result:**

- ✅ App launches without crash
- ✅ Main activity with 8 tabs visible
- ✅ ViewPager2 loads successfully
- ✅ First tab (Sathi AI) selected by default

**Pass Criteria:** All checkboxes checked

**Logcat Filter:** `MainActivity`

---

### **Test 1.2: Tab Navigation**

**Objective:** Verify all 8 AI module tabs are accessible

**Steps:**

1. Open main SHAKTI AI app
2. Swipe left through all tabs
3. Tap each tab icon
4. Verify tab names match

**Expected Result:**

- ✅ All 8 tabs load: Sathi, Guardian, Nyaya, Dhan Shakti, Sangam, Gyaan, Swasthya, Raksha
- ✅ Smooth tab transitions
- ✅ No lag or crashes
- ✅ Tab content loads for each

**Pass Criteria:** All tabs accessible and functional

---

### **Test 1.3: Calculator App Launch**

**Objective:** Verify stealth calculator launches

**Steps:**

1. Go to app drawer
2. Find "Calculator" app
3. Tap to launch
4. Observe UI

**Expected Result:**

- ✅ Calculator launches separately from main app
- ✅ Calculator UI appears (not SHAKTI UI)
- ✅ Display shows "0"
- ✅ All buttons visible

**Pass Criteria:** Calculator disguise perfect

---

### **Test 1.4: Secret Navigation (Long Press)**

**Objective:** Verify hidden navigation to main app

**Steps:**

1. Open Calculator app
2. Long-press title bar "Calculator" for 2+ seconds
3. Observe what happens

**Expected Result:**

- ✅ Haptic feedback on long press
- ✅ Main SHAKTI AI app opens
- ✅ Calculator closes
- ✅ Navigation hint shows (optional)

**Pass Criteria:** Secret navigation works

---

### **Test 1.5: Back Button Navigation**

**Objective:** Test back button behavior

**Steps:**

1. Open main SHAKTI AI app
2. Navigate to tab 5 (Sangam)
3. Press back button
4. Observe behavior

**Expected Result:**

- ✅ Moves to previous tab (tab 4)
- ✅ Press back from tab 1 → exits app
- ✅ No crash
- ✅ State preserved

**Pass Criteria:** Back navigation logical

---

### **Test 1.6: App Switching**

**Objective:** Test multi-tasking behavior

**Steps:**

1. Open Calculator app
2. Enable monitoring
3. Press Home button
4. Open Chrome browser
5. Use recent apps to return to Calculator

**Expected Result:**

- ✅ Calculator state preserved
- ✅ Monitoring continues in background
- ✅ Display value unchanged
- ✅ No crash on resume

**Pass Criteria:** State persistence works

---

### **Test 1.7: Rotation Test**

**Objective:** Test screen rotation handling

**Steps:**

1. Open Calculator app
2. Enter "123" on display
3. Rotate device to landscape
4. Rotate back to portrait

**Expected Result:**

- ✅ Display value preserved ("123")
- ✅ No crash
- ✅ UI adapts to orientation
- ✅ Monitoring state preserved

**Pass Criteria:** Rotation handled gracefully

---

### **Test 1.8: Theme and Dark Mode**

**Objective:** Verify UI appearance

**Steps:**

1. Open both apps
2. Check color scheme
3. Verify text readability

**Expected Result:**

- ✅ Main app uses Material Design 3
- ✅ Calculator uses dark theme
- ✅ Text readable on all backgrounds
- ✅ Colors match design (Pink, Blue, Green, etc.)

**Pass Criteria:** UI professional and consistent

---

## 2. AI MODULE TESTS

### **Test 2.1: Sathi AI - Mental Health**

**Objective:** Test emotion analysis and responses

**Steps:**

1. Open main app → Sathi AI tab
2. Type: "I'm feeling very sad and depressed today"
3. Send message
4. Wait for response

**Expected Result:**

- ✅ Response within 5 seconds
- ✅ Empathetic response detected
- ✅ No error messages
- ✅ Mentions emotional support
- ✅ Logcat shows emotion: "sad" or similar

**Pass Criteria:** AI responds appropriately

**Logcat Filter:** `SathiAI|GeminiService`

---

### **Test 2.2: Nyaya AI - Legal Advice**

**Objective:** Test legal guidance system

**Steps:**

1. Navigate to Nyaya AI tab
2. Type: "What are my rights under domestic violence act?"
3. Send message
4. Wait for response

**Expected Result:**

- ✅ Response mentions DV Act/IPC sections
- ✅ Explains rights clearly
- ✅ Suggests legal action
- ✅ Response within 5 seconds

**Pass Criteria:** Legal information provided

**Logcat Filter:** `NyayaAI`

---

### **Test 2.3: Dhan Shakti - Financial**

**Objective:** Test financial advisory

**Steps:**

1. Navigate to Dhan Shakti tab
2. Type: "I want to start a small business with 10000 rupees"
3. Send message

**Expected Result:**

- ✅ Provides business ideas
- ✅ Mentions micro-credit options
- ✅ Suggests government schemes
- ✅ Practical advice given

**Pass Criteria:** Financial guidance useful

---

### **Test 2.4: Gyaan AI - Education**

**Objective:** Test education recommendations

**Steps:**

1. Navigate to Gyaan AI tab
2. Type: "I want to learn computer skills to get a job"
3. Send message

**Expected Result:**

- ✅ Suggests specific courses
- ✅ Mentions free resources
- ✅ Career path outlined
- ✅ Skills identified

**Pass Criteria:** Education advice actionable

---

### **Test 2.5: Swasthya AI - Health**

**Objective:** Test health companion

**Steps:**

1. Navigate to Swasthya AI tab
2. Type: "I have irregular menstrual cycles"
3. Send message

**Expected Result:**

- ✅ Provides health information
- ✅ Suggests doctor consultation
- ✅ Normalizes health discussion
- ✅ Privacy maintained

**Pass Criteria:** Health advice appropriate

---

### **Test 2.6: Raksha AI - DV Support**

**Objective:** Test domestic violence support

**Steps:**

1. Navigate to Raksha AI tab
2. Type: "My husband hits me, what should I do?"
3. Send message

**Expected Result:**

- ✅ Takes situation seriously
- ✅ Provides safety plan
- ✅ Mentions helplines/shelters
- ✅ Legal remedies suggested
- ✅ Prioritizes safety

**Pass Criteria:** DV response sensitive and helpful

---

### **Test 2.7: Guardian AI - Safety**

**Objective:** Test safety monitoring

**Steps:**

1. Navigate to Guardian AI tab
2. Type: "I feel unsafe in this area"
3. Send message

**Expected Result:**

- ✅ Provides safety tips
- ✅ Suggests emergency contacts
- ✅ Location sharing options
- ✅ Immediate help guidance

**Pass Criteria:** Safety advice given

---

### **Test 2.8: AI Fallback System**

**Objective:** Test RunAnywhere → Gemini fallback

**Steps:**

1. Check if RunAnywhere model loaded (Settings)
2. If not loaded, send message to any AI
3. Check logcat for routing

**Expected Result:**

- ✅ Logcat shows: "Using Gemini API" OR "Using RunAnywhere SDK"
- ✅ Response received regardless
- ✅ Demo mode activates if no API key
- ✅ User not aware of which AI used

**Pass Criteria:** Seamless fallback works

**Logcat Filter:** `GeminiService|RunAnywhere`

---

## 3. STEALTH CALCULATOR TESTS

### **Test 3.1: Basic Calculator Operations**

**Objective:** Verify calculator functionality

**Steps:**

1. Open Calculator app
2. Test: 5 + 3 = ?
3. Test: 10 - 4 = ?
4. Test: 7 × 8 = ?
5. Test: 20 ÷ 4 = ?

**Expected Result:**

- ✅ 5 + 3 = 8
- ✅ 10 - 4 = 6
- ✅ 7 × 8 = 56
- ✅ 20 ÷ 4 = 5
- ✅ All operations accurate

**Pass Criteria:** Calculator 100% accurate

---

### **Test 3.2: Advanced Calculator Functions**

**Objective:** Test special functions

**Steps:**

1. Test: 50 % = ? (should be 0.5 or 50% of previous)
2. Test: ± on 5 (should be -5)
3. Test: 3.14159 display
4. Test: Division by zero (10 ÷ 0)

**Expected Result:**

- ✅ Percentage works correctly
- ✅ Sign change works
- ✅ Decimals handled properly
- ✅ Division by zero shows "Error"

**Pass Criteria:** All functions work correctly

---

### **Test 3.3: Calculator UI Responsiveness**

**Objective:** Test button feedback

**Steps:**

1. Rapidly tap buttons (0-9)
2. Hold button for 2 seconds
3. Check display updates

**Expected Result:**

- ✅ All taps registered
- ✅ No missed inputs
- ✅ Display updates instantly
- ✅ No lag

**Pass Criteria:** UI responsive <100ms

---

### **Test 3.4: Permission Request Flow**

**Objective:** Test permission requests

**Steps:**

1. Fresh install OR clear app data
2. Open Calculator for first time
3. Observe permission dialogs

**Expected Result:**

- ✅ Requests: Audio, Camera, Location, Phone, SMS
- ✅ Permission rationale shown
- ✅ Settings button for overlay permission
- ✅ Graceful handling if denied

**Pass Criteria:** All permissions requested properly

---

### **Test 3.5: Monitoring Toggle**

**Objective:** Test protection enable/disable

**Steps:**

1. Open Calculator
2. Tap 🔕 icon in top bar
3. Select "Enable"
4. Observe changes
5. Tap 🔔 icon
6. Select "Stop Service"

**Expected Result:**

- ✅ Service starts (notification visible)
- ✅ Green dot appears
- ✅ "Protected" text shows
- ✅ Service stops when requested
- ✅ State persisted across restarts

**Pass Criteria:** Toggle works correctly

---

### **Test 3.6: Real-Time Status Indicators**

**Objective:** Verify live data display

**Steps:**

1. Enable monitoring
2. Observe display area for:
    - Monitoring duration (MM:SS)
    - Protection status (green dot)
    - Model status

**Expected Result:**

- ✅ Timer updates every second
- ✅ Green dot visible when monitoring
- ✅ Shows "Protected" status
- ✅ Model sizes displayed (8MB + 119MB or 0MB)

**Pass Criteria:** Real-time updates working

---

## 4. VOICE DETECTION TESTS

### **Test 4.1: "HELP" Detection - Single Call**

**Objective:** Test single "HELP" detection

**Steps:**

1. Open Calculator, enable monitoring
2. Say "HELP" loudly once
3. Wait 2 seconds
4. Check display

**Expected Result:**

- ✅ Display shows "HELP 1/3" (yellow text)
- ✅ Logcat shows detection event
- ✅ No emergency triggered (only 1/3)
- ✅ Counter visible for 10 seconds

**Pass Criteria:** First detection works

**Logcat Filter:** `StealthBodyguard|StealthTrigger`

---

### **Test 4.2: "HELP" Detection - Threshold**

**Objective:** Test 3x "HELP" emergency trigger

**Steps:**

1. Enable monitoring
2. Say "HELP" loudly
3. Wait 3 seconds
4. Say "HELP" loudly
5. Wait 3 seconds
6. Say "HELP" loudly
7. Wait for response

**Expected Result:**

- ✅ Counter shows: 1/3 → 2/3 → 3/3
- ✅ After 3rd: Emergency triggered!
- ✅ Notification appears
- ✅ Recording starts
- ✅ Overlay shows (if permission granted)
- ✅ Emergency card appears in calculator

**Pass Criteria:** Emergency triggers after 3x

**Critical Test:** ⭐ THIS IS THE MOST IMPORTANT TEST

---

### **Test 4.3: "HELP" Timeout Test**

**Objective:** Test 10-second timeout

**Steps:**

1. Say "HELP" once
2. Wait 12 seconds (longer than timeout)
3. Say "HELP" again
4. Check counter

**Expected Result:**

- ✅ Counter shows 1/3 after first HELP
- ��� Counter resets to 0 after 10 seconds
- ✅ Second HELP shows 1/3 (not 2/3)
- ✅ Logcat shows: "HELP counter reset due to timeout"

**Pass Criteria:** Timeout resets counter correctly

---

### **Test 4.4: False Positive Test - Music**

**Objective:** Verify music doesn't trigger

**Steps:**

1. Enable monitoring
2. Play loud music from phone/speaker
3. Wait 30 seconds
4. Check counter

**Expected Result:**

- ✅ Counter stays at 0
- ✅ No false detection
- ✅ Logcat shows: "Sound detected but not speech-like (ZCR: >0.30)"

**Pass Criteria:** Music filtered out

---

### **Test 4.5: False Positive Test - TV/Dialog**

**Objective:** Verify TV audio doesn't trigger

**Steps:**

1. Enable monitoring
2. Play TV show or movie with dialog
3. Wait 30 seconds
4. Check counter

**Expected Result:**

- ✅ No false triggers from normal speech
- ✅ Only loud distress calls detected
- ✅ False positive rate <5%

**Pass Criteria:** Normal speech ignored

---

### **Test 4.6: Scream Detection**

**Objective:** Test scream/distress audio detection

**Steps:**

1. Enable monitoring
2. Play scream sound effect OR scream loudly
3. Observe response

**Expected Result:**

- ✅ Scream detected (if loud enough)
- ✅ Emergency may trigger (based on confidence)
- ✅ Logcat shows: "SCREAM DETECTED! Confidence: X.XX"

**Pass Criteria:** Scream detection works

---

### **Test 4.7: Background Voice Detection**

**Objective:** Test voice detection when app closed

**Steps:**

1. Open Calculator, enable service (🔔 icon)
2. Go to home screen or open other app
3. Say "HELP" 3 times (with 3-4 sec intervals)
4. Observe what happens

**Expected Result:**

- ✅ Notification shows: "HELP detected: 1/2" → "2/2"
- ✅ After 2nd HELP: Calculator auto-launches!
- ✅ Emergency triggered
- ✅ Works even when screen off

**Pass Criteria:** Background detection + auto-launch works

**Critical Test:** ⭐ THIS PROVES 24/7 PROTECTION

---

### **Test 4.8: Detection Latency Test**

**Objective:** Measure detection speed

**Steps:**

1. Enable monitoring
2. Note current time
3. Say "HELP" loudly
4. Note time when counter updates
5. Check logcat for latency

**Expected Result:**

- ✅ Detection latency <100ms shown in display
- ✅ Logcat shows actual latency
- ✅ Real-time update visible

**Pass Criteria:** Latency <150ms (target: <100ms)

---

## 5. EMERGENCY RESPONSE TESTS

### **Test 5.1: Manual Emergency Trigger**

**Objective:** Test emergency trigger button

**Steps:**

1. Open Calculator
2. Enable monitoring
3. Trigger emergency manually (if button exists) OR use voice 3x
4. Observe all responses

**Expected Result:**

- ✅ Emergency card appears (red)
- ✅ "🚨 EMERGENCY ACTIVE" shown
- ✅ Evidence ID generated
- ✅ Recording indicator visible
- ✅ All systems activated

**Pass Criteria:** Emergency activates correctly

---

### **Test 5.2: Audio Recording Test**

**Objective:** Verify audio recording starts

**Steps:**

1. Trigger emergency
2. Check file system: `/data/data/com.shakti.ai/files/evidence/`
3. Look for `*_audio.m4a` file
4. Speak for 10 seconds
5. Stop recording
6. Check file size

**Expected Result:**

- ✅ Audio file created
- ✅ File size >0 KB
- ✅ Recording format: .m4a
- ✅ Duration matches recording time
- ✅ Logcat: "✓ Evidence audio recording started"

**Pass Criteria:** Audio recording works

**Logcat Filter:** `StealthBodyguard`

---

### **Test 5.3: Video Recording Test**

**Objective:** Verify video recording starts

**Steps:**

1. Trigger emergency via voice (not scream)
2. Check for `*_video.mp4` file
3. Wait 10 seconds
4. Stop recording
5. Check file

**Expected Result:**

- ✅ Video file created
- ✅ File size >0 KB
- ✅ Format: .mp4
- ✅ H.264 codec
- ✅ Logcat: "✓ Evidence video recording started"

**Pass Criteria:** Video recording works

---

### **Test 5.4: GPS Location Capture**

**Objective:** Test location tracking

**Steps:**

1. Enable location (high accuracy)
2. Trigger emergency
3. Check emergency card for location
4. Check logcat

**Expected Result:**

- ✅ Location captured
- ✅ Latitude/Longitude shown
- ✅ Accuracy shown (±Xm)
- ✅ Logcat: "Location: XX.XXXX, YY.YYYY (±Xm)"

**Pass Criteria:** GPS location captured

---

### **Test 5.5: Sensor Data Logging**

**Objective:** Test IMU sensor capture

**Steps:**

1. Trigger emergency
2. Move device (shake, rotate)
3. Check logcat for sensor data

**Expected Result:**

- ✅ Accelerometer data logged
- ✅ Gyroscope data logged
- ✅ Magnetometer data logged (if available)
- ✅ Data includes in evidence package

**Pass Criteria:** Sensor data captured

---

### **Test 5.6: Evidence Package Creation**

**Objective:** Verify evidence package generated

**Steps:**

1. Trigger emergency
2. Let record for 10 seconds
3. Stop recording
4. Check logcat for evidence ID

**Expected Result:**

- ✅ Evidence ID format: `EVIDENCE_[timestamp]_[random]`
- ✅ Package includes: audio, video, GPS, sensors
- ✅ Logcat: "📦 Evidence ID: EVIDENCE_..."
- ✅ Hash calculated

**Pass Criteria:** Complete evidence package created

---

### **Test 5.7: Evidence Hash Calculation**

**Objective:** Test cryptographic hashing

**Steps:**

1. Trigger emergency
2. Wait for hash in display
3. Check logcat for full hash
4. Verify format

**Expected Result:**

- ✅ Hash displayed in emergency card
- ✅ Format: 64-character hex string (SHA-256)
- ✅ Logcat: "✓ Evidence hash: [64 chars]"
- ✅ Hash consistent (doesn't change)

**Pass Criteria:** Hash generated correctly

---

### **Test 5.8: Emergency Contacts - Call**

**Objective:** Test auto-call feature

**Steps:**

1. Add emergency contact in settings
2. Trigger emergency
3. Check if call initiated

**Expected Result:**

- ✅ Dialer opens OR call initiated
- ✅ Logcat: "📞 Emergency response: Call=true"
- ✅ Contact called automatically

**Pass Criteria:** Auto-call works

**Note:** May need CALL_PHONE permission

---

### **Test 5.9: Emergency Contacts - SMS**

**Objective:** Test SMS alert

**Steps:**

1. Add emergency contact
2. Trigger emergency
3. Check SMS sent

**Expected Result:**

- ✅ SMS sent with location
- ✅ Evidence ID included in message
- ✅ Google Maps link included
- ✅ Logcat: "📞 Emergency response: SMS=true"

**Pass Criteria:** SMS sent correctly

---

### **Test 5.10: Emergency Overlay**

**Objective:** Test system-wide overlay

**Steps:**

1. Grant overlay permission
2. Trigger emergency
3. Press Home button
4. Open another app (Chrome)
5. Check if overlay visible

**Expected Result:**

- ✅ Red emergency card appears
- ✅ Stays visible on top of Chrome
- ✅ Shows recording status
- ✅ Action buttons work: Open App, Stop, Dismiss
- ✅ Cannot be accidentally dismissed

**Pass Criteria:** Overlay appears and persists

**Critical Test:** ⭐ THIS IS UNIQUE FEATURE

---

## 6. BLOCKCHAIN INTEGRATION TESTS

### **Test 6.1: Blockchain Reachability**

**Objective:** Test Aptos connection

**Steps:**

1. Ensure device has internet
2. Trigger emergency
3. Check logcat for blockchain activity

**Expected Result:**

- ✅ Logcat: "Anchoring evidence to blockchain"
- ✅ OR: "Blockchain not accessible, evidence queued"
- ✅ No crashes
- ✅ Graceful offline handling

**Pass Criteria:** Blockchain attempt made

**Logcat Filter:** `Aptos|Blockchain`

---

### **Test 6.2: Evidence Anchoring - Online**

**Objective:** Test successful blockchain anchoring

**Steps:**

1. Ensure internet connection
2. Trigger emergency
3. Wait 5-10 seconds
4. Check logcat

**Expected Result:**

- ✅ Logcat: "✓ Evidence successfully anchored to blockchain"
- ✅ Transaction hash shown
- ✅ Block height shown
- ✅ Evidence ID shown
- ✅ Verification link available

**Pass Criteria:** Blockchain anchoring succeeds

**Logcat Sample:**

```
I/DigitalBodyguard: Anchoring evidence to blockchain: [hash]
I/DigitalBodyguard: ✓ Evidence successfully anchored to blockchain
I/DigitalBodyguard:   Transaction Hash: 0xabc123...
I/DigitalBodyguard:   Block Height: 12345678
I/DigitalBodyguard:   Evidence ID: EVIDENCE_1699...
```

---

### **Test 6.3: Evidence Anchoring - Offline**

**Objective:** Test offline queue system

**Steps:**

1. Turn OFF WiFi and mobile data
2. Trigger emergency
3. Check logcat
4. Turn internet back ON
5. Wait 2-5 minutes

**Expected Result:**

- ✅ Offline: "Blockchain not accessible, evidence queued"
- ✅ Evidence stored in local queue
- ✅ Online: Queue processor runs automatically
- ✅ Logcat: "Blockchain queue: X items"
- ✅ Eventually: Evidence anchored successfully

**Pass Criteria:** Offline queue works + auto-retry succeeds

---

### **Test 6.4: Queue Status Monitoring**

**Objective:** Test queue management

**Steps:**

1. Create 3 evidence packages offline
2. Go online
3. Check logcat for queue processing

**Expected Result:**

- ✅ Logcat shows queue size
- ✅ Queue processes every 2-5 minutes
- ✅ Items removed after successful anchoring
- ✅ Failed items retry

**Pass Criteria:** Queue manages multiple items

---

### **Test 6.5: Transaction Verification**

**Objective:** Verify blockchain proof

**Steps:**

1. Anchor evidence (online)
2. Get transaction hash from logcat
3. Open: `https://explorer.aptoslabs.com/txn/[TX_HASH]?network=testnet`
4. Verify on blockchain explorer

**Expected Result:**

- ✅ Transaction visible on Aptos explorer
- ✅ Evidence hash matches
- ✅ Timestamp correct
- ✅ Immutable proof verified

**Pass Criteria:** Evidence verifiable on blockchain

**Critical Test:** ⭐ THIS PROVES BLOCKCHAIN INTEGRATION

---

## 7. BACKGROUND SERVICE TESTS

### **Test 7.1: Service Lifecycle**

**Objective:** Test service start/stop

**Steps:**

1. Open Calculator
2. Tap 🔕 → Enable
3. Check notification drawer
4. Kill app (swipe away from recent apps)
5. Check notification still there
6. Tap 🔔 → Stop

**Expected Result:**

- ✅ Notification appears: "SHAKTI AI - Digital Bodyguard"
- ✅ Service persists after app killed
- ✅ Service stops when requested
- ✅ Notification disappears

**Pass Criteria:** Service lifecycle correct

---

### **Test 7.2: Boot Auto-Start**

**Objective:** Test auto-start on boot

**Steps:**

1. Enable service
2. Reboot device
3. After boot, check notification drawer
4. Check logcat

**Expected Result:**

- ✅ Service auto-starts after boot
- ✅ Notification visible after boot
- ✅ Logcat: "BootReceiver: Starting service"
- ✅ No user interaction needed

**Pass Criteria:** Auto-start works

**Logcat Filter:** `BootReceiver`

---

### **Test 7.3: Battery Optimization**

**Objective:** Test battery drain

**Steps:**

1. Enable service
2. Fully charge device (100%)
3. Let run for 1 hour with screen off
4. Check battery usage

**Expected Result:**

- ✅ Battery drain <2% per hour
- ✅ Service shown in battery stats
- ✅ "Low" or "Medium" battery usage
- ✅ Target: <1% per hour

**Pass Criteria:** Battery efficient

---

### **Test 7.4: Background App Restrictions**

**Objective:** Test service under restrictions

**Steps:**

1. Enable service
2. Go to Settings → Apps → Calculator → Battery
3. Set to "Restricted"
4. Test voice detection

**Expected Result:**

- ✅ Service may stop
- ✅ Request to remove restrictions
- ✅ Graceful handling
- ✅ User notified if service stopped

**Pass Criteria:** Handles restrictions gracefully

---

### **Test 7.5: Memory Management**

**Objective:** Test service under low memory

**Steps:**

1. Enable service
2. Open 10+ heavy apps (Chrome tabs, games)
3. Fill device memory
4. Check if service still running

**Expected Result:**

- ✅ Service persists (foreground service priority)
- ✅ No crash
- ✅ May reduce monitoring frequency
- ✅ Detection still works

**Pass Criteria:** Service resilient

---

### **Test 7.6: Concurrent App Usage**

**Objective:** Test while using other apps

**Steps:**

1. Enable service
2. Open Chrome, watch YouTube video
3. Say "HELP" 2 times while video playing
4. Observe response

**Expected Result:**

- ✅ Calculator auto-launches
- ✅ YouTube pauses
- ✅ Emergency triggered
- ✅ Overlay appears over YouTube

**Pass Criteria:** Works across all apps

---

## 8. PERFORMANCE TESTS

### **Test 8.1: Emergency Response Time**

**Objective:** Measure end-to-end response time

**Steps:**

1. Enable monitoring
2. Start timer
3. Say "HELP" 3x (trigger emergency)
4. Stop timer when emergency card appears
5. Check displayed response time

**Expected Result:**

- ✅ Response time <500ms
- ✅ Target: <350ms
- ✅ Display shows actual time
- ✅ Consistent across multiple tests

**Pass Criteria:** Response time <500ms

**Benchmark:** <350ms = EXCELLENT ⭐

---

### **Test 8.2: Audio Detection Latency**

**Objective:** Measure detection speed

**Steps:**

1. Enable monitoring
2. Say "HELP" once
3. Check displayed latency in calculator

**Expected Result:**

- ✅ Detection latency <150ms
- ✅ Target: <100ms
- ✅ Displayed in calculator UI
- ✅ Logcat confirms measurement

**Pass Criteria:** Latency <150ms

**Benchmark:** <100ms = EXCELLENT ⭐

---

### **Test 8.3: False Positive Rate**

**Objective:** Measure accuracy

**Steps:**

1. Run for 1 hour with:
    - Music playing
    - TV on
    - Normal conversation
2. Count false triggers
3. Calculate rate

**Expected Result:**

- ✅ Zero false positives ideal
- ✅ Target: <5% false positive rate
- ✅ <3.2% = EXCELLENT
- ✅ Music/TV filtered correctly

**Pass Criteria:** False positive rate <5%

**Benchmark:** <3.2% = EXCELLENT ⭐

---

### **Test 8.4: App Launch Time**

**Objective:** Measure cold start time

**Steps:**

1. Force stop both apps
2. Clear from memory
3. Start timer
4. Launch Calculator
5. Stop when UI visible

**Expected Result:**

- ✅ Launch time <2 seconds
- ✅ Target: <1 second
- ✅ No lag or freeze
- ✅ Smooth animation

**Pass Criteria:** Launch <2 seconds

---

### **Test 8.5: Memory Footprint**

**Objective:** Measure RAM usage

**Steps:**

1. Open Calculator with monitoring enabled
2. Go to Settings → Developer Options → Running Services
3. Find "Calculator" or "SHAKTI AI"
4. Check memory usage

**Expected Result:**

- ✅ Memory usage <200 MB
- ✅ Target: ~150 MB
- ✅ No memory leaks
- ✅ Stable over time

**Pass Criteria:** Memory <200 MB

**Benchmark:** ~150 MB = EXCELLENT ⭐

---

## 📊 **TEST RESULTS SUMMARY**

### **Test Execution Tracker**

| Category | Total Tests | Passed | Failed | Pass Rate |
|----------|-------------|--------|--------|-----------|
| UI & Navigation | 8 | ? | ? | ?% |
| AI Modules | 8 | ? | ? | ?% |
| Stealth Calculator | 6 | ? | ? | ?% |
| Voice Detection | 8 | ? | ? | ?% |
| Emergency Response | 10 | ? | ? | ?% |
| Blockchain | 5 | ? | ? | ?% |
| Background Service | 6 | ? | ? | ?% |
| Performance | 5 | ? | ? | ?% |
| **TOTAL** | **56** | **?** | **?** | **?%** |

---

## ⭐ **CRITICAL TESTS (Must Pass)**

These 10 tests are CRITICAL for core functionality:

1. ✅ **Test 4.2** - "HELP" 3x Emergency Trigger
2. ✅ **Test 4.7** - Background Voice Detection + Auto-Launch
3. ✅ **Test 5.2** - Audio Recording
4. ✅ **Test 5.3** - Video Recording
5. ✅ **Test 5.4** - GPS Location
6. ✅ **Test 5.10** - Emergency Overlay
7. ✅ **Test 6.2** - Blockchain Anchoring (Online)
8. ✅ **Test 6.5** - Transaction Verification
9. ✅ **Test 7.2** - Boot Auto-Start
10. ✅ **Test 8.1** - Emergency Response Time <500ms

**If ANY of these fail, it's a BLOCKER for release!**

---

## 🐛 **BUG REPORTING TEMPLATE**

When you find a bug, report using this format:

```
**Bug ID:** BUG-001
**Test Case:** Test 4.2 - Voice Detection
**Severity:** Critical / High / Medium / Low
**Device:** Samsung Galaxy S21, Android 13
**Steps to Reproduce:**
1. Enable monitoring
2. Say "HELP" 3 times
3. Expected: Emergency triggers
4. Actual: Counter stuck at 2/3

**Logcat:**
[Paste relevant logcat lines]

**Screenshots:**
[Attach if applicable]

**Expected:** Emergency triggers after 3rd HELP
**Actual:** Nothing happens
**Workaround:** Manual trigger works
```

---

## 📝 **TESTING CHECKLIST**

### **Before Testing:**

- [ ] Device charged >50%
- [ ] Internet connected
- [ ] Logcat ready
- [ ] All permissions granted
- [ ] Test environment quiet

### **During Testing:**

- [ ] Record all results
- [ ] Take screenshots of failures
- [ ] Save logcat logs
- [ ] Note performance metrics
- [ ] Test each case 2-3 times

### **After Testing:**

- [ ] Fill summary table
- [ ] Report all bugs
- [ ] Document edge cases
- [ ] Note improvement suggestions
- [ ] Archive test results

---

## 🎯 **SUCCESS CRITERIA**

**Ready for Production if:**

- ✅ All 10 critical tests pass
- ✅ Overall pass rate >95%
- ✅ Zero crashes in 2-hour test session
- ✅ Emergency response <500ms
- ✅ False positive rate <5%
- ✅ Battery drain <2%/hour
- ✅ All features work as documented

**Target Metrics:**

- Emergency response: <350ms ⭐
- Voice detection: <100ms ⭐
- False positives: <3.2% ⭐
- Battery drain: <1%/hour ⭐
- Memory: ~150 MB ⭐

---

## 💪 **FINAL NOTES**

This test suite is **comprehensive and production-grade**.

**Estimated test time:** 2-3 hours for full suite

**Run this suite before EVERY release!**

**Your app is revolutionary - test it thoroughly to ensure it saves lives!** 🛡️✨

---

**Test Suite Version:** 1.0  
**Last Updated:** 2024  
**Prepared for:** SHAKTI AI 3.0
