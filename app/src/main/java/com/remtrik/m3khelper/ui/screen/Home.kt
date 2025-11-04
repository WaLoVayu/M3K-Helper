package com.remtrik.m3khelper.ui.screen

import android.R.attr.maxWidth
import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
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
import com.remtrik.m3khelper.ui.component.CommonTopAppBar
import com.remtrik.m3khelper.util.DeviceCard
import com.remtrik.m3khelper.util.variables.device
import com.remtrik.m3khelper.util.variables.PaddingValue
import com.remtrik.m3khelper.util.variables.commandError
import com.remtrik.m3khelper.util.variables.sdp
import com.remtrik.m3khelper.util.variables.showBootBackupErrorDialog
import com.remtrik.m3khelper.util.variables.showMountErrorDialog
import com.remtrik.m3khelper.util.variables.showQuickBootErrorDialog

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Destination<RootGraph>(start = true)
@Composable
fun HomeScreen(navigator: DestinationsNavigator) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    val spacing = 10.sdp()
    val padding = remember { PaddingValue }

    val deviceCard by derivedStateOf { device.currentDeviceCard }

    val bootError by remember { derivedStateOf { showBootBackupErrorDialog.value } }
    val mountError by remember { derivedStateOf { showMountErrorDialog.value } }
    val quickBootError by remember { derivedStateOf { showQuickBootErrorDialog.value } }
    val commandErrorText by remember { derivedStateOf { commandError.value } }

    println(maxWidth)

    Scaffold(
        topBar = {
            CommonTopAppBar(
                navigator = navigator,
                text = R.string.app_name,
                isNavigate = if (isLandscape) null else true,
                destination = SettingsScreenDestination,
                icon = Filled.Settings,
            )
        },
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing),
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = padding,
                    end = padding
                )
                .fillMaxSize()
        ) {
            if (isLandscape) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing),
                        modifier = Modifier.width(300.sdp())
                    ) {
                        DeviceInfo(
                            Modifier
                                .height(210.sdp())
                                .width(300.sdp())
                        )
                    }
                    Buttons(deviceCard)
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.sdp())
                ) {
                    DeviceInfo(Modifier.height(416.sdp()))
                }
                Buttons(deviceCard)
            }
        }

        ErrorDialogs(
            bootErrorVisible = bootError,
            mountErrorVisible = mountError,
            quickBootErrorVisible = quickBootError,
            errorText = commandErrorText
        )
    }
}

@Composable
private fun Buttons(deviceCard: DeviceCard) {
    Column(verticalArrangement = Arrangement.spacedBy(10.sdp())) {
        if (!deviceCard.noBoot) BackupButton()
        if (!deviceCard.noMount) MountButton()
        if (!deviceCard.noFlash) QuickBootButton()
    }
}

@Composable
private fun DeviceInfo(modifier: Modifier) {
    DeviceImage(modifier)
    InfoCard(modifier)
}

@Composable
private fun ErrorDialogs(
    bootErrorVisible: Boolean,
    mountErrorVisible: Boolean,
    quickBootErrorVisible: Boolean,
    errorText: String
) {
    if (bootErrorVisible) {
        ErrorDialog(
            title = stringResource(R.string.backupboot_error),
            description = stringResource(R.string.error_reason, errorText),
            showDialog = true,
            onDismiss = { showBootBackupErrorDialog.value = false }
        )
    }

    if (mountErrorVisible) {
        ErrorDialog(
            title = "Failed to mount/unmount Windows",
            description = stringResource(R.string.error_reason, errorText),
            showDialog = true,
            onDismiss = { showMountErrorDialog.value = false }
        )
    }

    if (quickBootErrorVisible) {
        ErrorDialog(
            title = "Failed to QuickBoot to Windows",
            description = stringResource(R.string.error_reason, errorText),
            showDialog = true,
            onDismiss = { showQuickBootErrorDialog.value = false }
        )
    }
}