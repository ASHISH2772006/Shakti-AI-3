# RunAnywhere SDK Integration - ShaktiAI 3.0

## Overview

ShaktiAI 3.0 now integrates the **RunAnywhere SDK** for privacy-first, on-device AI inference. This
eliminates the need for API keys and ensures all AI processing happens locally on the user's device.

## Important Note - SDK Installation

**The RunAnywhere Android SDK is currently in alpha and requires local setup.** The SDK repository
has been cloned to `runanywhere-sdks/` in the project root. To use it, you have two options:

### Option 1: Use Pre-built AAR Files (Recommended)

Download the AAR files directly from the RunAnywhere GitHub releases:

1.
Download [RunAnywhereKotlinSDK-release.aar](https://github.com/RunanywhereAI/runanywhere-sdks/releases/download/android/v0.1.3-alpha/RunAnywhereKotlinSDK-release.aar)
2.
Download [runanywhere-llm-llamacpp-release.aar](https://github.com/RunanywhereAI/runanywhere-sdks/releases/download/android/v0.1.3-alpha/runanywhere-llm-llamacpp-release.aar)
3. Place both AAR files in `app/libs/`
4. Update `app/build.gradle.kts`:

```kotlin
dependencies {
    // Core SDK
    implementation(files("libs/RunAnywhereKotlinSDK-release.aar"))
    
    // LLM Module (includes llama.cpp)
    implementation(files("libs/runanywhere-llm-llamacpp-release.aar"))
    
    // Required dependency
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

### Option 2: Build Locally

If you want to build the SDK yourself:

```bash
cd runanywhere-sdks/sdk/runanywhere-kotlin
./scripts/sdk.sh build
./scripts/sdk.sh publish

# Then add mavenLocal() to repositories in settings.gradle.kts
```

## What is RunAnywhere SDK?

RunAnywhere SDK is a privacy-first, on-device AI SDK that brings powerful language models directly
to Android applications. It provides:

- **On-Device Processing**: All AI inference runs locally, no data leaves the device
- **Privacy-First**: No API calls, no data collection, complete privacy
- **Multiple Models**: Support for various GGUF models from HuggingFace
- **Streaming Support**: Real-time token-by-token generation
- **Model Management**: Download, load, and manage AI models
- **Optimized Performance**: 7 ARM64 CPU variants for optimal speed

## Architecture Changes

### Before (Gemini API Only)

```
User → GeminiService → Gemini API (Cloud) → Response
       ↓
    Requires API Key
    Data sent to cloud
    Privacy concerns
```

### After (RunAnywhere SDK + Gemini Fallback)

```
User → GeminiService → RunAnywhereAIService → On-Device Model → Response
                    ↓ (fallback if no model loaded)
                    → Gemini API (Cloud) → Response
                    
Priority:
1. RunAnywhere SDK (on-device, privacy-first)
2. Gemini API (cloud-based, requires API key)
```

## Key Components

### 1. ShaktiApplication.kt

- Initializes RunAnywhere SDK on app startup
- Registers LlamaCpp service provider
- Registers multiple AI models
- Scans for previously downloaded models

### 2. RunAnywhereAIService.kt

- Unified AI service for all ShaktiAI modules
- Provides specialized system prompts for each AI module
- Supports both streaming and non-streaming generation
- Handles model management (download, load, unload)

### 3. GeminiService.kt (Updated)

- Now acts as a bridge between RunAnywhere and Gemini
- Uses RunAnywhere SDK as primary AI service
- Falls back to Gemini API if no model is loaded
- Provides seamless integration with existing code

## Registered AI Models

The following models are pre-registered and available for download:

| Model | Size | Quality | Speed | Use Case |
|-------|------|---------|-------|----------|
| SmolLM2 360M Q8_0 | 119 MB | Basic | Very Fast | Testing, simple Q&A |
| LiquidAI LFM2 350M | 210 MB | Basic | Fast | Quick responses |
| Qwen 2.5 0.5B Instruct | 374 MB | Good | Fast | General conversations |
| Llama 3.2 1B Instruct | 815 MB | Better | Medium | Quality conversations |
| Qwen 2.5 1.5B Instruct | 1.2 GB | Best | Slower | High-quality responses |

**Recommendation**: Start with **Qwen 2.5 0.5B Instruct** for best balance of quality and speed.

## How to Use

### For Users (UI to be implemented)

1. **Download a Model**
    - Go to Settings → AI Models
    - Select a model from the list
    - Tap "Download" and wait for completion
    - Model is saved locally on device

2. **Load a Model**
    - After download, tap "Load Model"
    - Model is loaded into memory (takes a few seconds)
    - Status shows "Model Loaded: [Model Name]"

3. **Use AI Features**
    - All AI modules now work offline
    - No API key needed
    - Complete privacy (no data leaves device)

### For Developers

#### Download a Model

```kotlin
import com.shakti.ai.ai.RunAnywhereAIService
import kotlinx.coroutines.launch

val aiService = RunAnywhereAIService.getInstance(context)

// Download with progress tracking
viewModelScope.launch {
    aiService.downloadModel("qwen2.5-0.5b-instruct-q6_k").collect { progress ->
        val percentage = (progress * 100).toInt()
        println("Download progress: $percentage%")
        // Update UI with progress
    }
}
```

#### Load a Model

```kotlin
viewModelScope.launch {
    val success = aiService.loadModel("qwen2.5-0.5b-instruct-q6_k")
    if (success) {
        println("Model loaded successfully")
    }
}
```

#### Generate Text

```kotlin
// Non-streaming (wait for complete response)
viewModelScope.launch {
    val response = aiService.callSathiAI("I'm feeling anxious today")
    println("Response: $response")
}

// Streaming (real-time token generation)
viewModelScope.launch {
    var fullResponse = ""
    aiService.callSathiAIStream("Tell me about mental health").collect { token ->
        fullResponse += token
        // Update UI with each token
        updateUI(fullResponse)
    }
}
```

#### Get Available Models

```kotlin
viewModelScope.launch {
    val models = aiService.getAvailableModels()
    models.forEach { model ->
        println("${model.name} - ${model.size / 1024 / 1024} MB - Downloaded: ${model.isDownloaded}")
    }
}
```

#### Check Current Model

```kotlin
val currentModel = aiService.getCurrentModel()
if (currentModel != null) {
    println("Current model: $currentModel")
} else {
    println("No model loaded")
}
```

## AI Module Specializations

Each ShaktiAI module has a specialized system prompt:

### 1. Sathi AI (Emotional Support)

- Compassionate mental health companion
- Culturally sensitive support
- Hindi, English, and regional languages
- Temperature: 0.8 (more creative/empathetic)
- Max Tokens: 300

### 2. Guardian AI (Safety Monitoring)

- Safety monitoring and guidance
- Emergency response planning
- Quick, actionable instructions
- Temperature: 0.5 (more focused/direct)
- Max Tokens: 150

### 3. Nyaya AI (Legal Assistance)

- Legal advice for women's rights
- IPC sections and explanations
- Simple, jargon-free language
- Temperature: 0.7
- Max Tokens: 400

### 4. DhanShakti AI (Financial Literacy)

- Financial independence advisor
- Government schemes (Mudra, Stand-Up India)
- Budgeting and investment advice
- Temperature: 0.7
- Max Tokens: 350

### 5. Gyaan AI (Educational Content)

- Skill development advisor
- Career recommendations
- Scholarship finder
- Temperature: 0.7
- Max Tokens: 300

### 6. Swasthya AI (Health Monitoring)

- Reproductive health companion
- Menstrual cycle tracking
- Wellness and nutrition advice
- Temperature: 0.7
- Max Tokens: 300

### 7. Raksha AI (Pattern Recognition)

- Domestic violence support
- Safety planning
- Emergency resources
- Temperature: 0.6
- Max Tokens: 250

### 8. Sangam AI (Community Connections)

- Safe community recommendations
- Support networks
- Local resources
- Temperature: 0.7
- Max Tokens: 250

## Benefits

### Privacy & Security

- ✅ **Complete Privacy**: No data leaves device
- ✅ **No API Keys**: No need for cloud service accounts
- ✅ **Offline Capability**: Works without internet
- ✅ **Data Security**: All processing is local
- ✅ **No Tracking**: No analytics or data collection

### Cost & Accessibility

- ✅ **Free**: No API costs or subscriptions
- ✅ **Accessible**: Works for all users equally
- ✅ **No Quotas**: Unlimited usage
- ✅ **No Rate Limits**: Use as much as needed

### Performance

- ✅ **Fast Responses**: Optimized for ARM64 CPUs
- ✅ **Streaming Support**: Real-time token generation
- ✅ **7 CPU Variants**: Automatically selects best variant
- ✅ **Multiple Model Sizes**: Choose based on device capability

### User Experience

- ✅ **Always Available**: No network dependency
- ✅ **Consistent Quality**: Same experience for all users
- ✅ **No Setup Required**: Models downloaded once
- ✅ **Transparent**: Users know data stays local

## Technical Details

### Dependencies Added

```kotlin
// Core SDK
implementation(files("libs/RunAnywhereKotlinSDK-release.aar"))
    
// LLM Module (includes llama.cpp)
implementation(files("libs/runanywhere-llm-llamacpp-release.aar"))
    
// Required dependency
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

### Build Configuration Updates

- Updated to Java 17 (required for SDK)
- Updated compileSdk to 36
- Updated targetSdk to 36
- Added packaging excludes for META-INF conflicts
- Enabled largeHeap for AI models

### Manifest Changes

- Added `ShaktiApplication` as application class
- Enabled `largeHeap="true"` for better model loading

## Model Storage

Models are stored in the app's internal storage:

```
/data/data/com.shakti.ai/files/models/
├── smollm2-360m-q8_0.gguf
├── qwen2.5-0.5b-instruct-q6_k.gguf
└── ... (other downloaded models)
```

## Memory Considerations

| Model Size | RAM Required | Recommended Device |
|------------|--------------|-------------------|
| 119 MB | ~500 MB | Any Android 7.0+ device |
| 374 MB | ~1 GB | 2+ GB RAM devices |
| 815 MB | ~2 GB | 4+ GB RAM devices |
| 1.2 GB | ~3 GB | 6+ GB RAM devices |

**Note**: Enable `largeHeap="true"` in AndroidManifest.xml (already done)

## Fallback Mechanism

The system uses a smart fallback mechanism:

1. **Try RunAnywhere SDK**: If a model is loaded, use on-device inference
2. **Check Gemini API**: If no model loaded, check for Gemini API key
3. **Use Gemini API**: If API key available, use cloud-based inference
4. **Demo Mode**: If neither available, show guidance message

This ensures the app always works, even if:

- User hasn't downloaded a model yet
- User doesn't have a Gemini API key
- Device doesn't have enough storage

## Testing the Integration

### 1. Test Model Download

```kotlin
viewModelScope.launch {
    val aiService = RunAnywhereAIService.getInstance(context)
    aiService.downloadModel("smollm2-360m-q8_0").collect { progress ->
        println("Progress: ${(progress * 100).toInt()}%")
    }
}
```

### 2. Test Model Load

```kotlin
viewModelScope.launch {
    val success = aiService.loadModel("smollm2-360m-q8_0")
    println("Load success: $success")
}
```

### 3. Test AI Generation

```kotlin
viewModelScope.launch {
    val response = aiService.callSathiAI("Hello, how are you?")
    println("Response: $response")
}
```

### 4. Test Streaming

```kotlin
viewModelScope.launch {
    aiService.callSathiAIStream("Tell me a story").collect { token ->
        print(token)
    }
}
```

## Troubleshooting

### Model Download Fails

- Check internet connection
- Ensure sufficient storage space
- Verify HuggingFace URL is accessible

### Model Load Fails

- Ensure model is fully downloaded
- Check available RAM (free up memory)
- Try a smaller model
- Restart the app

### Generation is Slow

- Normal for on-device inference
- Try a smaller model (SmolLM2 360M)
- Close other apps to free memory
- Ensure largeHeap is enabled

### App Crashes

- Reduce model size
- Enable largeHeap in manifest (done)
- Close background apps
- Restart device

## Future Enhancements

### Planned Features

- [ ] Model management UI in Settings
- [ ] Download progress notifications
- [ ] Auto-select best model based on device
- [ ] Model size recommendations
- [ ] Background model downloads
- [ ] Multiple model switching
- [ ] Model performance metrics
- [ ] Offline indicator in UI

### Potential Models to Add

- Medical domain models (for Swasthya AI)
- Legal domain models (for Nyaya AI)
- Financial models (for DhanShakti AI)
- Multilingual models (Hindi, Tamil, Bengali)

## Additional Resources

- **RunAnywhere SDK GitHub**: https://github.com/RunanywhereAI/runanywhere-sdks
- **Android SDK Docs
  **: https://github.com/RunanywhereAI/runanywhere-sdks/blob/main/QUICKSTART_ANDROID.md
- **HuggingFace Models**: https://huggingface.co/models?library=gguf
- **llama.cpp**: https://github.com/ggerganov/llama.cpp

## Support

For issues related to:

- **RunAnywhere SDK**: Open issue on GitHub
- **ShaktiAI Integration**: Contact development team
- **Model Selection**: Refer to model comparison table above

---

**Made with ❤️ for women's safety and empowerment**

**Privacy-first AI, always on-device, always protecting you**
