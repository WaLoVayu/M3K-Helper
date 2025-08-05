package com.remtrik.m3khelper.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Assigns values to the variables above
@Composable
private fun rememberApproximatedWidth(): Int {
    val config = LocalConfiguration.current
    val smallestWidth = config.smallestScreenWidthDp
    return remember(smallestWidth) { approximateWidth(smallestWidth) }
}

// Will return the smallestWidth approximated to nearest 30 to improve performance
private fun approximateWidth(value: Int): Int {
    return (value + 15) / 30 * 30
}

@Composable
fun Int.sdp(): Dp {
    val approxWidth = rememberApproximatedWidth()
    val ratio = remember(approxWidth) {
        when {
            approxWidth <= 400 -> approxWidth / 440.0
            approxWidth <= 550 -> approxWidth / 450.0
            else -> approxWidth / 650.0
        }
    }
    return (this * ratio).dp
}

@Composable
fun Int.ssp(): TextUnit {
    val approxWidth = rememberApproximatedWidth()
    val ratio = remember(approxWidth) {
        when {
            approxWidth <= 400 -> approxWidth / 500.0
            approxWidth <= 450 -> approxWidth / 450.0
            approxWidth <= 550 -> approxWidth / 500.0
            else -> approxWidth / 650.0
        }
    }
    return (this * ratio).sp
}