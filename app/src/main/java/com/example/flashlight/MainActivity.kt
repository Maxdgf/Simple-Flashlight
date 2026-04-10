package com.example.flashlight

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

import com.example.flashlight.ui.components.MiniBatteryUiIndicator
import com.example.flashlight.ui.theme.FlashlightTheme
import com.example.flashlight.utils.FlashLightManager
import com.example.flashlight.utils.Toaster

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashlightTheme {
                val context = LocalContext.current // local context
                // camera flashlight feature support state
                val isCameraFlashLightSupported = remember {
                    context.packageManager.hasSystemFeature(
                        android.content.pm.PackageManager.FEATURE_CAMERA_FLASH
                    )
                }

                // show ui
                if (isCameraFlashLightSupported) FlashLightScreen(context = context)
                else CameraFlashLightIsNotSupportedScreen()
            }
        }
    }
}

/**
 * Creates a main app screen.
 * @param context local context.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FlashLightScreen(context: Context) {
    val haptic = LocalHapticFeedback.current // haptic feedback

    // toast util
    val toaster = remember { Toaster(context) }
    // camera permission launcher
    val cameraPermission = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    // battery level percent
    var batteryLevel by remember { mutableIntStateOf(0) }
    // low battery level flag
    var isBatteryLevelLow by rememberSaveable { mutableStateOf(false) }
    // flashlight state
    var isFlashLightOn by rememberSaveable { mutableStateOf(false) }
    // flashlight manager
    val flashLightManager: FlashLightManager? = remember(cameraPermission.status.isGranted) {
        // check camera permission
        if (cameraPermission.status.isGranted) FlashLightManager(context) // init flashlight manager
        else null
    }

    // torch synchronization
    DisposableEffect(flashLightManager) {
        // check flashlight manager
        if (flashLightManager == null)
            return@DisposableEffect onDispose { } // return empty onDispose

        // create torch callback
        val torchCallback = object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                super.onTorchModeChanged(cameraId, enabled)
                isFlashLightOn = enabled // update flashlight state
            }
        }
        val cameraManager = flashLightManager.cameraService // get camera manager from flash light utility

        // register torch callback
        cameraManager.registerTorchCallback(torchCallback, null)

        onDispose {
            // unregister torch callback
            cameraManager.unregisterTorchCallback(torchCallback)
        }
    }

    // battery level receiver
    DisposableEffect(Unit) {
        // init battery level receiver
        val batteryLevelReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) // get battery level
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1) // get battery scale
                val levelPercent = (level * 100 / scale.toFloat()).toInt() // convert to percent

                // update states
                // 5 - low battery percent
                isBatteryLevelLow = levelPercent <= 5
                batteryLevel = levelPercent
            }
        }

        // register receiver with intent filter
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(batteryLevelReceiver, intentFilter)

        // first battery data get
        context.registerReceiver(null, intentFilter)?.let { intent ->
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) // get battery level
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1) // get battery scale
            val levelPercent = (level * 100 / scale.toFloat()).toInt() // convert to percent

            // update states
            // 5 - low battery percent
            isBatteryLevelLow = levelPercent <= 5
            batteryLevel = levelPercent
        }

        onDispose {
            // unregister battery level receiver when exiting composition
            context.unregisterReceiver(batteryLevelReceiver)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MiniBatteryUiIndicator(percent = batteryLevel)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "$batteryLevel%",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        // check camera permission status
        if (cameraPermission.status.isGranted) {
            // check battery level
            if (!isBatteryLevelLow) {
                // radial white gradient(light effect)
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .align(Alignment.Center)
                        .background(
                            brush = Brush.radialGradient(
                                colors =
                                    if (isSystemInDarkTheme() && isFlashLightOn) listOf(Color.White, Color.Unspecified)
                                    else listOf(Color.Unspecified, Color.Unspecified)
                            ),
                            shape = CircleShape
                        )
                ) {
                    // turn on/off flashlight button
                    Button(
                        onClick = {
                            flashLightManager?.let { manager ->
                                // perform haptic
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                                val flashLightSuccess = manager.toggleFlashLight(!isFlashLightOn)
                                if (flashLightSuccess)
                                // update flashlight state
                                    isFlashLightOn = !isFlashLightOn
                                else toaster.showToast("flash not respond!")
                            }
                        },
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.Center),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor =
                                if (!isFlashLightOn) Color(0xFF4CAF50) // enabled
                                else Color(0xFFC92020) // disabled
                        )
                    ) {
                        Text(
                            text = if (!isFlashLightOn) "ON" else "OFF",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 30.sp
                        )
                    }
                }
            } else {
                // turn off phone flashlight if low battery
                if (isFlashLightOn)
                    flashLightManager?.let { manager ->
                        val flashLight = manager.toggleFlashLight(false)
                        if (flashLight)
                            // update flashlight state
                            isFlashLightOn = false
                    }

                // low battery level message
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(300.dp)
                        .border(
                            width = 2.dp,
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFF5722)
                        )
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Low battery! Please, charge.🪫",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        } else {
            // permission not granted message
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(300.dp)
                    .border(
                        width = 2.dp,
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFC92020)
                    )
                    .padding(10.dp)
            ) {
                // message to show
                val messageToShow =
                    if (cameraPermission.status.shouldShowRationale)
                        buildAnnotatedString {
                            append("App requires access to the ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("camera")
                            }
                            append(
                                " to work, as it controls the phone's flashlight, " +
                                        "which is part of the camera. Please, request this permission via "
                            )
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("request button")
                            }
                            append(" or ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("app settings button")
                            }
                            append('.')
                        }
                    else
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Camera permission")
                            }
                            append(" is not granted! ")
                            append("Please, request this permission via ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("request button")
                            }
                            append(" or ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("app settings button")
                            }
                            append('.')
                        }

                // message view
                Text(
                    text = messageToShow,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Row {
                    // go to app settings button
                    Button(
                        onClick = {
                            // create app settings intent
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", "com.example.flashlight", null)
                            )
                            context.startActivity(intent) // start intent
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1D93CC),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text(text = "app settings") }

                    Spacer(modifier = Modifier.weight(1f))

                    // request permission button
                    Button(
                        onClick = { cameraPermission.launchPermissionRequest() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text(text = "request") }
                }
            }
        }
    }
}

/**Creates screen which displays message about camera flash is not supported.*/
@Composable
fun CameraFlashLightIsNotSupportedScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .size(300.dp)
                .border(
                    width = 2.dp,
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFF0000)
                )
                .padding(10.dp)
        ) {
            // icon
            Icon(
                painter = painterResource(R.drawable.outline_flashlight_off_24),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(150.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // text message
            Text(
                modifier = Modifier.weight(1f),
                text = buildAnnotatedString {
                    append("☹️Oops, it looks like your device doesn't have or doesn't support ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("camera flash")
                    }
                    append('.')
                }
            )
        }
    }
}