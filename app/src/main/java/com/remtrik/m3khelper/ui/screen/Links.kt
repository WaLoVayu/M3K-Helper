package com.remtrik.m3khelper.ui.screen

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.remtrik.m3khelper.M3KApp
import com.remtrik.m3khelper.R
import com.remtrik.m3khelper.ui.component.LinkButton
import com.remtrik.m3khelper.ui.component.TopAppBar
import com.remtrik.m3khelper.util.Device
import com.remtrik.m3khelper.util.PaddingValue
import com.remtrik.m3khelper.util.sdp

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Destination<RootGraph>()
@Composable
fun LinksScreen(navigator: DestinationsNavigator) {
    val isLandscape = M3KApp.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigator = navigator,
                text = R.string.links,
                isNavigate = false,
                isPopBack = false
            )
        })
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
            if (isLandscape) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.sdp()),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.sdp()),
                        modifier = Modifier.width(500.sdp())
                    ) { FilesLinks() }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.sdp()),
                        modifier = Modifier.width(500.sdp())
                    ) { SocialLinks() }
                }
            } else {
                FilesLinks()
                SocialLinks()
            }
        }
    }
}

@Composable
private fun FilesLinks() {
    if (Device.currentDeviceCard.unifiedDriversUEFI
        && !(Device.currentDeviceCard.noDrivers || Device.currentDeviceCard.noUEFI)
    ) {
        LinkButton(
            stringResource(
                R.string.driversuefi,
                Device.currentDeviceCard.deviceName
            ),
            null,
            Device.currentDeviceCard.deviceDrivers,
            R.drawable.ic_drivers,
            LocalUriHandler.current
        )
    } else {
        when {
            !Device.currentDeviceCard.noDrivers -> {
                LinkButton(
                    stringResource(
                        R.string.drivers,
                        Device.currentDeviceCard.deviceName
                    ),
                    null,
                    Device.currentDeviceCard.deviceDrivers,
                    R.drawable.ic_drivers,
                    LocalUriHandler.current
                )
            }
        }
        when {
            !Device.currentDeviceCard.noUEFI -> {
                LinkButton(
                    stringResource(
                        R.string.uefi,
                        Device.currentDeviceCard.deviceName
                    ),
                    null,
                    Device.currentDeviceCard.deviceUEFI,
                    R.drawable.ic_uefi,
                    LocalUriHandler.current
                )
            }
        }
    }
}

@Composable
private fun SocialLinks() {
    when {
        !Device.currentDeviceCard.noGroup -> {
            LinkButton(
                stringResource(R.string.group, Device.currentDeviceCard.deviceName),
                null,
                Device.currentDeviceCard.deviceGroup,
                Icons.AutoMirrored.Filled.Message,
                LocalUriHandler.current
            )
        }
    }
    when {
        !Device.currentDeviceCard.noGuide -> {
            LinkButton(
                stringResource(R.string.guide, Device.currentDeviceCard.deviceName),
                null,
                Device.currentDeviceCard.deviceGuide,
                Icons.Filled.Book,
                LocalUriHandler.current
            )
        }
    }
}