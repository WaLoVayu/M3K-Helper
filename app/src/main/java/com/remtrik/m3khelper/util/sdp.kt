package com.remtrik.m3khelper.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.remtrik.m3khelper.util.SdpScreenDimensionValues.smallestWidth

internal object SdpScreenDimensionValues {
    var smallestWidth: Int = 0
}

// Assigns values to the variables above
@Composable
private fun AssValsToTheSdpObject() {
    val config = LocalConfiguration.current
    smallestWidth = config.smallestScreenWidthDp
}

// Will return the smallestWidth approximated to nearest 30 to improve performance
private fun approximateWidth(value: Int): Int {
    val remainder = value % 30
    return when {
        remainder <= 15 -> {
            value - remainder
        }

        else -> {
            value + (30 - remainder)
        }
    }
}

@Composable
fun Int.sdp(): Dp {
    if (smallestWidth == 0) {
        AssValsToTheSdpObject()
    }
    val ratio = when {
        smallestWidth <= 400 -> {
            approximateWidth(smallestWidth) / 440.0
        }

        smallestWidth <= 450 -> {
            approximateWidth(smallestWidth) / 450.0
        }

        smallestWidth <= 550 -> {
            approximateWidth(smallestWidth) / 450.0
        }

        else -> {
            approximateWidth(smallestWidth) / 650.0
        }
    }
    val final = this * ratio
    return final.dp
}

@Composable
fun Int.ssp(): TextUnit {
    if (smallestWidth == 0) {
        AssValsToTheSdpObject()
    }
    val ratio = when {
        smallestWidth <= 400 -> {
            approximateWidth(smallestWidth) / 500.0
        }

        smallestWidth <= 450 -> {
            approximateWidth(smallestWidth) / 450.0
        }

        smallestWidth <= 550 -> {
            approximateWidth(smallestWidth) / 500.0
        }

        else -> {
            approximateWidth(smallestWidth) / 650.0
        }
    }
    val final = this * ratio
    return final.sp
}