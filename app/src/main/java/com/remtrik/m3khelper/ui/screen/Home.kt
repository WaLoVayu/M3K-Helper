package com.remtrik.m3khelper.ui.screen

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.remtrik.m3khelper.M3KApp
import com.remtrik.m3khelper.R
import com.remtrik.m3khelper.ui.component.BackupButton
import com.remtrik.m3khelper.ui.component.DeviceImage
import com.remtrik.m3khelper.ui.component.InfoCard
import com.remtrik.m3khelper.ui.component.MountButton
import com.remtrik.m3khelper.ui.component.QuickBootButton
import com.remtrik.m3khelper.util.CurrentDeviceCard
import com.remtrik.m3khelper.util.FontSize
import com.remtrik.m3khelper.util.PaddingValue
import com.remtrik.m3khelper.util.sdp

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Destination<RootGraph>(start = true)
@Composable
fun HomeScreen(navigator: DestinationsNavigator) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = M3KApp.getString(R.string.app_name),
                        fontSize = FontSize,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { navigator.navigate(SettingsScreenDestination) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = null
                        )
                    }
                },
            )
        }
    )
    { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(10.sdp()),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = PaddingValue)
                .fillMaxWidth(),
        ) {
            if (M3KApp.resources.configuration.orientation != Configuration.ORIENTATION_PORTRAIT) Landscape()
            else Portrait()
        }
    }
}

@Composable
private fun Landscape() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.sdp()),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.sdp()),
            modifier = Modifier.width(300.sdp())
        ) {
            InfoCard(
                Modifier
                    .height(210.sdp())
                    .width(300.sdp())
            )
            DeviceImage(
                Modifier
                    .height(210.sdp())
                    .width(300.sdp())
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(10.sdp()),
            modifier = Modifier.fillMaxWidth()
        ) {
            when {
                !CurrentDeviceCard.noBoot -> {
                    BackupButton()
                }
            }
            when {
                !CurrentDeviceCard.noMount -> {
                    MountButton()
                }
            }
            when {
                !CurrentDeviceCard.noFlash -> {
                    QuickBootButton()
                }
            }
        }
    }
}

@Composable
private fun Portrait() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.sdp())
    ) {
        DeviceImage(
            Modifier
                .height(416.sdp())
        )
        InfoCard(
            Modifier
                .height(416.sdp())
        )
    }
    when {
        !CurrentDeviceCard.noBoot -> {
            BackupButton()
        }
    }

    when {
        !CurrentDeviceCard.noMount -> {
            MountButton()
        }
    }
    when {
        !CurrentDeviceCard.noFlash -> {
            QuickBootButton()
        }
    }
}