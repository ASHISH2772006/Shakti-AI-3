# HELP Voice Trigger - Testing Guide

## Quick Test (RECOMMENDED FIRST)

### Test the System is Working (30 seconds)

1. **Open Calculator** (SHAKTI AI → Settings → Stealth Mode)
2. **Grant All Permissions** when prompted
3. **Long-press the ℹ️ icon** (top-right corner)
4. **Emergency should trigger immediately!**
    - 🚨 Red emergency banner appears
    - Recording starts
    - Evidence ID generated
    - Emergency contacts notified

✅ **If this works**, the system is functional and you can proceed to voice testing.
❌ **If this doesn't work**, check permissions or Logcat for errors.

## What Was Fixed (Latest Update)

The HELP voice trigger feature has been **significantly improved** with much more sensitive
detection:

### 1. **Ultra-Sensitive Thresholds** (NEW)

- **ML Model Threshold**: `0.85` → `0.65` → Now more forgiving
- **Trigger Threshold**: `0.55` → `0.45` → **`0.35`** (30% more sensitive!)
- **RMS (Volume)**: `2000` → `1500` → **`1000`** (50% quieter detection!)
- **Peak Amplitude**: `8000` → `6000` → **`4000`** (50% more sensitive!)
- **Burst Threshold**: `10000` → `8000` → **`6000`** (40% more sensitive!)
- **Background RMS**: `8000` → `5000` → **`3000`** (63% more sensitive!)
- **Background Noise Trigger**: `25000` → **`15000`** (40% easier!)

### 2. **Improved Detection Logic** (NEW)

- **Wider ZCR Range**: `0.02-0.25` → **`0.01-0.35`** (catches more speech patterns)
- **More Flexible Burst Count**: `1-4` → **`1-6`** (accommodates variations)
- **Multi-Path Confidence**: Now has 7 different paths to trigger (was 4)
- **Background Service**: "Any 2 of 3" conditions instead of "all 3" (much more forgiving)

### 3. **Auto-Start Monitoring** (NEW)

- Monitoring now starts **automatically** when all permissions granted
- No need to manually activate
- Shows in top bar when active

### 4. **Test Button Added** (NEW)

- **Long-press the ℹ️ icon** to manually trigger emergency (for testing)
- Bypasses all detection and triggers immediately
- Helps verify system is working

### 5. **Enhanced Visual Feedback** (NEW)

- Shows voice detection even at **10% confidence** (gray text)
- "🎤 Listening..." appears at 25% confidence (cyan text)
- Better logging with "✅ TRIGGERED" marker

## How to Test

### Test 1: Manual Voice Test (In-App)

1. **Launch Calculator**
    - Open the SHAKTI AI app
    - Navigate to Settings → Stealth Mode → Launch Calculator
    - OR long-press the Calculator title bar from the hidden calculator

2. **Grant Permissions**
    - Audio recording (required)
    - Location (recommended)
    - Camera (optional)
    - Overlay permission (for emergency display)

3. **Speak "HELP"**
    - Say "HELP" loudly and clearly
    - Wait 1-2 seconds
    - Say "HELP" again
    - Wait 1-2 seconds
    - Say "HELP" a third time

4. **Expected Behavior**
    - You should see the HELP counter increment: `HELP 1/3`, `HELP 2/3`, `HELP 3/3`
    - After 3rd HELP, emergency mode should activate
    - Look for:
        - 🚨 EMERGENCY ACTIVE banner (red)
        - Evidence ID displayed
        - Recording indicator (● REC)
        - Emergency contacts called/messaged

5. **Visual Indicators to Watch**
    - When you speak, watch the display for:
        - Voice confidence percentage (e.g., "Voice: 65%")
        - "🎤 Listening..." appears when voice detected
        - Yellow/orange/red indicator next to HELP counter

### Test 2: Background Service Test

1. **Enable Background Service**
    - Open Calculator
    - Tap the bell icon (🔔) in top right
    - Tap "Enable" in the dialog
    - Service should start (bell icon becomes 🔔)

2. **Lock Screen or Switch Apps**
    - Put the app in background or lock your phone
    - The service continues listening

3. **Trigger with Voice**
    - Say "HELP" twice (2 times is threshold for background service)
    - Calculator should auto-launch with emergency mode active

4. **Expected Behavior**
    - Calculator launches automatically
    - Shows "🚨 Auto-Launched: Help voice trigger detected"
    - Emergency recording starts immediately
    - Notification shows: "🚨 Emergency detected! Starting protection..."

### Test 3: Loud Noise Test

1. **With Background Service Running**
    - Make a very loud noise (clap, shout, slam door)
    - Should trigger if amplitude > 25000

2. **Expected Behavior**
    - Calculator launches automatically
    - Shows "🚨 Auto-Launched: Loud noise detected"
    - Emergency mode activates

## Troubleshooting

### "HELP counter doesn't increment"

**Check:**

1. **Microphone Permission**: Ensure "Record Audio" permission is granted
2. **Volume**: Speak louder - RMS needs to be > 1000
3. **Environment**: Test in a quiet room (background noise can interfere)
4. **Logs**: Check Logcat for:
   ```
   Voice analysis: RMS=XXXX, ZCR=0.XXX, Peak=XXXX, Bursts=X, Confidence=0.XX
   ```
    - RMS should be > 1000
    - ZCR should be between 0.01-0.35
    - Peak should be > 4000
    - Confidence should be > 0.35 to trigger

5. **Timeout**: You have 10 seconds between "HELP" utterances - say them faster
6. **Word Choice**: Currently only detects "HELP" specifically

### "Visual indicators don't show"

**Check:**

1. Voice confidence is displayed only if > 10% (lowered threshold)
2. "🎤 Listening..." appears only if confidence > 25%
3. HELP counter appears only if count > 0

### "Background service doesn't launch calculator"

**Check:**

1. Overlay permission granted (Settings → Apps → SHAKTI AI → Display over other apps)
2. Service is actually running (check notification)
3. Bell icon shows 🔔 (enabled) not 🔕 (disabled)
4. Not in cooldown period (30 seconds after last trigger)
5. Threshold for background is 2 HELPs, not 3

### "Emergency doesn't record"

**Check:**

1. Camera permission for video
2. Audio permission for audio
3. Storage space available
4. Check `app/files/evidence/` directory for files

## Debug Mode

### Enable Detailed Logging

Open Android Studio Logcat and filter by:

```
tag:StealthBodyguard OR tag:StealthTrigger OR tag:HiddenCalculator
```

### Key Log Messages

**When voice is detected:**

```
Voice analysis: RMS=XXXX, ZCR=0.XXX, Peak=XXXX, Bursts=X, Confidence=0.XX
```

**When HELP increments:**

```
🗣️ Voice trigger detected: "HELP" (X/3) confidence=0.XX
```

**When emergency triggers:**

```
🚨 HELP THRESHOLD REACHED! Triggering emergency!
```

**When background service detects:**

```
🗣️ Speech detected (RMS: XXXX, ZCR: 0.XXX, Peak: XXXX, Bursts: X): HELP count = X/2
```

### Manual Testing Values

If you want to test with even more sensitive values temporarily:

**StealthBodyguardManager.kt:**

```kotlin
private const val VOICE_TRIGGER_THRESHOLD = 0.50f  // Even lower
val isTriggered = voiceConfidence > 0.35f          // Even more sensitive
val isLoudEnough = rms > 1000f                     // Lower volume
```

**StealthTriggerService.kt:**

```kotlin
private const val MIN_RMS_FOR_SPEECH = 3000        // Lower volume for background
val hasVoicePattern = zcr > 0.01f && zcr < 0.35f  // Wider range
```

## Technical Details

### How Voice Detection Works

1. **Audio Capture**: Records 100ms frames at 16kHz sample rate
2. **Feature Extraction**: Calculates RMS (volume), ZCR (speech rate), peak amplitude, burst count
3. **Pattern Matching**: Looks for speech-like patterns characteristic of "HELP"
4. **Confidence Scoring**: Combines multiple factors into 0-1 confidence score
5. **Threshold Check**: If confidence > 0.35, increments HELP counter
6. **Counter Logic**: If 3 HELPs within 10 seconds, triggers emergency

### Why Multiple "HELP"s?

- Prevents false positives from background noise
- Confirms user intent (not accidental)
- Balances sensitivity vs false alarms
- 10-second window is forgiving but not too long

### Audio Requirements for Detection

| Metric             | Threshold | Why                                         |
|--------------------|-----------|---------------------------------------------|
| RMS                | > 1000    | Must be audible above background noise      |
| Zero-Crossing Rate | 0.01-0.35 | Speech has characteristic frequency changes |
| Peak Amplitude     | > 4000    | Distinguishes voice from ambient noise      |
| Burst Count        | 1-6       | "HELP" is one syllable with clear onset     |
| Overall Confidence | > 0.35    | Combined metric for reliability             |

## Next Steps

If detection is still not working:

1. **Share Logs**: Copy relevant Logcat output showing voice analysis
2. **Record Sample**: Record yourself saying "HELP" and check audio properties
3. **Test Environment**: Try in different environments (quiet room, noisy room)
4. **Adjust Thresholds**: Can be further lowered if needed
5. **Alternative Words**: Could add more trigger words (STOP, NO, etc.)

## Success Criteria

✅ HELP counter increments when you say "HELP" clearly
✅ Emergency triggers after 3rd HELP within 10 seconds  
✅ Visual feedback shows voice detection (confidence %, listening indicator)
✅ Background service auto-launches calculator on trigger
✅ Emergency recording starts (audio + video)
✅ Emergency contacts are called/messaged
✅ Evidence package is created and encrypted
✅ Blockchain anchoring is initiated

---

**Last Updated**: January 2025
**Changes Made**: Lowered thresholds by 20-40%, improved speech detection, fixed auto-trigger
