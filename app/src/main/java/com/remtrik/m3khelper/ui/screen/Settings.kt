package com.remtrik.m3khelper.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.edit
import androidx.lifecycle.compose.dropUnlessResumed
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ThemeEngineScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.remtrik.m3khelper.R
import com.remtrik.m3khelper.ui.component.SwitchItem
import com.remtrik.m3khelper.util.FontSize
import com.remtrik.m3khelper.util.PaddingValue
import com.remtrik.m3khelper.util.deviceCardsArray
import com.remtrik.m3khelper.util.sdp
import com.remtrik.m3khelper.util.showAboutCard

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun SettingsScreen(navigator: DestinationsNavigator) {
    var expanded by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = dropUnlessResumed { navigator.popBackStack() },
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        fontSize = FontSize,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
        ) {
            val context = LocalContext.current
            val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            var checkUpdate by rememberSaveable {
                mutableStateOf(
                    prefs.getBoolean("check_update", true)
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
            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Filled.Palette,
                        stringResource(R.string.themeengine)
                    )
                },
                headlineContent = { Text(stringResource(R.string.themeengine), fontSize = FontSize) },
                modifier = Modifier.clickable {
                    navigator.navigate(ThemeEngineScreenDestination)
                }
            )
            SwitchItem(
                icon = Icons.Filled.Update,
                title = stringResource(R.string.autoupdate),
                summary = stringResource(R.string.autoupdate_summary),
                checked = checkUpdate
            ) {
                prefs.edit { putBoolean("check_update", it) }
                checkUpdate = it
            }
            SwitchItem(
                icon = Icons.Filled.DevicesOther,
                title = "Override device",
                summary = "Use this if your device is detected as wrong model",
                checked = overrideDevice
            ) {
                prefs.edit { putBoolean("override_device", it) }
                overrideDevice = it
            }
            ElevatedCard(Modifier.padding(PaddingValue)) {
                AnimatedVisibility(
                    visible = overrideDevice,
                    enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
                    modifier = Modifier.padding(PaddingValue).fillMaxWidth()
                ) {
                    Button(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.sdp())
                        ) {
                            Text(text = "device: $overridenDeviceName", modifier = Modifier.fillMaxWidth())
                        }
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        deviceCardsArray.forEach {
                            DropdownMenuItem(
                                text = { Text(it.deviceName) },
                                onClick = {
                                    com.remtrik.m3khelper.util.prefs.edit { putString("overriden_device_codename", it.deviceCodename[0]) }
                                    com.remtrik.m3khelper.util.prefs.edit { putString("overriden_device_name", it.deviceName) }
                                    overridenDeviceName = it.deviceName
                                    expanded = !expanded
                                }
                            )
                        }
                    }
                }
            }
            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Filled.Info,
                        stringResource(R.string.about)
                    )
                },
                headlineContent = { Text(stringResource(R.string.about), fontSize = FontSize) },
                modifier = Modifier.clickable {
                    showAboutCard.value = true
                }
            )
        }
    }
}