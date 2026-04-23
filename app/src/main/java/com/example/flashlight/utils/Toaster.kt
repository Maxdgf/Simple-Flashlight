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
    ) {
        // show toast message (long or short by time)
        if (isLong)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show() // long
        else
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show() // short
    }
}