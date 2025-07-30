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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.remtrik.m3khelper.M3KApp
import com.remtrik.m3khelper.R.string
import com.remtrik.m3khelper.util.BootIsPresent
import com.remtrik.m3khelper.util.Device
import com.remtrik.m3khelper.util.FontSize
import com.remtrik.m3khelper.util.LineHeight
import com.remtrik.m3khelper.util.PaddingValue
import com.remtrik.m3khelper.util.WindowsIsPresent
import com.remtrik.m3khelper.util.specialDeviceCardsArray
import com.remtrik.m3khelper.util.sdp

@Composable
fun InfoCard(modifier: Modifier) {
    ElevatedCard(
        modifier =
            if (specialDeviceCardsArray.contains(Device.currentDeviceCard) && M3KApp.resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
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
                text = M3KApp.getString(string.woa),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = FontSize,
                lineHeight = LineHeight
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = PaddingValue),
                text = M3KApp.getString(
                    string.model,
                    Device.currentDeviceCard.deviceName,
                    Device.currentDeviceCard.deviceCodename[0]
                ),
                fontSize = FontSize,
                lineHeight = LineHeight
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = PaddingValue),
                text = M3KApp.getString(string.ramvalue, Device.ram),
                fontSize = FontSize,
                lineHeight = LineHeight
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = PaddingValue),
                text = M3KApp.getString(string.paneltype, Device.panelType.value),
                fontSize = FontSize,
                lineHeight = LineHeight
            )
            when {
                !Device.currentDeviceCard.noBoot && !Device.currentDeviceCard.noMount -> {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = PaddingValue),
                        text = M3KApp.getString(
                            string.backup_boot_state,
                            M3KApp.getString(BootIsPresent.value)
                        ),
                        fontSize = FontSize,
                        lineHeight = LineHeight
                    )
                }
            }
            when {
                Device.slot.isNotEmpty() -> {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = PaddingValue),
                        text = M3KApp.getString(string.slot, Device.slot),
                        fontSize = FontSize,
                        lineHeight = LineHeight
                    )
                }
            }
            when {
                !Device.currentDeviceCard.noMount -> {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.sdp()),
                        text = M3KApp.getString(
                            string.windows_status,
                            M3KApp.getString(WindowsIsPresent.value)
                        ),
                        fontSize = FontSize,
                        lineHeight = LineHeight
                    )
                }
            }
        }
    }
}