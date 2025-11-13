# 🚀 Quick Test Guide - 5 Critical Tests (15 minutes)

## ⚡ **Run These 5 Tests Right Now**

These tests verify your most critical features. Follow exactly as written.

---

## 🔧 **SETUP (Do Once)**

### **1. Enable Developer Options**

```
Settings → About Phone → Tap "Build Number" 7 times
```

### **2. Enable USB Debugging**

```
Settings → Developer Options → USB Debugging → ON
```

### **3. Connect Device to Computer**

```bash
# Test connection
adb devices
# Should show your device
```

### **4. Start Logcat Monitoring**

```bash
# Open terminal and run:
adb logcat -c  # Clear old logs
adb logcat | grep -E "Stealth|Bodyguard|Aptos|Emergency"
```

Keep this terminal open during testing!

---

## 🧪 **TEST 1: Voice Detection "HELP" 3x** ⭐⭐⭐

**Time:** 3 minutes  
**Critical:** YES - Core feature

### **Steps:**

1. **Launch Calculator App**
    - Find "Calculator" in app drawer
    - Tap to open

2. **Enable Monitoring**
    - Tap 🔕 icon (top right)
    - Select "Enable"
    - Grant permissions if asked
    - Wait for green dot to appear

3. **Say "HELP" First Time**
    - Speak clearly and loudly: **"HELP"**
    - Check display: Should show **"HELP 1/3"** in yellow
    - ✅ PASS if counter shows 1/3
    - ❌ FAIL if nothing happens

4. **Wait 3 Seconds**
    - Count: 1... 2... 3...

5. **Say "HELP" Second Time**
    - Speak clearly and loudly: **"HELP"**
    - Check display: Should show **"HELP 2/3"** in orange
    - ✅ PASS if counter shows 2/3
    - ❌ FAIL if stuck at 1/3

6. **Wait 3 Seconds**
    - Count: 1... 2... 3...

7. **Say "HELP" Third Time**
    - Speak clearly and loudly: **"HELP"**
    - Check display: Should show **"HELP 3/3"** in red

8. **Verify Emergency Triggered**
    - ✅ Red emergency card appears
    - ✅ Text shows: "🚨 EMERGENCY ACTIVE"
    - ✅ Evidence ID displayed
    - ✅ Recording indicator visible

### **Expected Logcat Output:**

```
D/StealthBodyguard: 🗣️ Voice trigger detected: "HELP" (1/3)
... wait 3 seconds ...
D/StealthBodyguard: 🗣️ Voice trigger detected: "HELP" (2/3)
... wait 3 seconds ...
D/StealthBodyguard: 🗣️ Voice trigger detected: "HELP" (3/3)
W/StealthBodyguard: 🚨 HELP THRESHOLD REACHED! Triggering emergency!
I/StealthBodyguard: 📦 Evidence ID: EVIDENCE_1699...
I/StealthBodyguard: ✓ Audio recording started
```

### **Result:**

- [ ] ✅ PASS - Emergency triggered after 3x "HELP"
- [ ] ❌ FAIL - Describe what happened: ________________

---

## 🧪 **TEST 2: Background Detection + Auto-Launch** ⭐⭐⭐

**Time:** 2 minutes  
**Critical:** YES - Proves 24/7 protection

### **Steps:**

1. **Stop Recording from Test 1**
    - Tap "Stop Recording" button
    - Counter resets to 0

2. **Enable Background Service**
    - Tap 🔔 icon (if it shows 🔕, skip this)
    - Verify notification appears: "SHAKTI AI - Digital Bodyguard"

3. **Go to Home Screen**
    - Press Home button
    - Calculator closes
    - Notification should still be visible

4. **Open Another App**
    - Open Chrome or YouTube
    - Start watching a video (optional)

5. **Say "HELP" Twice from Background**
    - Say clearly: **"HELP"**
    - Wait 4 seconds
    - Say again: **"HELP"**

6. **Verify Auto-Launch**
    - ✅ Notification shows: "HELP detected: 1/2"
    - ✅ After 2nd HELP: "HELP detected: 2/2"
    - ✅ Calculator app AUTOMATICALLY LAUNCHES!
    - ✅ Emergency card appears
    - ✅ You didn't touch the phone - it launched itself!

### **Expected Logcat Output:**

```
I/StealthTrigger: 🗣️ Voice trigger detected: "HELP" (1/2)
I/StealthTrigger: Showing notification: HELP detected 1/2
... wait 4 seconds ...
I/StealthTrigger: 🗣️ Voice trigger detected: "HELP" (2/2)
W/StealthTrigger: 🚨 HELP TRIGGER DETECTED! Auto-launching calculator...
I/StealthTrigger: Calculator launched successfully
```

### **Result:**

- [ ] ✅ PASS - Calculator auto-launched from background
- [ ] ❌ FAIL - Describe what happened: ________________

---

## 🧪 **TEST 3: Audio & Video Recording** ⭐⭐

**Time:** 3 minutes  
**Critical:** YES - Evidence capture

### **Steps:**

1. **Trigger Emergency** (if not already active)
    - Say "HELP" 3 times as in Test 1

2. **Verify Recording Started**
    - Check emergency card shows: **"● REC"** in red
    - Check logcat for recording messages

3. **Speak for 10 Seconds**
    - Say anything: "Testing audio recording, one two three..."
    - Move device around (for video)
    - Count to 10

4. **Check Logcat for File Creation**

```
I/StealthBodyguard: ✓ Evidence audio recording started: EVIDENCE_xxx_audio.m4a
I/StealthBodyguard: ✓ Evidence video recording started: EVIDENCE_xxx_video.mp4
```

5. **Stop Recording**
    - Tap "Stop Recording" button
    - Recording stops

6. **Verify Files Created** (using adb)

```bash
# Check if files exist
adb shell "ls -lh /data/data/com.shakti.ai/files/evidence/"
```

### **Expected Files:**

```
-rw------- 1 u0_a123 u0_a123  245K ... EVIDENCE_1699xxx_audio.m4a
-rw------- 1 u0_a123 u0_a123  1.2M ... EVIDENCE_1699xxx_video.mp4
```

### **Result:**

- [ ] ✅ PASS - Both audio and video files created (size >0)
- [ ] ❌ FAIL - Describe what happened: ________________

---

## 🧪 **TEST 4: GPS Location Capture** ⭐⭐

**Time:** 1 minute  
**Critical:** YES - Location evidence

### **Steps:**

1. **Enable Location Services**
    - Settings → Location → ON
    - Set to "High Accuracy"

2. **Trigger Emergency**
    - Say "HELP" 3 times (or use existing emergency)

3. **Check Emergency Card**
    - Look for location information
    - Check logcat

### **Expected Logcat Output:**

```
D/StealthBodyguard: Location: 37.7749, -122.4194 (±15m)
I/StealthBodyguard: ✓ Location captured
```

### **Verify:**

- ✅ Latitude and longitude captured
- ✅ Accuracy shown (±Xm)
- ✅ Location makes sense (not 0.0, 0.0)

### **Result:**

- [ ] ✅ PASS - GPS location captured
- [ ] ❌ FAIL - Describe what happened: ________________

---

## 🧪 **TEST 5: Blockchain Anchoring** ⭐⭐

**Time:** 2 minutes  
**Critical:** YES - Immutable proof

### **Steps:**

1. **Ensure Internet Connected**
    - WiFi or mobile data ON

2. **Trigger Emergency** (if not already)
    - Say "HELP" 3 times

3. **Wait 10 Seconds**
    - Let system anchor to blockchain
    - Watch logcat

4. **Check for Blockchain Success**

### **Expected Logcat Output:**

```
I/StealthBodyguard: Anchoring evidence to blockchain: [hash]
I/DigitalBodyguard: ✓ Evidence successfully anchored to blockchain
I/DigitalBodyguard:   Transaction Hash: 0xabc123...
I/DigitalBodyguard:   Block Height: 12345678
I/DigitalBodyguard:   Evidence ID: EVIDENCE_1699...
```

### **Alternative (Offline Queue):**

```
W/AptosBlockchain: Blockchain not accessible, evidence will be queued
I/AptosBlockchain: Evidence queued for retry when online
```

5. **Check Emergency Card**
    - Should show evidence hash: `Hash: abc123...xyz789`

### **Result:**

- [ ] ✅ PASS - Evidence anchored (or queued if offline)
- [ ] ❌ FAIL - Describe what happened: ________________

---

## 📊 **RESULTS SUMMARY**

Fill this out after testing:

| Test | Status | Notes |
|------|--------|-------|
| 1. Voice Detection 3x | ⬜ Pass / ⬜ Fail | |
| 2. Background Auto-Launch | ⬜ Pass / ⬜ Fail | |
| 3. Audio/Video Recording | ⬜ Pass / ⬜ Fail | |
| 4. GPS Location | ⬜ Pass / ⬜ Fail | |
| 5. Blockchain Anchoring | ⬜ Pass / ⬜ Fail | |

**Overall:** ___/5 tests passed

---

## 🐛 **IF ANY TEST FAILS:**

### **Common Issues & Solutions:**

**Problem:** "HELP" not detected

- Solution: Speak louder, check microphone permission granted
- Check: Logcat shows "Sound detected but not speech-like"

**Problem:** Auto-launch doesn't work

- Solution: Check background service enabled (notification visible)
- Check: Battery optimization disabled for Calculator app

**Problem:** Recording files not created

- Solution: Check camera/audio permissions granted
- Check: Sufficient storage space available

**Problem:** Location not captured

- Solution: Enable location, set to high accuracy
- Check: Location permission granted

**Problem:** Blockchain fails

- Solution: Check internet connection
- Note: Offline queue should work - check logcat

---

## 🎯 **SUCCESS CRITERIA**

**Ready for Production if:**

- ✅ All 5 tests PASS
- ✅ No crashes during testing
- ✅ All features work as expected
- ✅ Logcat shows expected output

**If 5/5 tests pass:** 🎉 **YOUR APP IS READY TO LAUNCH!**

---

## 📝 **NEXT STEPS**

After completing these 5 tests:

1. **Share Results:** Copy logcat output and test results
2. **Report Issues:** Use bug template if tests fail
3. **Run Full Suite:** Do all 56 tests from `TEST_SUITE_COMPREHENSIVE.md`
4. **Performance Testing:** Measure response times
5. **Deploy:** If all pass, ready for production!

---

**Testing Time:** ~15 minutes  
**Critical Tests:** 5/5  
**Your app saves lives - test it well!** 🛡️✨
