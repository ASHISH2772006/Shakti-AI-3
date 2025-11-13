package com.shakti.ai

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * UI Automator Tests for SHAKTI AI 3.0
 *
 * These tests verify system-wide functionality:
 * - Background service operation
 * - Auto-launch from other apps
 * - Emergency overlay across apps
 * - Permission handling
 * - Notification verification
 * - Cross-app navigation
 *
 * Run with: ./gradlew connectedAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class UIAutomatorTests {

    private lateinit var device: UiDevice
    private lateinit var context: Context

    companion object {
        private const val PACKAGE_NAME = "com.shakti.ai"
        private const val CALCULATOR_ACTIVITY = "$PACKAGE_NAME.stealth.StealthCalculatorActivity"
        private const val MAIN_ACTIVITY = "$PACKAGE_NAME.MainActivity"
        private const val LAUNCH_TIMEOUT = 5000L
        private const val SHORT_TIMEOUT = 2000L
    }

    @Before
    fun setUp() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        context = ApplicationProvider.getApplicationContext()

        // Start from home screen
        device.pressHome()

        // Wait for launcher
        val launcherPackage = device.launcherPackageName
        assertNotNull(launcherPackage)
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT)
    }

    /**
     * Test 1: Calculator App Launch
     */
    @Test
    fun test01_CalculatorLaunch() {
        // Launch calculator
        launchCalculator()

        // Verify calculator UI visible
        val display =
            device.findObject(UiSelector().resourceId("$PACKAGE_NAME:id/calculator_display"))
        assertTrue("Calculator display not found", display.exists())

        // Verify title bar shows "Calculator"
        val title = device.findObject(UiSelector().text("Calculator"))
        assertTrue("Calculator title not found", title.exists())

        println("✅ Test 1 PASSED: Calculator launches correctly")
    }

    /**
     * Test 2: Permission Request Flow
     */
    @Test
    fun test02_PermissionRequests() {
        launchCalculator()

        // Wait for permission dialogs
        device.wait(Until.hasObject(By.text("Allow")), SHORT_TIMEOUT)

        // Grant all permissions
        for (i in 1..6) { // Audio, Camera, Location, Phone, SMS, Notification
            val allowButton = device.findObject(
                By.text(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) "While using the app" else "Allow")
            )
            if (allowButton != null) {
                allowButton.click()
                device.waitForIdle()
            }
        }

        // Verify calculator still visible after permissions
        val display = device.findObject(UiSelector().text("0"))
        assertTrue("Calculator not visible after permissions", display.exists())

        println("✅ Test 2 PASSED: Permissions handled correctly")
    }

    /**
     * Test 3: Enable Background Service
     */
    @Test
    fun test03_EnableBackgroundService() {
        launchCalculator()
        grantAllPermissions()

        // Find and tap service toggle icon (🔕 or 🔔)
        val serviceToggle = device.findObject(
            UiSelector().descriptionContains("service").className("android.widget.Button")
        )

        // If not found by description, try by position (top-right area)
        if (!serviceToggle.exists()) {
            // Tap approximate location of service toggle
            val displayWidth = device.displayWidth
            val toggleX = displayWidth - 100
            val toggleY = 100
            device.click(toggleX, toggleY)
        } else {
            serviceToggle.click()
        }

        device.waitForIdle()

        // Find "Enable" button in dialog
        val enableButton = device.findObject(UiSelector().text("Enable"))
        if (enableButton.exists()) {
            enableButton.click()
            device.waitForIdle()
        }

        // Verify notification appears
        device.openNotification()
        device.wait(Until.hasObject(By.textContains("Digital Bodyguard")), SHORT_TIMEOUT)

        val notification = device.findObject(UiSelector().textContains("Digital Bodyguard"))
        assertTrue("Background service notification not found", notification.exists())

        // Close notification shade
        device.pressBack()

        println("✅ Test 3 PASSED: Background service enabled")
    }

    /**
     * Test 4: Service Persistence After App Kill
     */
    @Test
    fun test04_ServicePersistence() {
        launchCalculator()
        grantAllPermissions()
        enableBackgroundService()

        // Kill app
        device.pressRecentApps()
        device.waitForIdle()

        // Swipe away calculator
        val appCard = device.findObject(UiSelector().textContains("Calculator"))
        if (appCard.exists()) {
            appCard.swipeRight(50)
        }

        device.pressHome()
        device.waitForIdle()

        // Check notification still exists
        device.openNotification()
        device.wait(Until.hasObject(By.textContains("Digital Bodyguard")), SHORT_TIMEOUT)

        val notification = device.findObject(UiSelector().textContains("Digital Bodyguard"))
        assertTrue("Service notification disappeared after app kill", notification.exists())

        device.pressBack()

        println("✅ Test 4 PASSED: Service persists after app kill")
    }

    /**
     * Test 5: Calculator Button Operations
     */
    @Test
    fun test05_CalculatorOperations() {
        launchCalculator()
        grantAllPermissions()

        // Test: 5 + 3 = 8
        clickButton("5")
        clickButton("+")
        clickButton("3")
        clickButton("=")

        // Verify result
        val display = device.findObject(UiSelector().text("8"))
        assertTrue("Calculator result incorrect: expected 8", display.exists())

        // Clear
        clickButton("C")

        // Test: 7 × 8 = 56
        clickButton("7")
        clickButton("×")
        clickButton("8")
        clickButton("=")

        val result2 = device.findObject(UiSelector().text("56"))
        assertTrue("Calculator result incorrect: expected 56", result2.exists())

        println("✅ Test 5 PASSED: Calculator operations work correctly")
    }

    /**
     * Test 6: Long Press Title Bar Navigation
     */
    @Test
    fun test06_SecretNavigation() {
        launchCalculator()
        grantAllPermissions()

        // Find title bar with "Calculator" text
        val titleBar = device.findObject(UiSelector().text("Calculator"))
        assertTrue("Title bar not found", titleBar.exists())

        // Long press (2 seconds)
        val bounds = titleBar.bounds
        device.swipe(
            bounds.centerX(), bounds.centerY(),
            bounds.centerX(), bounds.centerY(),
            100 // steps = duration, 100 steps ≈ 2 seconds
        )

        device.waitForIdle()

        // Verify main SHAKTI AI app opened
        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME)), SHORT_TIMEOUT)

        // Check for one of the AI module tabs
        val tab = device.findObject(
            UiSelector().textContains("Sathi").or(UiSelector().textContains("Guardian"))
        )

        assertTrue("Main app not opened after long press", tab.exists())

        println("✅ Test 6 PASSED: Secret navigation works")
    }

    /**
     * Test 7: Tab Navigation in Main App
     */
    @Test
    fun test07_MainAppTabNavigation() {
        launchMainApp()
        grantAllPermissions()

        // Verify initial tab (Sathi AI)
        val sathiTab = device.findObject(UiSelector().textContains("Sathi"))
        assertTrue("Sathi tab not found", sathiTab.exists())

        // Swipe to next tab
        val displayWidth = device.displayWidth
        val displayHeight = device.displayHeight
        device.swipe(displayWidth - 100, displayHeight / 2, 100, displayHeight / 2, 20)
        device.waitForIdle()

        // Verify Guardian tab visible
        val guardianTab = device.findObject(UiSelector().textContains("Guardian"))
        assertTrue("Guardian tab not found after swipe", guardianTab.exists())

        // Tap Nyaya tab directly
        val nyayaTab = device.findObject(UiSelector().textContains("Nyaya"))
        if (nyayaTab.exists()) {
            nyayaTab.click()
            device.waitForIdle()
        }

        println("✅ Test 7 PASSED: Tab navigation works")
    }

    /**
     * Test 8: Cross-App Navigation
     */
    @Test
    fun test08_CrossAppNavigation() {
        launchCalculator()
        grantAllPermissions()
        enableBackgroundService()

        // Go to home
        device.pressHome()
        device.waitForIdle()

        // Open Chrome (or any browser)
        val chrome = device.findObject(UiSelector().text("Chrome"))
        if (chrome.exists()) {
            chrome.click()
            device.wait(Until.hasObject(By.pkg("com.android.chrome")), LAUNCH_TIMEOUT)
        }

        // Return to calculator via notification
        device.openNotification()
        val notification = device.findObject(UiSelector().textContains("Digital Bodyguard"))
        if (notification.exists()) {
            notification.click()
            device.waitForIdle()
        }

        // Verify calculator opened
        val calculatorVisible = device.hasObject(By.text("Calculator"))
        assertTrue("Calculator not opened from notification", calculatorVisible)

        println("✅ Test 8 PASSED: Cross-app navigation works")
    }

    /**
     * Test 9: Monitoring Status Indicators
     */
    @Test
    fun test09_MonitoringStatusIndicators() {
        launchCalculator()
        grantAllPermissions()
        enableBackgroundService()

        // Wait for status indicators to appear
        device.waitForIdle()
        Thread.sleep(2000) // Allow monitoring to start

        // Check for "Protected" text
        val protectedStatus = device.findObject(UiSelector().textContains("Protected"))
        assertTrue("'Protected' status not shown", protectedStatus.exists())

        // Check for monitoring duration timer (MM:SS format)
        val timer = device.findObject(
            UiSelector().textMatches("\\d{2}:\\d{2}") // Regex for 00:00 format
        )
        assertTrue("Monitoring timer not found", timer.exists())

        println("✅ Test 9 PASSED: Status indicators working")
    }

    /**
     * Test 10: App Rotation Handling
     */
    @Test
    fun test10_RotationHandling() {
        launchCalculator()
        grantAllPermissions()

        // Enter a value
        clickButton("1")
        clickButton("2")
        clickButton("3")

        // Verify display shows 123
        val display1 = device.findObject(UiSelector().text("123"))
        assertTrue("Display doesn't show 123", display1.exists())

        // Rotate to landscape
        device.setOrientationLeft()
        device.waitForIdle()

        // Verify value preserved
        val display2 = device.findObject(UiSelector().text("123"))
        assertTrue("Display value not preserved after rotation", display2.exists())

        // Rotate back to portrait
        device.setOrientationNatural()
        device.waitForIdle()

        val display3 = device.findObject(UiSelector().text("123"))
        assertTrue("Display value not preserved after rotation back", display3.exists())

        println("✅ Test 10 PASSED: Rotation handling works")
    }

    /**
     * Test 11: Memory Test - Multiple Restarts
     */
    @Test
    fun test11_MultipleRestarts() {
        for (i in 1..5) {
            println("Restart attempt $i/5")

            launchCalculator()
            grantAllPermissions()
            device.waitForIdle()

            // Verify calculator loaded
            val display = device.findObject(UiSelector().text("0"))
            assertTrue("Calculator failed to load on restart $i", display.exists())

            // Kill app
            device.pressHome()
            device.pressRecentApps()
            device.waitForIdle()

            val appCard = device.findObject(UiSelector().textContains("Calculator"))
            if (appCard.exists()) {
                appCard.swipeRight(50)
            }

            device.pressHome()
        }

        println("✅ Test 11 PASSED: Multiple restarts handled correctly")
    }

    /**
     * Test 12: Notification Interaction
     */
    @Test
    fun test12_NotificationInteraction() {
        launchCalculator()
        grantAllPermissions()
        enableBackgroundService()

        // Go home
        device.pressHome()
        device.waitForIdle()

        // Open notification shade
        device.openNotification()
        device.wait(Until.hasObject(By.textContains("Digital Bodyguard")), SHORT_TIMEOUT)

        // Verify notification content
        val notification = device.findObject(UiSelector().textContains("Digital Bodyguard"))
        assertTrue("Notification not found", notification.exists())

        // Verify notification is ongoing (cannot be dismissed)
        val notificationBounds = notification.bounds
        device.swipe(
            notificationBounds.centerX(), notificationBounds.centerY(),
            device.displayWidth, notificationBounds.centerY(),
            20
        )
        device.waitForIdle()

        // Notification should still exist (ongoing)
        val notificationStillExists = device.findObject(
            UiSelector().textContains("Digital Bodyguard")
        ).exists()
        assertTrue("Notification dismissed (should be ongoing)", notificationStillExists)

        device.pressBack()

        println("✅ Test 12 PASSED: Notification works correctly")
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    /**
     * Launch Calculator app
     */
    private fun launchCalculator() {
        val intent = Intent().apply {
            setClassName(PACKAGE_NAME, CALCULATOR_ACTIVITY)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME)), LAUNCH_TIMEOUT)
    }

    /**
     * Launch Main SHAKTI AI app
     */
    private fun launchMainApp() {
        val intent = Intent().apply {
            setClassName(PACKAGE_NAME, MAIN_ACTIVITY)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME)), LAUNCH_TIMEOUT)
    }

    /**
     * Grant all required permissions
     */
    private fun grantAllPermissions() {
        // Grant permissions via adb commands
        val permissions = arrayOf(
            "android.permission.RECORD_AUDIO",
            "android.permission.CAMERA",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.CALL_PHONE",
            "android.permission.SEND_SMS",
            "android.permission.POST_NOTIFICATIONS"
        )

        for (permission in permissions) {
            try {
                device.executeShellCommand("pm grant $PACKAGE_NAME $permission")
            } catch (e: IOException) {
                println("Warning: Could not grant $permission: ${e.message}")
            }
        }

        device.waitForIdle()
    }

    /**
     * Enable background service
     */
    private fun enableBackgroundService() {
        // Tap service toggle (top-right corner)
        val displayWidth = device.displayWidth
        val toggleX = displayWidth - 100
        val toggleY = 100
        device.click(toggleX, toggleY)
        device.waitForIdle()

        // Tap "Enable" if dialog appears
        val enableButton = device.findObject(UiSelector().text("Enable"))
        if (enableButton.exists()) {
            enableButton.click()
            device.waitForIdle()
        }
    }

    /**
     * Click calculator button by text
     */
    private fun clickButton(text: String) {
        val button = device.findObject(UiSelector().text(text))
        assertTrue("Button '$text' not found", button.exists())
        button.click()
        device.waitForIdle()
    }

    /**
     * Execute shell command and return output
     */
    private fun executeShellCommand(command: String): String {
        return try {
            device.executeShellCommand(command)
        } catch (e: IOException) {
            println("Error executing command: $command")
            ""
        }
    }

    /**
     * Check if service is running
     */
    private fun isServiceRunning(serviceName: String): Boolean {
        val output = executeShellCommand("dumpsys activity services $PACKAGE_NAME")
        return output.contains(serviceName)
    }

    /**
     * Clear app data
     */
    private fun clearAppData() {
        executeShellCommand("pm clear $PACKAGE_NAME")
        device.waitForIdle()
        Thread.sleep(1000)
    }
}
