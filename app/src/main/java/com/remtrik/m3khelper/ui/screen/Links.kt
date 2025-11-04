package com.remtrik.m3khelper.ui.screen

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.remtrik.m3khelper.R
import com.remtrik.m3khelper.ui.component.LinkButton
import com.remtrik.m3khelper.ui.component.CommonTopAppBar
import com.remtrik.m3khelper.util.DeviceCard
import com.remtrik.m3khelper.util.variables.device
import com.remtrik.m3khelper.util.variables.PaddingValue
import com.remtrik.m3khelper.util.variables.sdp

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Destination<RootGraph>()
@Composable
fun LinksScreen(navigator: DestinationsNavigator) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CommonTopAppBar(
                navigator = navigator,
                text = R.string.links,
            )
        }
    ) { innerPadding ->
        LinksContent(
            isLandscape = isLandscape,
            scrollState = scrollState,
            innerPadding = innerPadding
        )
    }
}

@Composable
private fun LinksContent(
    isLandscape: Boolean,
    scrollState: ScrollState,
    innerPadding: PaddingValues
) {
    val spacing = 10.sdp()
    val deviceCard = remember { device.currentDeviceCard }
    val uriHandler = LocalUriHandler.current

    Column(
        verticalArrangement = Arrangement.spacedBy(10.sdp()),
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(
                top = innerPadding.calculateTopPadding(),
                start = PaddingValue,
                end = PaddingValue
            )
            .fillMaxSize()
    ) {
        if (isLandscape) {
            LandscapeLinksLayout(spacing, deviceCard, uriHandler)
        } else {
            PortraitLinksLayout(spacing, deviceCard, uriHandler)
        }
    }
}

@Composable
private fun LandscapeLinksLayout(
    spacing: Dp,
    deviceCard: DeviceCard,
    uriHandler: UriHandler
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing),
        modifier = Modifier.fillMaxWidth()
    ) {
        LinksColumn(
            modifier = Modifier.width(500.sdp())
        ) {
            FilesLinks(deviceCard, uriHandler)
        }
        LinksColumn(
            modifier = Modifier.width(500.sdp())
        ) {
            SocialLinks(deviceCard, uriHandler)
        }
    }
}

@Composable
private fun PortraitLinksLayout(
    spacing: Dp,
    deviceCard: DeviceCard,
    uriHandler: UriHandler
) {
    FilesLinks(deviceCard, uriHandler)
    SocialLinks(deviceCard, uriHandler)
}

@Composable
private fun LinksColumn(
    modifier: Modifier = Modifier,
    verticalSpacing: Dp = 10.sdp(),
    content: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        modifier = modifier
    ) {
        content()
    }
}

@Composable
private fun FilesLinks(
    deviceCard: DeviceCard,
    uriHandler: UriHandler
) {
    when {
        deviceCard.unifiedDriversUEFI &&
                !(deviceCard.noDrivers || deviceCard.noUEFI) -> {
            LinkButton(
                title = stringResource(R.string.driversuefi, deviceCard.deviceName),
                subtitle = null,
                link = deviceCard.driversLink,
                icon = R.drawable.ic_drivers,
                uriHandler = uriHandler
            )
        }

        !deviceCard.noDrivers && !deviceCard.unifiedDriversUEFI -> {
            LinkButton(
                title = stringResource(R.string.drivers, deviceCard.deviceName),
                subtitle = null,
                link = deviceCard.driversLink,
                icon = R.drawable.ic_drivers,
                uriHandler = uriHandler
            )
        }

        !deviceCard.noUEFI && !deviceCard.unifiedDriversUEFI -> {
            LinkButton(
                title = stringResource(R.string.uefi, deviceCard.deviceName),
                subtitle = null,
                link = deviceCard.uefiLink,
                icon = R.drawable.ic_uefi,
                uriHandler = uriHandler
            )
        }
    }
}

@Composable
private fun SocialLinks(
    deviceCard: DeviceCard,
    uriHandler: UriHandler
) {
    if (!deviceCard.noGroup) {
        LinkButton(
            title = stringResource(R.string.group, deviceCard.deviceName),
            subtitle = null,
            link = deviceCard.groupLink,
            icon = Icons.AutoMirrored.Filled.Message,
            uriHandler = uriHandler
        )
    }

    if (!deviceCard.noGuide) {
        LinkButton(
            title = stringResource(R.string.guide, deviceCard.deviceName),
            subtitle = null,
            link = deviceCard.deviceGuide,
            icon = Icons.Filled.Book,
            uriHandler = uriHandler
        )
    }
}