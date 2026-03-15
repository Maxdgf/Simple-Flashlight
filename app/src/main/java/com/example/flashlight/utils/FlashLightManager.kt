package com.example.flashlight.utils

import android.content.Context
import android.hardware.camera2.CameraManager
import java.lang.Exception

class FlashLightManager(private val context: Context) {
    // get camera system service
    val cameraService = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    var cameraId: String? = null

    init {
        cameraId = cameraService.getBackCameraId()
    }

    /**
     * Turns on phone flashlight.
     * @return boolean success state.
     */
    private fun turnOn(): Boolean =
        try {
            // manage torch mode by camera id
            cameraId?.let { id ->
                cameraService.setTorchMode(id, true)
                true
            } == true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    /**
     * Turns off phone flashlight.
     * @return boolean success state.
     */
    private fun turnOff(): Boolean =
        try {
            // manage torch mode by camera id
            cameraId?.let { id ->
                cameraService.setTorchMode(id, false)
                true
            } == true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    /**
     * Toggles torch mode by state.
     * @param state torch state.
     * @return boolean success state.
     */
    fun toggleFlashLight(state: Boolean): Boolean =
        try {
            when (state) {
                true -> turnOn() // turn on torch
                false -> turnOff() // turn off torch
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    /**
     * Returns phone back camera id.
     * @return nullable string.
     */
    private fun CameraManager.getBackCameraId(): String? =
        try {
            cameraIdList[0]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
}