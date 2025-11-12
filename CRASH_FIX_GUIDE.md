# ShaktiAI 3.0 - Crash Fix Guide

## Problem

The app was closing immediately after showing the logo screen.

## Root Causes Identified & Fixed

### 1. **Missing GEMINI_API_KEY in local.properties**

- **Issue**: The app was trying to read `GEMINI_API_KEY` from `local.properties` but it wasn't
  defined
- **Fix**: Added `GEMINI_API_KEY=your_api_key_here` to `local.properties`
- **Impact**: BuildConfig will now generate properly without crashes

### 2. **RunAnywhere SDK Initialization Crashes**

- **Issue**: Direct imports of RunAnywhere SDK classes were causing crashes if AAR files had issues
- **Fix**:
    - Modified `ShaktiApplication.kt` to use reflection for SDK initialization
    - Wrapped all SDK calls in try-catch blocks
    - Made initialization non-blocking so app continues even if SDK fails
    - Modified `RunAnywhereAIService.kt` to use reflection instead of direct imports

### 3. **GeminiService BuildConfig Access**

- **Issue**: Direct access to `BuildConfig.GEMINI_API_KEY` could crash if BuildConfig wasn't
  generated
- **Fix**:
    - Used reflection to safely access BuildConfig fields
    - Added fallback to "DEMO_MODE" if API key is missing
    - Made RunAnywhereAIService nullable with proper null handling

### 4. **Improved Error Handling**

- **Fix**: All AI services now gracefully handle failures:
    - SDK initialization failures won't crash the app
    - Missing API keys won't crash the app
    - Demo mode responses when services are unavailable

## Changes Made

### Files Modified:

1. **local.properties**
    - Added: `GEMINI_API_KEY=your_api_key_here`

2. **app/build.gradle.kts**
    - Made GEMINI_API_KEY optional with default empty string
    - Added vector drawable support

3. **app/src/main/java/com/shakti/ai/ShaktiApplication.kt**
    - Removed direct SDK imports
    - Added reflection-based SDK initialization
    - Made initialization non-blocking with proper error handling
    - Used CoroutineScope with SupervisorJob for better lifecycle management

4. **app/src/main/java/com/shakti/ai/ai/GeminiService.kt**
    - Used reflection to access BuildConfig safely
    - Made runAnywhereService nullable
    - Added safe call operators (?.) for all SDK calls
    - Improved fallback handling

5. **app/src/main/java/com/shakti/ai/ai/RunAnywhereAIService.kt**
    - Removed direct SDK imports
    - Implemented all SDK operations using reflection
    - Made constructor private and used singleton pattern
    - Added proper null safety checks

## How to Run the App

### Option 1: With Gemini API (Recommended for Testing)

1. Get a free Gemini API key from [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Open `local.properties` file
3. Replace `your_api_key_here` with your actual API key:
   ```
   GEMINI_API_KEY=AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
   ```
4. Build and run the app

### Option 2: Demo Mode (No API Key Required)

1. Leave the API key as `your_api_key_here` in `local.properties`
2. Build and run the app
3. App will show demo responses and guide you to add API key or download on-device models

### Option 3: With On-Device AI (Privacy-First)

1. Run the app (with or without API key)
2. Go to Settings in the app
3. Download an AI model (choose based on your needs):
    - **SmolLM2 360M** (119 MB) - Fast, good for quick responses
    - **Qwen 2.5 0.5B** (374 MB) - Balanced quality and speed
    - **Llama 3.2 1B** (815 MB) - Best quality
4. Load the model
5. All AI features will work offline!

## What Works Now

✅ **App starts without crashing**

- Initialization errors are handled gracefully
- App continues to work even if SDK/API fails

✅ **All 8 AI Modules Available**

- 💬 Sathi AI (Mental Health)
- 🛡️ Guardian AI (Safety)
- ⚖️ Nyaya AI (Legal)
- 💰 Dhan Shakti (Finance)
- 👥 Sangam (Community)
- 📚 Gyaan (Education)
- ❤️ Swasthya (Health)
- 🔒 Raksha (DV Support)

✅ **Fallback System**

1. Tries RunAnywhere SDK first (on-device, private)
2. Falls back to Gemini API if SDK not ready
3. Shows demo mode if no services available

✅ **No Crashes**

- All potential crash points are wrapped in try-catch
- Proper null safety throughout
- Reflection prevents ClassNotFoundException crashes

## Build the App

### Using Android Studio:

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Click **Run** button or press **Shift+F10**

### Using Command Line:

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Install and run on connected device
./gradlew installDebug
```

## Troubleshooting

### If app still crashes:

1. **Check logcat** for error messages:
   ```bash
   adb logcat | grep -E "ShaktiApplication|GeminiService|AndroidRuntime"
   ```

2. **Clear app data**:
   ```bash
   adb shell pm clear com.shakti.ai
   ```

3. **Rebuild from scratch**:
   ```bash
   ./gradlew clean build --refresh-dependencies
   ```

4. **Check AAR files exist**:
    - Verify `app/libs/RunAnywhereKotlinSDK-release.aar` exists
    - Verify `app/libs/runanywhere-llm-llamacpp-release.aar` exists

### If Gemini API doesn't work:

- Verify API key is correct
- Check internet connection
- Ensure API key has Gemini API enabled

### If on-device models don't download:

- Check storage space
- Verify internet connection
- Try a smaller model first (SmolLM2 360M)

## Next Steps

1. **Add your Gemini API key** for cloud-based AI
2. **Test all 8 AI modules** to ensure they work
3. **Download an on-device model** for privacy and offline use
4. **Test with different scenarios** to ensure stability

## Support

If you continue to experience issues:

1. Check the logcat output for specific errors
2. Verify all dependencies are properly resolved
3. Ensure Android SDK and build tools are up to date
4. Try on a different device or emulator

---

**Status**: ✅ All critical crashes fixed. App should now start successfully!
