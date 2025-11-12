package com.shakti.ai.runanywhere

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.ParcelUuid
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

/**
 * BLE Mesh Service for Offline Peer-to-Peer Communication
 *
 * Features:
 * - Broadcast SOS messages to nearby SHAKTI users
 * - Scan for nearby helpers
 * - Rank helpers by proximity and availability
 * - Works completely offline (no internet required)
 *
 * Uses BLE advertising and scanning for mesh networking.
 */
class BLEMeshService private constructor(private val context: Context) {

    companion object {
        private const val TAG = "BLEMesh"

        // SHAKTI BLE UUID for identifying other app users
        private val SHAKTI_SERVICE_UUID = UUID.fromString("0000FE00-0000-1000-8000-00805f9b34fb")
        private val SHAKTI_SOS_UUID = UUID.fromString("0000FE01-0000-1000-8000-00805f9b34fb")

        // Scan settings
        private const val SCAN_INTERVAL_MS = 5000L // Scan every 5 seconds
        private const val HELPER_TIMEOUT_MS = 30000L // 30 seconds

        @Volatile
        private var INSTANCE: BLEMeshService? = null

        fun getInstance(context: Context): BLEMeshService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BLEMeshService(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    private val bleScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    private val bleAdvertiser: BluetoothLeAdvertiser? = bluetoothAdapter?.bluetoothLeAdvertiser

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // State flows
    private val _nearbyHelpers = MutableStateFlow<List<NearbyHelper>>(emptyList())
    val nearbyHelpers: StateFlow<List<NearbyHelper>> = _nearbyHelpers

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    private val _isAdvertising = MutableStateFlow(false)
    val isAdvertising: StateFlow<Boolean> = _isAdvertising

    // Current SOS broadcast
    private var currentSOSBroadcast: SOSBroadcast? = null

    // Discovered helpers map (address -> helper)
    private val discoveredHelpers = mutableMapOf<String, NearbyHelper>()

    /**
     * Start scanning for nearby SHAKTI users
     */
    fun startScanning() {
        if (!checkBLEPermissions()) {
            Log.w(TAG, "BLE permissions not granted")
            return
        }

        if (!bluetoothAdapter?.isEnabled!!) {
            Log.w(TAG, "Bluetooth is not enabled")
            return
        }

        if (_isScanning.value) {
            Log.d(TAG, "Already scanning")
            return
        }

        Log.i(TAG, "Starting BLE scanning for SHAKTI users")

        val scanFilters = listOf(
            ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(SHAKTI_SERVICE_UUID))
                .build()
        )

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER) // Battery efficient
            .setReportDelay(0) // Real-time results
            .build()

        try {
            if (checkBLEPermissions()) {
                bleScanner?.startScan(scanFilters, scanSettings, scanCallback)
                _isScanning.value = true

                // Start periodic cleanup of stale helpers
                startHelperCleanup()

                Log.i(TAG, "BLE scanning started")
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception starting scan", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting scan", e)
        }
    }

    /**
     * Stop scanning
     */
    fun stopScanning() {
        if (!_isScanning.value) {
            return
        }

        Log.i(TAG, "Stopping BLE scanning")

        try {
            if (checkBLEPermissions()) {
                bleScanner?.stopScan(scanCallback)
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception stopping scan", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping scan", e)
        }

        _isScanning.value = false
    }

    /**
     * Scan callback for discovering nearby SHAKTI users
     */
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                handleScanResult(it)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results?.forEach { handleScanResult(it) }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "BLE scan failed with error code: $errorCode")
            _isScanning.value = false
        }
    }

    /**
     * Handle scan result - discovered SHAKTI user
     */
    private fun handleScanResult(result: ScanResult) {
        val device = result.device
        val rssi = result.rssi
        val distance = calculateDistanceFromRSSI(rssi)

        Log.d(TAG, "Discovered SHAKTI user: ${device.address} (RSSI: $rssi, Distance: $distance m)")

        // Check if device is broadcasting SOS
        val advertisedData = result.scanRecord?.serviceData
        val sosData = advertisedData?.get(ParcelUuid(SHAKTI_SOS_UUID))

        // Get device name with permission check
        val deviceName = if (checkBLEPermissions()) {
            try {
                device.name ?: "SHAKTI User"
            } catch (e: SecurityException) {
                "SHAKTI User"
            }
        } else {
            "SHAKTI User"
        }

        val helper = NearbyHelper(
            userId = device.address,
            name = deviceName,
            distance = distance,
            rssi = rssi,
            isAvailable = sosData == null, // Not available if already in distress
            responseTime = 0,
            lastSeen = System.currentTimeMillis()
        )

        // Update helpers list
        discoveredHelpers[device.address] = helper
        updateHelpersList()

        // If SOS data present, notify about nearby emergency
        if (sosData != null) {
            handleReceivedSOS(sosData)
        }
    }

    /**
     * Calculate distance from RSSI (approximate)
     */
    private fun calculateDistanceFromRSSI(rssi: Int): Float {
        // Using path loss model: RSSI = TxPower - 10*n*log10(distance)
        // Assuming TxPower = -59 dBm at 1m, n = 2 (free space)
        val txPower = -59
        val n = 2.0
        val ratio = (txPower - rssi) / (10.0 * n)
        return Math.pow(10.0, ratio).toFloat()
    }

    /**
     * Update helpers list (sorted by priority)
     */
    private fun updateHelpersList() {
        val sortedHelpers = discoveredHelpers.values
            .sortedByDescending { it.calculatePriority() }
            .take(10) // Top 10 closest helpers

        _nearbyHelpers.value = sortedHelpers
    }

    /**
     * Start periodic cleanup of stale helpers
     */
    private fun startHelperCleanup() {
        serviceScope.launch {
            while (_isScanning.value) {
                delay(10000) // Clean up every 10 seconds

                val now = System.currentTimeMillis()
                val staleHelpers = discoveredHelpers.filterValues {
                    now - it.lastSeen > HELPER_TIMEOUT_MS
                }

                staleHelpers.keys.forEach {
                    discoveredHelpers.remove(it)
                }

                if (staleHelpers.isNotEmpty()) {
                    Log.d(TAG, "Removed ${staleHelpers.size} stale helpers")
                    updateHelpersList()
                }
            }
        }
    }

    /**
     * Broadcast SOS message to nearby SHAKTI users
     */
    fun broadcastSOS(sos: SOSBroadcast) {
        if (!checkBLEPermissions()) {
            Log.w(TAG, "BLE permissions not granted")
            return
        }

        if (!bluetoothAdapter?.isEnabled!!) {
            Log.w(TAG, "Bluetooth is not enabled")
            return
        }

        Log.i(TAG, "Broadcasting SOS via BLE: ${sos.messageId}")

        currentSOSBroadcast = sos

        // Start BLE advertising with SOS data
        startAdvertising(sos)
    }

    /**
     * Start BLE advertising with SOS data
     */
    private fun startAdvertising(sos: SOSBroadcast) {
        if (_isAdvertising.value) {
            stopAdvertising()
        }

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setConnectable(false)
            .setTimeout(0) // Advertise indefinitely
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH) // Max range
            .build()

        // Create compact SOS payload
        val sosBytes = sos.toBytes()

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(false) // Save space
            .addServiceUuid(ParcelUuid(SHAKTI_SERVICE_UUID))
            .addServiceData(
                ParcelUuid(SHAKTI_SOS_UUID),
                sosBytes.take(20).toByteArray()
            ) // BLE limit
            .build()

        try {
            if (checkBLEPermissions()) {
                bleAdvertiser?.startAdvertising(settings, data, advertiseCallback)
                Log.i(TAG, "BLE advertising started")
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception starting advertising", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting advertising", e)
        }
    }

    /**
     * Stop advertising
     */
    private fun stopAdvertising() {
        try {
            if (checkBLEPermissions()) {
                bleAdvertiser?.stopAdvertising(advertiseCallback)
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception stopping advertising", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping advertising", e)
        }

        _isAdvertising.value = false
    }

    /**
     * Advertise callback
     */
    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            Log.i(TAG, "BLE advertising started successfully")
            _isAdvertising.value = true
        }

        override fun onStartFailure(errorCode: Int) {
            Log.e(TAG, "BLE advertising failed with error code: $errorCode")
            _isAdvertising.value = false
        }
    }

    /**
     * Handle received SOS from nearby user
     */
    private fun handleReceivedSOS(sosData: ByteArray) {
        val sos = SOSBroadcast.fromBytes(sosData)

        sos?.let {
            Log.w(
                TAG,
                "⚠️ SOS RECEIVED from ${it.senderName} at ${it.location?.latitude}, ${it.location?.longitude}"
            )

            // TODO: Show notification to user asking if they can help
            // TODO: Update UI with nearby emergency marker

            // For now, just log
            Log.w(TAG, "Nearby user needs help! Urgency: ${it.urgency}, Type: ${it.threatType}")
        }
    }

    /**
     * Cancel SOS broadcast
     */
    fun cancelSOS() {
        currentSOSBroadcast = null
        stopAdvertising()
        Log.i(TAG, "SOS broadcast cancelled")
    }

    /**
     * Check BLE permissions
     */
    private fun checkBLEPermissions(): Boolean {
        val permissions =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                listOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } else {
                listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }

        return permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Clean up
     */
    fun cleanup() {
        stopScanning()
        stopAdvertising()
        serviceScope.cancel()
        discoveredHelpers.clear()
        _nearbyHelpers.value = emptyList()
    }
}
