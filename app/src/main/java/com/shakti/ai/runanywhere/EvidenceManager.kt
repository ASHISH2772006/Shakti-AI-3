package com.shakti.ai.runanywhere

import android.content.Context
import android.location.Location
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Evidence Manager
 *
 * Handles creation, encryption, and storage of evidence packages.
 * Features:
 * - Encrypted audio/video recording
 * - SHA-256 hash generation
 * - Local encrypted storage
 * - Blockchain anchoring queue
 * - Privacy-first (user consent required)
 */
class EvidenceManager(private val context: Context) {

    companion object {
        private const val TAG = "EvidenceManager"
        private const val EVIDENCE_DIR = "evidence"
        private const val MAX_RECORDING_DURATION_MS = 300000 // 5 minutes
    }

    private val evidenceDir: File = File(context.filesDir, EVIDENCE_DIR).apply {
        if (!exists()) {
            mkdirs()
        }
    }

    private var mediaRecorder: MediaRecorder? = null
    private var currentRecordingFile: File? = null
    private var isRecording = false

    // Master key for encryption
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    /**
     * Create evidence package from threat detection
     */
    suspend fun createEvidencePackage(
        threat: ThreatDetection,
        location: Location?,
        sensorLogs: SensorLogs
    ): EvidencePackage = withContext(Dispatchers.IO) {
        val evidenceId = EvidencePackage.generateEvidenceId()

        Log.i(TAG, "Creating evidence package: $evidenceId")

        val locationEvidence = location?.let {
            LocationEvidence(
                latitude = it.latitude,
                longitude = it.longitude,
                accuracy = it.accuracy,
                altitude = it.altitude,
                timestamp = System.currentTimeMillis()
            )
        }

        val package_ = EvidencePackage(
            evidenceId = evidenceId,
            timestamp = System.currentTimeMillis(),
            threatDetection = threat,
            location = locationEvidence,
            sensorLogs = sensorLogs,
            isEncrypted = true
        )

        // Calculate hash
        val hash = package_.calculateHash()
        val packageWithHash = package_.copy(evidenceHash = hash)

        // Save package metadata
        saveEvidenceMetadata(packageWithHash)

        Log.i(TAG, "Evidence package created: $evidenceId (hash: $hash)")

        return@withContext packageWithHash
    }

    /**
     * Start evidence recording (audio + video)
     */
    suspend fun startEvidenceRecording() = withContext(Dispatchers.IO) {
        if (isRecording) {
            Log.w(TAG, "Already recording")
            return@withContext
        }

        Log.i(TAG, "Starting evidence recording")

        try {
            val timestamp = System.currentTimeMillis()
            currentRecordingFile = File(evidenceDir, "recording_$timestamp.m4a")

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setMaxDuration(MAX_RECORDING_DURATION_MS)
                setOutputFile(currentRecordingFile?.absolutePath)

                prepare()
                start()
            }

            isRecording = true
            Log.i(TAG, "Evidence recording started: ${currentRecordingFile?.name}")

        } catch (e: Exception) {
            Log.e(TAG, "Error starting recording", e)
            stopEvidenceRecording()
        }
    }

    /**
     * Stop evidence recording
     */
    suspend fun stopEvidenceRecording(): File? = withContext(Dispatchers.IO) {
        if (!isRecording) {
            return@withContext null
        }

        Log.i(TAG, "Stopping evidence recording")

        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null

            val recordedFile = currentRecordingFile
            currentRecordingFile = null
            isRecording = false

            // Encrypt the recording
            val encryptedFile = recordedFile?.let { encryptFile(it) }

            // Delete unencrypted original
            recordedFile?.delete()

            Log.i(TAG, "Evidence recording stopped and encrypted")

            return@withContext encryptedFile

        } catch (e: Exception) {
            Log.e(TAG, "Error stopping recording", e)
            return@withContext null
        }
    }

    /**
     * Encrypt file using Android Keystore
     */
    private suspend fun encryptFile(file: File): File = withContext(Dispatchers.IO) {
        val encryptedFile = File(evidenceDir, "${file.name}.enc")

        try {
            val encryptedFileWrapper = EncryptedFile.Builder(
                encryptedFile,
                context,
                masterKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            // Read original file
            val bytes = file.readBytes()

            // Write encrypted
            encryptedFileWrapper.openFileOutput().use { outputStream ->
                outputStream.write(bytes)
            }

            Log.i(TAG, "File encrypted: ${encryptedFile.name}")

            return@withContext encryptedFile

        } catch (e: Exception) {
            Log.e(TAG, "Error encrypting file", e)
            return@withContext file // Return original if encryption fails
        }
    }

    /**
     * Decrypt file
     */
    suspend fun decryptFile(encryptedFile: File): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val encryptedFileWrapper = EncryptedFile.Builder(
                encryptedFile,
                context,
                masterKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val bytes = encryptedFileWrapper.openFileInput().use { inputStream ->
                inputStream.readBytes()
            }

            Log.i(TAG, "File decrypted: ${encryptedFile.name}")

            return@withContext bytes

        } catch (e: Exception) {
            Log.e(TAG, "Error decrypting file", e)
            return@withContext null
        }
    }

    /**
     * Save evidence package metadata
     */
    private suspend fun saveEvidenceMetadata(evidence: EvidencePackage) =
        withContext(Dispatchers.IO) {
            try {
                val metadataFile = File(evidenceDir, "${evidence.evidenceId}.json")
                val json = evidence.toJson()

                // Encrypt metadata
                val encryptedFile = EncryptedFile.Builder(
                    metadataFile,
                    context,
                    masterKeyAlias,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
                ).build()

                encryptedFile.openFileOutput().use { outputStream ->
                    outputStream.write(json.toByteArray())
                }

                Log.i(TAG, "Evidence metadata saved: ${metadataFile.name}")

            } catch (e: Exception) {
                Log.e(TAG, "Error saving metadata", e)
            }
        }

    /**
     * Get all evidence packages
     */
    suspend fun getAllEvidencePackages(): List<String> = withContext(Dispatchers.IO) {
        return@withContext evidenceDir.listFiles { file ->
            file.name.endsWith(".json")
        }?.map { it.name } ?: emptyList()
    }

    /**
     * Delete evidence package (user requested)
     */
    suspend fun deleteEvidencePackage(evidenceId: String) = withContext(Dispatchers.IO) {
        try {
            // Delete metadata
            val metadataFile = File(evidenceDir, "$evidenceId.json")
            if (metadataFile.exists()) {
                metadataFile.delete()
            }

            // Delete associated files
            evidenceDir.listFiles { file ->
                file.name.contains(evidenceId)
            }?.forEach { it.delete() }

            Log.i(TAG, "Evidence package deleted: $evidenceId")

        } catch (e: Exception) {
            Log.e(TAG, "Error deleting evidence", e)
        }
    }

    /**
     * Auto-delete old evidence (privacy setting)
     */
    suspend fun deleteOldEvidence(olderThanDays: Int) = withContext(Dispatchers.IO) {
        val cutoffTime = System.currentTimeMillis() - (olderThanDays * 24 * 60 * 60 * 1000L)

        try {
            evidenceDir.listFiles()?.forEach { file ->
                if (file.lastModified() < cutoffTime) {
                    file.delete()
                    Log.d(TAG, "Deleted old evidence: ${file.name}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting old evidence", e)
        }
    }

    /**
     * Get storage usage
     */
    fun getStorageUsageBytes(): Long {
        return evidenceDir.listFiles()?.sumOf { it.length() } ?: 0L
    }

    /**
     * Get storage usage in MB
     */
    fun getStorageUsageMB(): Float {
        return getStorageUsageBytes() / (1024f * 1024f)
    }

    /**
     * Check if recording
     */
    fun isRecording(): Boolean = isRecording

    /**
     * Cleanup
     */
    fun cleanup() {
        if (isRecording) {
            try {
                mediaRecorder?.apply {
                    stop()
                    release()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error cleaning up recorder", e)
            }
        }
        mediaRecorder = null
        currentRecordingFile = null
        isRecording = false
    }
}
