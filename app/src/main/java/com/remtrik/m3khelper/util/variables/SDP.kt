package com.remtrik.m3khelper.util.variables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class WidthRatioConfig(
    val widthThreshold: Int,
    val divisor: Double
)

@Composable
private fun rememberApproximatedWidth(): Int {
    val config = LocalConfiguration.current
    val smallestWidth = config.smallestScreenWidthDp
    return remember(smallestWidth) { approximateWidth(smallestWidth) }
}

private fun approximateWidth(value: Int): Int {
    return (value + 15) / 30 * 30
}

@Composable
fun Int.sdp(): Dp {
    val ratio = rememberWidthRatio(sdpRatioConfigs)
    return (this * ratio).dp
}

@Composable
fun Int.ssp(): TextUnit {
    val ratio = rememberWidthRatio(sspRatioConfigs)
    return (this * ratio).sp
}

@Composable
private fun rememberWidthRatio(configs: List<WidthRatioConfig>): Double {
    val approxWidth = rememberApproximatedWidth()
    return remember(approxWidth) {
        calculateRatio(approxWidth, configs)
    }
}

private fun calculateRatio(width: Int, configs: List<WidthRatioConfig>): Double {
    val matchingConfig = configs.find { width <= it.widthThreshold }
    return width / (matchingConfig?.divisor ?: configs.last().divisor)
}

private val sdpRatioConfigs = listOf(
    WidthRatioConfig(widthThreshold = 400, divisor = 440.0),
    WidthRatioConfig(widthThreshold = 550, divisor = 450.0),
    WidthRatioConfig(widthThreshold = Int.MAX_VALUE, divisor = 650.0)
)

private val sspRatioConfigs = listOf(
    WidthRatioConfig(widthThreshold = 400, divisor = 500.0),
    WidthRatioConfig(widthThreshold = 450, divisor = 450.0),
    WidthRatioConfig(widthThreshold = 550, divisor = 500.0),
    WidthRatioConfig(widthThreshold = Int.MAX_VALUE, divisor = 650.0)
)