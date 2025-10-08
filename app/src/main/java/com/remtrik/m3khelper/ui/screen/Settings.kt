package com.remtrik.m3khelper.ui.screen

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.core.content.edit
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ThemeEngineScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.remtrik.m3khelper.M3KApp
import com.remtrik.m3khelper.R
import com.remtrik.m3khelper.prefs
import com.remtrik.m3khelper.ui.component.AboutCard
import com.remtrik.m3khelper.ui.component.ButtonItem
import com.remtrik.m3khelper.ui.component.SwitchItem
import com.remtrik.m3khelper.ui.component.CommonTopAppBar
import com.remtrik.m3khelper.util.beyond1Card
import com.remtrik.m3khelper.util.collapseTransition
import com.remtrik.m3khelper.util.debugCard
import com.remtrik.m3khelper.util.deviceCardsArray
import com.remtrik.m3khelper.util.expandTransition
import com.remtrik.m3khelper.util.unknownCard
import com.remtrik.m3khelper.util.variables.Device
import com.remtrik.m3khelper.util.variables.FontSize
import com.remtrik.m3khelper.util.variables.LineHeight
import com.remtrik.m3khelper.util.variables.PaddingValue
import com.remtrik.m3khelper.util.variables.fastLoadSavedDevice
import com.remtrik.m3khelper.util.variables.sdp
import com.remtrik.m3khelper.util.variables.showAboutCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun SettingsScreen(navigator: DestinationsNavigator) {
    var checkUpdate by rememberSaveable {
        mutableStateOf(
            prefs.getBoolean("check_update", true)
        )
    }
    var forceRotation by rememberSaveable {
        mutableStateOf(
            prefs.getBoolean("force_rotation", false)
        )
    }
    var overrideDevice by rememberSaveable {
        mutableStateOf(
            prefs.getBoolean("override_device", false)
        )
    }
    var overridenDeviceName by rememberSaveable {
        mutableStateOf(
            prefs.getString("overriden_device_name", "Poco X3 Pro")
        )
    }

    var expanded by rememberSaveable { mutableStateOf(false) }

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CommonTopAppBar(
                navigator = navigator,
                text = R.string.settings,
                isPopBack = if (isLandscape) null else true,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(top = innerPadding.calculateTopPadding())
                .padding(horizontal = PaddingValue)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            ButtonItem(
                icon = Icons.Filled.Style,
                title = stringResource(R.string.theme_engine),
                onClick = { navigator.navigate(ThemeEngineScreenDestination) }
            )
            SwitchItem(
                icon = Icons.Filled.Update,
                title = stringResource(R.string.autoupdate),
                summary = stringResource(R.string.autoupdate_summary),
                checked = checkUpdate
            ) {
                scope.launch(Dispatchers.IO) {
                    prefs.edit { putBoolean("check_update", it) }
                    checkUpdate = it
                }
            }
            SwitchItem(
                icon = Icons.Filled.DevicesOther,
                title = stringResource(R.string.override_device),
                summary = stringResource(R.string.override_device_summary),
                checked = overrideDevice
            ) {
                scope.launch(Dispatchers.IO) {
                    prefs.edit { putBoolean("override_device", it) }
                    overrideDevice = it
                    fastLoadSavedDevice(it)
                }
            }
            AnimatedVisibility(
                visible = overrideDevice,
                enter = expandTransition,
                exit = collapseTransition,
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent,
                    ),
                    modifier = Modifier.padding(PaddingValue)
                ) {
                    Button(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.sdp())
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.device,
                                    overridenDeviceName ?: "Unknown"
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = FontSize,
                                lineHeight = LineHeight
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .padding(PaddingValue)
                            .width(250.sdp())
                    ) {
                        deviceCardsArray
                            .filterNot {
                                it in listOf(
                                    beyond1Card,
                                    debugCard,
                                    unknownCard,
                                    Device.savedDeviceCard,
                                    Device.currentDeviceCard
                                )
                            }
                            .forEach {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = it.deviceName,
                                            fontSize = FontSize,
                                            lineHeight = LineHeight
                                        )
                                    },
                                    onClick = {
                                        kotlinx.coroutines.runBlocking(Dispatchers.IO) {
                                            prefs.edit {
                                                putString(
                                                    "overriden_device_codename",
                                                    it.deviceCodename[0]
                                                )
                                                putString(
                                                    "overriden_device_name",
                                                    it.deviceName
                                                )
                                            }
                                            overridenDeviceName = it.deviceName
                                            expanded = !expanded

                                            fastLoadSavedDevice(true)
                                        }
                                    }
                                )
                            }
                    }
                }
            }
            AnimatedVisibility(
                visible = !Device.special.value,
                enter = expandTransition,
                exit = collapseTransition,
                modifier = Modifier.fillMaxWidth()
            ) {
                SwitchItem(
                    icon = R.drawable.ic_rotation,
                    title = stringResource(R.string.force_rotation),
                    summary = stringResource(R.string.force_rotation_summary),
                    checked = forceRotation
                ) {
                    kotlinx.coroutines.runBlocking(Dispatchers.IO) {
                        prefs.edit { putBoolean("force_rotation", it) }
                        forceRotation = it
                        if (it) M3KApp.resources.configuration.orientation =
                            Configuration.ORIENTATION_UNDEFINED
                        else Configuration.ORIENTATION_PORTRAIT
                    }
                }
            }
            ButtonItem(
                icon = Icons.Filled.Info,
                title = stringResource(R.string.about),
                onClick = { showAboutCard.value = true }
            )
            when {
                showAboutCard.value -> {
                    AboutCard()
                }
            }
        }
    }
}