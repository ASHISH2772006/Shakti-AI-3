# 🤖 UI Automator Tests - Complete Guide

## 📋 **Overview**

Automated system-wide tests for SHAKTI AI 3.0 using Android UI Automator framework.

**Test File:** `app/src/androidTest/java/com/shakti/ai/UIAutomatorTests.kt`

**Total Tests:** 12 automated tests covering:

- ✅ App launches (Calculator + Main App)
- ✅ Permission handling
- ✅ Background service lifecycle
- ✅ Service persistence after app kill
- ✅ Calculator operations
- ✅ Secret navigation (long press)
- ✅ Tab navigation
- ✅ Cross-app functionality
- ✅ Status indicators
- ✅ Screen rotation
- ✅ Multiple restarts
- ✅ Notification interaction

---

## 🔧 **Setup**

### **1. Install Dependencies**

Dependencies are already added to `app/build.gradle.kts`:

```kotlin
androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
androidTestImplementation("androidx.test:runner:1.5.2")
androidTestImplementation("androidx.test:rules:1.5.0")
androidTestImplementation("androidx.test:core:1.5.0")
```

### **2. Sync Gradle**

```bash
# In Android Studio, click "Sync Now"
# OR run:
./gradlew clean build
```

### **3. Connect Android Device**

```bash
# Enable USB Debugging on device
# Connect via USB
adb devices

# Should show:
# List of devices attached
# XXXXXXXX    device
```

### **4. Unlock Device**

- ✅ Screen unlocked
- ✅ No lock pattern/PIN required during testing
- ✅ Device stays awake (Settings → Developer Options → Stay Awake)

---

## 🚀 **Running Tests**

### **Method 1: Run All Tests (Recommended)**

```bash
# Run all 12 tests
./gradlew connectedAndroidTest

# View results in HTML report
# Open: app/build/reports/androidTests/connected/index.html
```

### **Method 2: Run Single Test**

```bash
# Run specific test
./gradlew connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.shakti.ai.UIAutomatorTests#test01_CalculatorLaunch
```

### **Method 3: Run from Android Studio**

1. Open `UIAutomatorTests.kt`
2. Right-click on test function
3. Select "Run 'testXX_...'"
4. View results in Run panel

### **Method 4: Using adb**

```bash
# Run tests via adb
adb shell am instrument -w \
  -r -e debug false \
  com.shakti.ai.test/androidx.test.runner.AndroidJUnitRunner
```

---

## 📊 **Test Results**

### **Viewing Results**

**HTML Report:**

```
app/build/reports/androidTests/connected/index.html
```

**Console Output:**

```
✅ Test 1 PASSED: Calculator launches correctly
✅ Test 2 PASSED: Permissions handled correctly
✅ Test 3 PASSED: Background service enabled
...
```

**Example Output:**

```
Starting 12 tests on Pixel 5 - 13

com.shakti.ai.UIAutomatorTests > test01_CalculatorLaunch PASSED
com.shakti.ai.UIAutomatorTests > test02_PermissionRequests PASSED
com.shakti.ai.UIAutomatorTests > test03_EnableBackgroundService PASSED
com.shakti.ai.UIAutomatorTests > test04_ServicePersistence PASSED
com.shakti.ai.UIAutomatorTests > test05_CalculatorOperations PASSED
com.shakti.ai.UIAutomatorTests > test06_SecretNavigation PASSED
com.shakti.ai.UIAutomatorTests > test07_MainAppTabNavigation PASSED
com.shakti.ai.UIAutomatorTests > test08_CrossAppNavigation PASSED
com.shakti.ai.UIAutomatorTests > test09_MonitoringStatusIndicators PASSED
com.shakti.ai.UIAutomatorTests > test10_RotationHandling PASSED
com.shakti.ai.UIAutomatorTests > test11_MultipleRestarts PASSED
com.shakti.ai.UIAutomatorTests > test12_NotificationInteraction PASSED

Tests: 12 passed, 12 total
Time: 3m 24s
```

---

## 🧪 **Individual Test Descriptions**

### **Test 1: Calculator Launch**

- Launches Calculator app
- Verifies display and title bar
- **Time:** ~5 seconds

### **Test 2: Permission Requests**

- Handles permission dialogs
- Grants all required permissions
- **Time:** ~10 seconds

### **Test 3: Enable Background Service**

- Taps service toggle
- Enables monitoring
- Verifies notification appears
- **Time:** ~8 seconds

### **Test 4: Service Persistence**

- Enables service
- Kills app (swipe away)
- Verifies service still running
- **Time:** ~10 seconds

### **Test 5: Calculator Operations**

- Tests: 5 + 3 = 8
- Tests: 7 × 8 = 56
- Verifies calculations correct
- **Time:** ~8 seconds

### **Test 6: Secret Navigation**

- Long presses title bar
- Verifies main app opens
- **Time:** ~6 seconds

### **Test 7: Tab Navigation**

- Swipes through AI module tabs
- Verifies all tabs accessible
- **Time:** ~10 seconds

### **Test 8: Cross-App Navigation**

- Opens Chrome
- Returns via notification
- **Time:** ~12 seconds

### **Test 9: Status Indicators**

- Checks "Protected" status
- Verifies timer display
- **Time:** ~8 seconds

### **Test 10: Rotation Handling**

- Enters value "123"
- Rotates device
- Verifies value preserved
- **Time:** ~10 seconds

### **Test 11: Multiple Restarts**

- Launches app 5 times
- Kills app between each
- Verifies stability
- **Time:** ~30 seconds

### **Test 12: Notification Interaction**

- Opens notification
- Verifies ongoing notification
- **Time:** ~8 seconds

**Total Runtime:** ~3-4 minutes

---

## 🐛 **Troubleshooting**

### **Problem: Tests fail to start**

**Solution:**

```bash
# Unlock device
# Disable screen lock
# Keep screen on

adb shell settings put global stay_on_while_plugged_in 3
```

### **Problem: Permission errors**

**Solution:**

```bash
# Grant permissions manually
adb shell pm grant com.shakti.ai android.permission.RECORD_AUDIO
adb shell pm grant com.shakti.ai android.permission.CAMERA
adb shell pm grant com.shakti.ai android.permission.ACCESS_FINE_LOCATION
```

### **Problem: Service not found**

**Solution:**

```bash
# Install app first
./gradlew installDebug

# Then run tests
./gradlew connectedAndroidTest
```

### **Problem: Element not found**

**Cause:** UI timing issues

**Solution:** Tests include `device.waitForIdle()` and timeouts

### **Problem: Multiple devices connected**

**Solution:**

```bash
# Specify device
ANDROID_SERIAL=DEVICE_ID ./gradlew connectedAndroidTest
```

---

## ⚙️ **Configuration**

### **Timeout Settings**

In `UIAutomatorTests.kt`:

```kotlin
private const val LAUNCH_TIMEOUT = 5000L  // 5 seconds
private const val SHORT_TIMEOUT = 2000L   // 2 seconds
```

Increase if tests fail on slower devices:

```kotlin
private const val LAUNCH_TIMEOUT = 10000L  // 10 seconds
private const val SHORT_TIMEOUT = 5000L    // 5 seconds
```

### **Test Runner Configuration**

In `app/build.gradle.kts`:

```kotlin
defaultConfig {
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
}
```

---

## 📱 **Device Requirements**

**Minimum:**

- Android 7.0 (API 24)
- Screen unlocked
- USB Debugging enabled
- 2GB RAM
- Internet connection (optional, for blockchain tests)

**Recommended:**

- Android 11+ (API 30+)
- Physical device (not emulator)
- Developer Options enabled
- Stay Awake enabled
- Sufficient storage space

---

## 🎯 **CI/CD Integration**

### **GitHub Actions Example**

```yaml
name: Android Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          
      - name: Run UI Automator Tests
        uses: reactivecircus/android-emulator-runner@v2
        with:
          api-level: 30
          script: ./gradlew connectedAndroidTest
          
      - name: Upload Test Results
        uses: actions/upload-artifact@v3
        if: always()
        with:
          name: test-results
          path: app/build/reports/androidTests/
```

### **GitLab CI Example**

```yaml
test:
  stage: test
  image: runmymind/docker-android-sdk:latest
  script:
    - ./gradlew connectedAndroidTest
  artifacts:
    reports:
      junit: app/build/test-results/**/*.xml
    paths:
      - app/build/reports/androidTests/
```

---

## 📈 **Coverage Report**

### **Generate Coverage**

```bash
# Run tests with coverage
./gradlew connectedAndroidTest \
  -Pcoverage

# View coverage report
# Open: app/build/reports/coverage/index.html
```

---

## 🔍 **Debugging Tests**

### **View Logcat During Tests**

```bash
# Terminal 1: Run tests
./gradlew connectedAndroidTest

# Terminal 2: Monitor logs
adb logcat | grep -E "Test|Shakti|Bodyguard"
```

### **Take Screenshots**

Add to test:

```kotlin
@Test
fun test_Something() {
    // ... test code ...
    
    // Take screenshot
    val screenshot = device.takeScreenshot()
    val file = File("/sdcard/screenshot.png")
    screenshot.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(file))
}
```

### **Dump UI Hierarchy**

```bash
# During test, dump UI hierarchy
adb shell uiautomator dump /sdcard/ui_dump.xml

# View hierarchy
adb pull /sdcard/ui_dump.xml
```

---

## 📝 **Writing New Tests**

### **Template for New Test**

```kotlin
/**
 * Test Description
 */
@Test
fun testXX_TestName() {
    // Setup
    launchCalculator()
    grantAllPermissions()
    
    // Action
    // ... perform test actions ...
    
    // Verification
    val element = device.findObject(UiSelector().text("Expected"))
    assertTrue("Element not found", element.exists())
    
    println("✅ Test XX PASSED: Description")
}
```

### **Finding UI Elements**

```kotlin
// By text
device.findObject(UiSelector().text("Calculator"))

// By resource ID
device.findObject(UiSelector().resourceId("com.shakti.ai:id/button"))

// By class
device.findObject(UiSelector().className("android.widget.Button"))

// By description
device.findObject(UiSelector().description("Settings"))

// Combined
device.findObject(
    UiSelector()
        .text("Enable")
        .className("android.widget.Button")
)
```

---

## 🎓 **Best Practices**

1. **Always wait for idle:** `device.waitForIdle()`
2. **Use timeouts:** `device.wait(Until.hasObject(...), TIMEOUT)`
3. **Handle flakiness:** Add retries for flaky tests
4. **Clear state:** Start each test from clean state
5. **Print progress:** Use `println()` for debugging
6. **Test isolation:** Tests should not depend on each other
7. **Fast execution:** Keep tests under 30 seconds each

---

## 🚀 **Quick Start**

**1. Install app:**

```bash
./gradlew installDebug
```

**2. Run tests:**

```bash
./gradlew connectedAndroidTest
```

**3. View results:**

```bash
open app/build/reports/androidTests/connected/index.html
```

**That's it!** 🎉

---

## 📞 **Support**

**If tests fail:**

1. Check device is unlocked
2. Check USB debugging enabled
3. Check permissions granted
4. Check app installed
5. Check internet connection
6. Review test logs
7. Run tests individually to isolate issue

**Still having issues?**

- Check `adb logcat` for errors
- Review `app/build/reports/androidTests/connected/index.html`
- Increase timeout values
- Try on different device

---

## ✅ **Success Criteria**

**Tests are PASSING if:**

- All 12 tests show ✅ PASSED
- No crashes during execution
- Total time < 5 minutes
- All assertions pass
- HTML report shows 100% pass rate

**Your automated tests are now set up!** 🎉

Run them before every release to ensure quality! 🚀✨
