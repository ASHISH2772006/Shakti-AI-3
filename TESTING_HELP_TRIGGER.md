# Testing the HELP Voice Trigger

## Quick Test (5 minutes)

### Setup

1. Open the SHAKTI AI app
2. Grant all permissions (Audio, Camera, Location)
3. Navigate to the Calculator screen (stealth mode)
4. Ensure you're in a quiet environment

### Test 1: Basic Voice Detection

**Goal**: Verify the system detects your voice

1. Look at the calculator display area (below the main number)
2. Say "HELLO" or any word loudly
3. **Expected**: You should see "Voice: 30-45%" appear briefly in gray/orange
4. **If not working**:
    - Speak louder
    - Check microphone permission is granted
    - Try moving closer to phone

### Test 2: HELP Counter

**Goal**: Verify the HELP counter increments

1. Say "HELP" loudly and clearly
2. **Expected**:
    - "Voice: 55-75%" appears in yellow/red
    - "HELP 1/3" counter appears below
3. Wait 2-3 seconds
4. Say "HELP" again loudly
5. **Expected**: "HELP 2/3" counter updates
6. **If counter doesn't increment**:
    - Make sure you see "Voice: >55%" (must be above 55%)
    - Speak louder and more clearly
    - Ensure no background music/TV

### Test 3: Emergency Trigger

**Goal**: Verify emergency mode activates

1. Say "HELP" clearly 3 times (with 2-3 seconds between each)
    - "HELP" (pause)
    - "HELP" (pause)
    - "HELP"
2. **Expected**:
    - Red "EMERGENCY ACTIVE" card appears
    - Audio recording starts
    - Emergency overlay shows
    - Contacts are notified (if configured)
3. Click "Stop Recording" to end test

### Test 4: Timeout Test

**Goal**: Verify counter resets after 10 seconds

1. Say "HELP" once (counter: 1/3)
2. Wait 11 seconds (do nothing)
3. **Expected**: Counter disappears (reset to 0)
4. Say "HELP" again
5. **Expected**: Counter shows 1/3 (not 2/3)

### Test 5: Background Service

**Goal**: Verify auto-launch from background

1. Click the bell icon (🔔) in top-right
2. Click "Enable" in dialog
3. **Expected**: Bell changes to 🔔 (enabled)
4. Press Home button (app goes to background)
5. Say "HELP" twice loudly (2-3 seconds apart)
6. **Expected**:
    - Calculator app launches automatically
    - Emergency recording starts
    - Shows "Auto-Launched: Help voice trigger detected"

## Troubleshooting

### "Voice: X%" never appears

- **Check permissions**: Settings → Apps → SHAKTI AI → Permissions → Microphone = Allowed
- **Restart app**: Force stop and reopen
- **Check logs**: Use `adb logcat | grep StealthBodyguard` to see detection logs

### Counter doesn't increment

- **Confidence too low**: Voice must be >55% confidence
    - Speak louder
    - Speak more clearly
    - Move closer to microphone
- **Background noise**: Turn off music/TV
- **Check logs**: Look for "Voice trigger detected" messages

### Counter resets too quickly

- **This is normal**: 10-second timeout is by design
- **Solution**: Say "HELP" 3 times within 10 seconds

### Background service doesn't launch

- **Check service is enabled**: Bell icon should be 🔔 (not 🔕)
- **Check battery optimization**: Disable for SHAKTI AI
- **Wait for 2 detections**: Service needs "HELP" said 2 times (not 3)
- **Check logs**: `adb logcat | grep StealthTrigger`

## Expected Audio Metrics

When saying "HELP" loudly:

- **RMS**: 8000-25000
- **ZCR**: 0.05-0.15
- **Peak**: 15000-30000
- **Bursts**: 1-2
- **Confidence**: 55-75%

When playing music/TV:

- **RMS**: 5000-15000
- **ZCR**: 0.01-0.05 or >0.20
- **Confidence**: 0-30%
- **Counter**: Should NOT increment

## Viewing Logs

### Android Studio

1. Connect phone via USB
2. Open Logcat
3. Filter: `tag:StealthBodyguard OR tag:StealthTrigger`
4. Say "HELP"
5. Look for:

```
🗣️ Voice trigger detected: "HELP" (1/3) confidence=0.65
Voice analysis: RMS=12500, ZCR=0.089, Peak=18234, Bursts=2, Confidence=0.65
```

### ADB Command Line

```bash
# Real-time monitoring
adb logcat -s StealthBodyguard:I StealthTrigger:I

# Save to file
adb logcat -s StealthBodyguard:I StealthTrigger:I > test_log.txt
```

## Success Criteria

✅ **PASS**: All 5 tests work as expected

- Voice detection shows 30-75% for speech
- Counter increments on each "HELP"
- Emergency triggers after 3 "HELP"s
- Counter resets after 10s timeout
- Background service auto-launches

❌ **FAIL**: If any test doesn't work

- Check troubleshooting section
- Review logs for errors
- Verify permissions granted
- Test in quieter environment

## Advanced Testing

### Noise Rejection Test

1. Play YouTube music at 50% volume
2. Say "HELP" multiple times
3. **Expected**: Counter should NOT increment (music != speech)

### Accent Test

1. Say "HELP" with different accents/emphasis
2. "HELP!" (shouting)
3. "help me" (phrase)
4. "heeeelp" (drawn out)
5. **Expected**: Should detect most variations above 55% confidence

### Distance Test

1. Start 6 inches from phone - say "HELP"
2. Move to 1 foot - say "HELP"
3. Move to 2 feet - say "HELP"
4. Move to 5 feet - say "HELP"
5. **Expected**: Detection decreases with distance, fails at 5+ feet

## Performance Targets

| Metric | Target | Acceptable | Fail |
|--------|--------|------------|------|
| Detection Latency | <100ms | <200ms | >500ms |
| False Positive Rate | <3% | <10% | >20% |
| Speech Detection | >85% | >70% | <50% |
| Battery Impact | <1%/hr | <2%/hr | >5%/hr |

## Report Issues

If tests fail consistently:

1. Collect logs from failed test
2. Note phone model and Android version
3. Describe environment (quiet room, noisy, etc.)
4. Include voice metrics (RMS, ZCR) from logs
5. Create GitHub issue with above info
