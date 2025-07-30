package com.remtrik.m3khelper.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.remtrik.m3khelper.util.Device
import com.remtrik.m3khelper.util.specialDeviceCardsArray
import com.remtrik.m3khelper.util.sdp

@Composable
fun DeviceImage(modifier: Modifier) {
    Image(
        alignment = Alignment.TopStart,
        modifier = if (specialDeviceCardsArray.contains(Device.currentDeviceCard)) {
            modifier
        } else {
            Modifier
                .height(210.sdp())
        },
        painter = painterResource(id = Device.currentDeviceCard.deviceImage),
        contentDescription = null,
    )
}