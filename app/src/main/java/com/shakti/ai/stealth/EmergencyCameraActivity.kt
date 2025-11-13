package com.shakti.ai.stealth

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.shakti.ai.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Emergency Camera Activity
 *
 * Automatically starts video recording when opened.
 * Used during emergency situations triggered by "HELP" 3x.
 *
 * Features:
 * - Auto-start recording on launch
 * - Full-screen camera preview
 * - High-quality video recording
 * - Saves to public Movies directory (accessible)
 * - Red recording indicator
 * - Stop button to end recording
 */
class EmergencyCameraActivity : AppCompatActivity(), SurfaceHolder.Callback {

    companion object {
        private const val TAG = "EmergencyCamera"
        const val EXTRA_EVIDENCE_ID = "evidence_id"
    }

    private var camera: Camera? = null
    private var mediaRecorder: MediaRecorder? = null
    private var surfaceHolder: SurfaceHolder? = null
    private var isRecording = false
    private var outputFile: File? = null
    private var evidenceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Full-screen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_emergency_camera)

        // Get evidence ID from intent
        evidenceId = intent.getStringExtra(EXTRA_EVIDENCE_ID)

        // Setup camera surface
        val surfaceView = findViewById<SurfaceView>(R.id.camera_surface)
        surfaceHolder = surfaceView.holder
        surfaceHolder?.addCallback(this)

        Log.i(TAG, "Emergency Camera Activity created")
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.i(TAG, "Surface created, starting camera")
        if (checkPermissions()) {
            startCameraAndRecording()
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Surface changed - can adjust preview if needed
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.i(TAG, "Surface destroyed")
        releaseCamera()
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCameraAndRecording() {
        try {
            // Open camera
            camera = Camera.open()
            camera?.setDisplayOrientation(90) // Portrait mode

            // Set preview
            camera?.setPreviewDisplay(surfaceHolder)

            // Configure camera parameters
            val parameters = camera?.parameters
            parameters?.let {
                // Set focus mode
                if (it.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    it.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
                }

                // Set optimal preview size
                val sizes = it.supportedPreviewSizes
                val optimalSize = sizes?.firstOrNull { size ->
                    size.width == 1920 && size.height == 1080
                } ?: sizes?.get(0)

                optimalSize?.let { size ->
                    it.setPreviewSize(size.width, size.height)
                }

                camera?.parameters = it
            }

            camera?.startPreview()
            Log.i(TAG, "✓ Camera preview started")

            // Start recording after a short delay
            surfaceHolder?.let {
                android.os.Handler(mainLooper).postDelayed({
                    startRecording()
                }, 500) // 500ms delay to ensure camera is ready
            }

        } catch (e: IOException) {
            Log.e(TAG, "Error starting camera", e)
            Toast.makeText(this, "Failed to start camera", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting camera", e)
            Toast.makeText(this, "Failed to start camera", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun startRecording() {
        if (isRecording) {
            Log.w(TAG, "Already recording")
            return
        }

        try {
            // Create output file
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val fileName = "EMERGENCY_${evidenceId ?: timestamp}.mp4"
            val moviesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
            val shaktiDir = File(moviesDir, "SHAKTI_Emergency")
            shaktiDir.mkdirs()
            outputFile = File(shaktiDir, fileName)

            Log.i(TAG, "Recording to: ${outputFile?.absolutePath}")

            // Setup MediaRecorder
            mediaRecorder = MediaRecorder().apply {
                camera?.unlock()
                setCamera(camera)

                setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
                setVideoSource(MediaRecorder.VideoSource.CAMERA)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)

                // Use high-quality profile if available
                if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH)) {
                    val profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)
                    setVideoEncodingBitRate(profile.videoBitRate)
                    setVideoFrameRate(profile.videoFrameRate)
                    setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight)
                    setAudioEncodingBitRate(profile.audioBitRate)
                    setAudioSamplingRate(profile.audioSampleRate)
                } else {
                    setVideoEncodingBitRate(3000000)
                    setVideoFrameRate(30)
                    setVideoSize(1920, 1080)
                    setAudioEncodingBitRate(128000)
                    setAudioSamplingRate(44100)
                }

                setOutputFile(outputFile?.absolutePath)
                setPreviewDisplay(surfaceHolder?.surface)
                setOrientationHint(90) // Portrait mode

                prepare()
                start()
            }

            isRecording = true
            Log.i(TAG, "✓ Recording started automatically")
            Toast.makeText(this, "🔴 Emergency Recording Started", Toast.LENGTH_LONG).show()

        } catch (e: IOException) {
            Log.e(TAG, "Error starting recording", e)
            Toast.makeText(this, "Failed to start recording", Toast.LENGTH_SHORT).show()
            releaseMediaRecorder()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting recording", e)
            Toast.makeText(this, "Failed to start recording", Toast.LENGTH_SHORT).show()
            releaseMediaRecorder()
        }
    }

    fun stopRecording() {
        if (!isRecording) return

        try {
            mediaRecorder?.stop()
            Log.i(TAG, "✓ Recording stopped")
            Toast.makeText(
                this,
                "Recording saved: ${outputFile?.name}",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping recording", e)
        } finally {
            releaseMediaRecorder()
            isRecording = false
        }
    }

    private fun releaseMediaRecorder() {
        mediaRecorder?.apply {
            reset()
            release()
        }
        mediaRecorder = null
        camera?.lock()
    }

    private fun releaseCamera() {
        if (isRecording) {
            stopRecording()
        }
        camera?.apply {
            stopPreview()
            release()
        }
        camera = null
    }

    override fun onPause() {
        super.onPause()
        releaseCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseCamera()
    }

    override fun onBackPressed() {
        // Confirm before exiting
        if (isRecording) {
            stopRecording()
        }
        super.onBackPressed()
    }

    // Click handler for stop button
    fun onStopButtonClicked(view: android.view.View) {
        if (isRecording) {
            stopRecording()
        }
        finish()
    }
}
