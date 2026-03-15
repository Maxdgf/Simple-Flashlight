package com.example.flashlight.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private const val WIDTH = 40 // indicator width
private const val HEIGHT = 20 // indicator height

/**
 * Creates a mini ui battery level indicator.
 * @param percent current percent.
 * @param lowLevel low battery mark.
 */
@Composable
fun MiniBatteryUiIndicator(
    percent: Int,
    lowLevel: Int = 5
) {
    // battery indicator color
    val color =
        if (percent <= lowLevel) Color.Red
        else MaterialTheme.colorScheme.onPrimary

    // battery indicator
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(
                    width = WIDTH.dp,
                    height = HEIGHT.dp
                )
                .border(
                    width = 2.dp,
                    shape = RoundedCornerShape(5.dp),
                    color = color
                )
                .padding(3.dp)
        ) {
            // calculate bar width
            val barWidth = percent * WIDTH / 100

            // bar
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(barWidth.dp)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }

        Box(
            modifier = Modifier
                .size(
                    width = 3.dp,
                    height = 10.dp
                )
                .background(color = color)
        )
    }
}