package com.example.flashlight.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Creates a styled vertical flashlight toggle.
 *
 * @param isFlashLightOn flashlight state.
 * @param onToggle flashlight toggle function.
 */
@Composable
fun FlashlightUiToggle(
    isFlashLightOn: Boolean,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(
                color = Color(0xFF252525),
                shape = CircleShape
            )
            .clip(shape = CircleShape)
            .border(
                width = 5.dp,
                color = Color(0xFF333333),
                shape = CircleShape
            )
            .clickable(onClick = { onToggle() })
            .size(
                width = 135.dp,
                height = 260.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val animatedWeight1 by animateFloatAsState(
            if (!isFlashLightOn) 1f
            else 0.05f
        )
        Spacer(modifier = Modifier.weight(animatedWeight1))

        val animatedColor by animateColorAsState(
            if (!isFlashLightOn) Color(0xFF4CAF50) // enabled
            else Color(0xFFC92020)                 // disabled
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = animatedColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (!isFlashLightOn) "ON" else "OFF", // ON/OFF caption
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 30.sp
            )
        }

        val animatedWeight2 by animateFloatAsState(
            if (!isFlashLightOn) 0.05f
            else 1f
        )
        Spacer(modifier = Modifier.weight(animatedWeight2))
    }
}