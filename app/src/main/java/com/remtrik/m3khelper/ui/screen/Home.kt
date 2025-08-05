package com.remtrik.m3khelper.ui.screen

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.remtrik.m3khelper.R
import com.remtrik.m3khelper.ui.component.BackupButton
import com.remtrik.m3khelper.ui.component.DeviceImage
import com.remtrik.m3khelper.ui.component.ErrorDialog
import com.remtrik.m3khelper.ui.component.InfoCard
import com.remtrik.m3khelper.ui.component.MountButton
import com.remtrik.m3khelper.ui.component.QuickBootButton
import com.remtrik.m3khelper.ui.component.TopAppBar
import com.remtrik.m3khelper.util.Device
import com.remtrik.m3khelper.util.PaddingValue
import com.remtrik.m3khelper.util.sdp
import com.remtrik.m3khelper.util.showBootBackupErorDialog
import com.remtrik.m3khelper.util.showUEFIFlashErorDialog

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Destination<RootGraph>(start = true)
@Composable
fun HomeScreen(navigator: DestinationsNavigator) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigator = navigator,
                text = R.string.app_name,
                isNavigate = true,
                isPopBack = false,
                destination = SettingsScreenDestination,
                icon = Filled.Settings
            )
        }
    )
    { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(10.sdp()),
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(horizontal = PaddingValue)
                .fillMaxWidth()
                .fillMaxHeight(),
        ) {
            ErrorDialogs()
            if (isLandscape) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.sdp()),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.sdp()),
                        modifier = Modifier.width(300.sdp())
                    ) {
                        DeviceInfo(
                            Modifier
                                .height(210.sdp())
                                .width(300.sdp())
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.sdp()),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Buttons()
                    }
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.sdp())
                ) {
                    DeviceInfo(Modifier.height(416.sdp()))
                }
                Buttons()
            }
        }
    }
}

@Composable
private fun Buttons() {
    when {
        !Device.currentDeviceCard.noBoot -> {
            BackupButton()
        }
    }

    when {
        !Device.currentDeviceCard.noMount -> {
            MountButton()
        }
    }
    when {
        !Device.currentDeviceCard.noFlash -> {
            QuickBootButton()
        }
    }
}

@Composable
private fun DeviceInfo(modifier: Modifier) {
    DeviceImage(modifier)
    InfoCard(modifier)
}

@Composable
private fun ErrorDialogs() {
    when {
        showUEFIFlashErorDialog.value -> {
            ErrorDialog(
                title = "woops",
                description = "failed to flash uefi",
                showDialog = showUEFIFlashErorDialog.value,
                onDismiss = { showUEFIFlashErorDialog.value = false }
            )
        }
    }
    when {
        showBootBackupErorDialog.value -> {
            ErrorDialog(
                title = "woops",
                description = "failed to backup boot",
                showDialog = showBootBackupErorDialog.value,
                onDismiss = { showBootBackupErorDialog.value = false }
            )
        }
    }
}