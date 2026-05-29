package com.example.flashlight.utils

import android.content.Context
import android.widget.Toast

class Toaster(private val context: Context) {
    /**
     * Shows a toast message (long or short by time).
     *
     * @param message message string.
     * @param isLong state show time toast.
     */
    fun showToast(
        message: String,
        isLong: Boolean = false
    ) =
        Toast.makeText(
            context,
            message,
            if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        ).show() // show toast message (long or short by time)
}