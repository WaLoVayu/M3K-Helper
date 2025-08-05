package com.remtrik.m3khelper.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.remtrik.m3khelper.R.string
import com.remtrik.m3khelper.util.Device
import com.remtrik.m3khelper.util.FontSize
import com.remtrik.m3khelper.util.LineHeight
import com.remtrik.m3khelper.util.PaddingValue
import com.remtrik.m3khelper.util.rememberDeviceStrings
import com.remtrik.m3khelper.util.sdp
import com.remtrik.m3khelper.util.string

@Composable
fun InfoCard(modifier: Modifier) {
    val deviceStrings = rememberDeviceStrings()

    ElevatedCard(
        modifier =
            if (Device.special.value && LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE) {
                modifier
            } else {
                Modifier
                    .height(210.sdp())
            },
        shape = RoundedCornerShape(8.sdp()),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.sdp())) {
            Text(
                modifier = Modifier
                    .padding(top = PaddingValue)
                    .fillMaxWidth(),
                text = string.woa.string(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = FontSize,
                lineHeight = LineHeight
            )

            listOfNotNull(
                deviceStrings.model,
                deviceStrings.ram,
                deviceStrings.panel,
                deviceStrings.bootState,
                deviceStrings.slot,
                deviceStrings.windowsStatus
            ).forEach {
                Text(
                    text = it,
                    fontSize = FontSize,
                    lineHeight = LineHeight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = PaddingValue)
                )
            }
        }
    }
}