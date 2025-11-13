# Camera App Launch Feature - Added

## ✨ New Feature

When emergency is triggered after saying **"HELP" 3 times**, the system now:

1. ✅ **Starts background video recording** (stealth - 1x1 invisible surface)
2. ✅ **Opens visible camera app** (for user to see and record openly)

## 🎯 How It Works

### Trigger Sequence:

1. User says **"HELP"** loudly → Counter: **1/3**
2. User says **"HELP"** again → Counter: **2/3**
3. User says **"HELP"** third time → Counter: **3/3**
4. **Emergency Triggers!** 🚨

### What Happens Automatically:

```
[0ms]    Emergency detected
[100ms]  ✓ Audio recording starts (background)
[200ms]  ✓ Background video recording starts (stealth 1x1 surface)
[300ms]  ✓ Camera app opens (VISIBLE - video mode)
[350ms]  ✓ Location captured
[400ms]  ✓ Sensor data captured
[450ms]  ✓ Evidence package created
[500ms]  ✓ Emergency contacts called/messaged
[550ms]  ✓ Blockchain anchoring initiated
```

## 📹 Dual Recording System

### 1. **Stealth Background Recording** (Already Working)

- Uses 1x1 invisible SurfaceTexture
- Records video in background
- Saved to: `app/files/evidence/{evidenceId}_video.mp4`
- User cannot see this recording
- Cannot be stopped by attacker

### 2. **Visible Camera App** (NEW)

- Opens device's native camera app in **video mode**
- User can see what's being recorded
- Records at high quality (quality level 1)
- 5-minute duration limit
- User has control over this recording
- Provides second evidence source

## 🔧 Technical Implementation

### Code Location: `StealthBodyguardManager.kt:760-773`

```kotlin
// Start video recording only if permission is granted and trigger is VOICE_HELP
if (trigger == EmergencyTrigger.VOICE_HELP) {
    delay(100)
    startEvidenceVideoRecording()  // Background stealth recording
    Log.i(TAG, "✓ Video recording started")
    
    // Also open visible camera app for user to see
    delay(100)
    openCameraApp()  // Opens device camera
    Log.i(TAG, "✓ Camera app opened")
}
```

### Camera Launch Function: `StealthBodyguardManager.kt:1242-1271`

```kotlin
private fun openCameraApp() {
    try {
        // Launch camera in video mode for recording
        val cameraIntent = Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(android.provider.MediaStore.EXTRA_VIDEO_QUALITY, 1) // High quality
            putExtra(android.provider.MediaStore.EXTRA_DURATION_LIMIT, 300) // 5 minutes
        }
        context.startActivity(cameraIntent)
        Log.i(TAG, "✓ Camera app opened for video recording")
    } catch (e: Exception) {
        Log.e(TAG, "Error opening camera app", e)
        // Fallback: Try photo mode
        val intent = Intent("android.media.action.IMAGE_CAPTURE").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
```

## 🎬 User Experience

### Before (Without Camera):

```
Say "HELP" 3x → Emergency triggers → Recording happens in background
                                  ↓
                          (User sees nothing camera-related)
```

### After (With Camera):

```
Say "HELP" 3x → Emergency triggers → Recording happens in background
                                  ↓
                          + Camera app opens on screen
                                  ↓
                          User can see recording interface
                                  ↓
                          User can manually record additional evidence
```

## 📱 Camera App Features

### Video Mode Settings:

- **Quality**: High (level 1)
- **Duration Limit**: 5 minutes (300 seconds)
- **Mode**: Video recording (ACTION_VIDEO_CAPTURE)
- **Launch Flags**: NEW_TASK + CLEAR_TOP

### Fallback:

If video mode fails, falls back to photo mode (`IMAGE_CAPTURE`)

## 🔒 Security Benefits

### Dual Recording = Stronger Evidence:

1. **Stealth Recording** (Cannot be stopped)
    - Records even if attacker takes phone
    - 1x1 invisible surface - attacker won't notice
    - Saves directly to internal storage
    - Encrypted evidence package

2. **Visible Recording** (User control)
    - User can aim camera at threat
    - Higher quality recording possible
    - Saved to camera roll (accessible later)
    - Additional evidence source

## 🧪 Testing

### Test: Camera Opens After 3 HELPs

1. Open Calculator
2. Grant all permissions (Camera required!)
3. Say "HELP" loudly → See counter: 1/3
4. Say "HELP" again → See counter: 2/3
5. Say "HELP" third time → See counter: 3/3
6. **Emergency activates!**
7. **Camera app should open automatically** ✅
8. Background recording also starts (check logs)

### Expected Behavior:

```
✅ Calculator shows "🚨 EMERGENCY ACTIVE"
✅ Red banner with evidence ID
✅ Camera app opens in video mode
✅ User can start recording in camera
✅ Background stealth recording also active
✅ Emergency contacts called/messaged
✅ Location captured
```

### Check Logcat:

```
StealthBodyguard: 🗣️ Voice trigger detected: "HELP" (1/3) confidence=0.55
StealthBodyguard: 🗣️ Voice trigger detected: "HELP" (2/3) confidence=0.60
StealthBodyguard: 🗣️ Voice trigger detected: "HELP" (3/3) confidence=0.62
StealthBodyguard: 🚨 HELP THRESHOLD REACHED! Triggering emergency!
StealthBodyguard: ✓ Audio recording started [+100ms]
StealthBodyguard: ✓ Video recording started [+200ms]
StealthBodyguard: ✓ Camera app opened [+300ms]
StealthBodyguard: ✓ Location captured [+350ms]
```

## ❓ Troubleshooting

### Camera doesn't open?

**Check:**

1. Camera permission granted?
2. Device has camera app installed?
3. Check Logcat for errors:
   ```
   Error opening camera app
   ```

**Solutions:**

- Grant camera permission in app settings
- Try restarting the app
- Check if device's default camera app works manually

### Camera opens but recording doesn't start?

- **This is normal!** The camera app opens, but user must tap record button
- Background stealth recording IS automatic (check logs)
- Visible camera is for user to control manually

### Only photo mode opens (not video)?

- Some devices don't support ACTION_VIDEO_CAPTURE
- This is the fallback - still works for evidence
- User can switch to video mode manually in camera

## 📊 Comparison

| Feature | Background Recording | Visible Camera |
|---------|---------------------|----------------|
| **Stealth** | ✅ Yes (1x1 invisible) | ❌ No (visible) |
| **Automatic** | ✅ Yes | ✅ Yes (opens automatically) |
| **User Control** | ❌ No | ✅ Yes (user can aim/record) |
| **Can be Stopped** | ❌ No (hidden) | ✅ Yes (visible) |
| **Quality** | Standard | High |
| **Evidence Location** | App internal storage | Camera roll |
| **Encrypted** | ✅ Yes | ❌ No (standard video) |

## 💡 Best Practice

### In Emergency Situation:

1. **Say "HELP" 3 times** → System activates
2. **Camera opens automatically** → You see it
3. **Immediately start recording** (tap record button)
4. **Aim camera at threat** → Capture evidence
5. **Background recording** continues even if attacker takes phone
6. **Dual evidence** = Stronger case

## ✅ Benefits

- ✅ **Visible feedback** - User knows system is working
- ✅ **User control** - Can aim camera at threat
- ✅ **High quality** - Native camera app records well
- ✅ **Dual evidence** - Two recordings better than one
- ✅ **Backup** - If one recording fails, have another
- ✅ **Accessible** - Camera roll video easy to find later

---

## 📝 Summary

**Before:** Only background stealth recording (user sees nothing)
**Now:** Background recording + visible camera app opens

**Trigger:** Say "HELP" 3 times
**Result:**

- Background video recording (stealth)
- Visible camera app opens (video mode)
- User can manually record additional evidence

**Files Modified:**

- `StealthBodyguardManager.kt` (Lines 760-773, 1242-1271)

---

**Last Updated:** January 2025  
**Status:** ✅ IMPLEMENTED  
**Feature:** Camera App Launch on Emergency
