package com.remtrik.m3khelper.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.edit
import androidx.lifecycle.compose.dropUnlessResumed
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.remtrik.m3khelper.M3KApp
import com.remtrik.m3khelper.R
import com.remtrik.m3khelper.ui.component.ButtonItem
import com.remtrik.m3khelper.ui.component.ColorPicker
import com.remtrik.m3khelper.ui.component.SwitchItem
import com.remtrik.m3khelper.ui.theme.PaletteStyle
import com.remtrik.m3khelper.util.FontSize
import com.remtrik.m3khelper.util.LineHeight
import com.remtrik.m3khelper.util.PaddingValue
import com.remtrik.m3khelper.util.prefs
import com.remtrik.m3khelper.util.restart
import com.remtrik.m3khelper.util.sdp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun ThemeEngineScreen(navigator: DestinationsNavigator) {
    var enableThemeEngine by rememberSaveable {
        mutableStateOf(
            prefs.getBoolean("enable_theme_engine", false)
        )
    }
    var enableMaterialU by rememberSaveable {
        mutableStateOf(
            prefs.getBoolean("enable_materialu", true)
        )
    }
    var paletteStyle by rememberSaveable {
        mutableStateOf(
            prefs.getString("paletteStyle", "TonalSpot")
        )
    }
    var expanded by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = dropUnlessResumed { navigator.popBackStack() },
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(20.sdp())
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.theme_engine),
                        fontSize = FontSize,
                        lineHeight = LineHeight,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .fillMaxWidth(),
        ) {
            SwitchItem(
                icon = Icons.Filled.Brush,
                title = stringResource(R.string.enable_materialu),
                summary = stringResource(R.string.enable_materialu_summary),
                checked = enableMaterialU
            ) {
                prefs.edit { putBoolean("enable_materialu", it) }
                enableMaterialU = it
                if (it) {
                    prefs.edit { putBoolean("enable_theme_engine", false) }
                    enableThemeEngine = false
                }
            }
            SwitchItem(
                icon = Icons.Filled.FormatPaint,
                title = stringResource(R.string.theme_engine_enable),
                summary = stringResource(R.string.theme_engine_enable_summary),
                checked = enableThemeEngine
            ) {
                prefs.edit { putBoolean("enable_theme_engine", it) }
                enableThemeEngine = it
                if (it) {
                    prefs.edit { putBoolean("enable_materialu", false) }
                    enableMaterialU = false
                }
            }
            AnimatedVisibility(
                visible = enableThemeEngine,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
                modifier = Modifier.padding(PaddingValue).fillMaxWidth()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier.padding(PaddingValue)
                ) {
                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth().size(25.sdp())
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.sdp())
                        ) {
                            Icon(
                                Icons.Filled.Palette,
                                contentDescription = "More options",
                                modifier = Modifier.size(25.sdp()).align(Alignment.CenterVertically)
                            )
                            Text(
                                text = M3KApp.getString(
                                    R.string.themeenigne_current_palette,
                                    paletteStyle
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
                        modifier = Modifier.padding(PaddingValue)
                    ) {
                        PaletteStyle.entries.forEach {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = it.name,
                                        fontSize = FontSize,
                                        lineHeight = LineHeight
                                    )
                                },
                                onClick = {
                                    prefs.edit {
                                        putString(
                                            "paletteStyle",
                                            it.name
                                        )
                                    }; paletteStyle = it.name; expanded = !expanded
                                }
                            )
                        }
                    }
                    ColorPicker()
                }
            }
            ButtonItem(
                icon = Icons.Filled.Refresh,
                title = stringResource(R.string.apply),
                onClick = { M3KApp.restart() }
            )
        }
    }
}