package com.remtrik.m3khelper.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.core.content.edit
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.remtrik.m3khelper.M3KApp
import com.remtrik.m3khelper.R
import com.remtrik.m3khelper.prefs
import com.remtrik.m3khelper.ui.component.ColorPicker
import com.remtrik.m3khelper.ui.component.SwitchItem
import com.remtrik.m3khelper.ui.component.CommonTopAppBar
import com.remtrik.m3khelper.ui.theme.PaletteStyle
import com.remtrik.m3khelper.ui.theme.themeReapply
import com.remtrik.m3khelper.util.collapseTransition
import com.remtrik.m3khelper.util.expandTransition
import com.remtrik.m3khelper.util.variables.FontSize
import com.remtrik.m3khelper.util.variables.LineHeight
import com.remtrik.m3khelper.util.variables.PaddingValue
import com.remtrik.m3khelper.util.variables.sdp
import kotlinx.coroutines.Dispatchers

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun ThemeEngineScreen(navigator: DestinationsNavigator) {
    val themeState = rememberThemeState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CommonTopAppBar(
                navigator = navigator,
                text = R.string.theme_engine,
                isPopBack = true
            )
        }
    ) { innerPadding ->
        ThemeEngineContent(
            themeState = themeState,
            scrollState = scrollState,
            innerPadding = innerPadding
        )
    }
}

@Composable
private fun rememberThemeState(): ThemeState {
    var enableThemeEngine by rememberSaveable {
        mutableStateOf(prefs.getBoolean("theme_engine_enable", false))
    }
    var enableMaterialU by rememberSaveable {
        mutableStateOf(prefs.getBoolean("theme_engine_enable_materialu", true))
    }
    var paletteStyle by rememberSaveable {
        mutableStateOf(prefs.getString("theme_engine_palette_style", "TonalSpot") ?: "TonalSpot")
    }

    return remember(enableThemeEngine, enableMaterialU, paletteStyle) {
        ThemeState(
            enableThemeEngine = enableThemeEngine,
            enableMaterialU = enableMaterialU,
            paletteStyle = paletteStyle,
            onThemeEngineChanged = { newValue ->
                enableThemeEngine = newValue
                if (newValue) {
                    enableMaterialU = false
                }
                savePreference("theme_engine_enable", newValue)
                if (newValue) {
                    savePreference("theme_engine_enable_materialu", false)
                }
                triggerThemeReapply()
            },
            onMaterialUChanged = { newValue ->
                enableMaterialU = newValue
                if (newValue) {
                    enableThemeEngine = false
                }
                savePreference("theme_engine_enable_materialu", newValue)
                if (newValue) {
                    savePreference("theme_engine_enable", false)
                }
                triggerThemeReapply()
            },
            onPaletteStyleChanged = { newStyle ->
                paletteStyle = newStyle
                savePreference("theme_engine_palette_style", newStyle)
                triggerThemeReapply()
            }
        )
    }
}

@Composable
private fun ThemeEngineContent(
    themeState: ThemeState,
    scrollState: androidx.compose.foundation.ScrollState,
    innerPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(top = innerPadding.calculateTopPadding())
            .padding(horizontal = PaddingValue)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        ThemeSwitchItems(themeState)
        PaletteSelector(themeState)
    }
}

@Composable
private fun ThemeSwitchItems(themeState: ThemeState) {
    SwitchItem(
        icon = Icons.Filled.Brush,
        title = stringResource(R.string.enable_materialu),
        summary = stringResource(R.string.enable_materialu_summary),
        checked = themeState.enableMaterialU,
        onCheckedChange = themeState.onMaterialUChanged
    )

    SwitchItem(
        icon = Icons.Filled.FormatPaint,
        title = stringResource(R.string.theme_engine_enable),
        summary = stringResource(R.string.theme_engine_enable_summary),
        checked = themeState.enableThemeEngine,
        onCheckedChange = themeState.onThemeEngineChanged
    )
}

@Composable
private fun PaletteSelector(themeState: ThemeState) {
    AnimatedVisibility(
        visible = themeState.enableThemeEngine,
        enter = expandTransition,
        exit = collapseTransition,
        modifier = Modifier
            .padding(PaddingValue)
            .fillMaxWidth()
    ) {
        PaletteCard(themeState)
    }
}

@Composable
private fun PaletteCard(themeState: ThemeState) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.padding(PaddingValue)
    ) {
        PaletteDropdown(
            expanded = expanded,
            paletteStyle = themeState.paletteStyle,
            onExpandedChange = { expanded = it },
            onPaletteStyleSelected = themeState.onPaletteStyleChanged
        )
        ColorPicker()
    }
}

@Composable
private fun PaletteDropdown(
    expanded: Boolean,
    paletteStyle: String,
    onExpandedChange: (Boolean) -> Unit,
    onPaletteStyleSelected: (String) -> Unit
) {
    IconButton(
        onClick = { onExpandedChange(!expanded) },
        modifier = Modifier
            .fillMaxWidth()
            .size(25.sdp())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.sdp())
        ) {
            Icon(
                Icons.Filled.Palette,
                contentDescription = "More options",
                modifier = Modifier
                    .size(25.sdp())
                    .align(Alignment.CenterVertically),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = M3KApp.getString(
                    R.string.theme_engine_current_palette,
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
        onDismissRequest = { onExpandedChange(false) },
        modifier = Modifier.padding(PaddingValue)
    ) {
        PaletteStyle.entries
            .filterNot { it.name == paletteStyle }
            .forEach { palette ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = palette.name,
                            fontSize = FontSize,
                            lineHeight = LineHeight
                        )
                    },
                    onClick = {
                        onPaletteStyleSelected(palette.name)
                        onExpandedChange(false)
                    }
                )
            }
    }
}

private data class ThemeState(
    val enableThemeEngine: Boolean,
    val enableMaterialU: Boolean,
    val paletteStyle: String,
    val onThemeEngineChanged: (Boolean) -> Unit,
    val onMaterialUChanged: (Boolean) -> Unit,
    val onPaletteStyleChanged: (String) -> Unit
)

private fun savePreference(key: String, value: Any) {
    kotlinx.coroutines.runBlocking(Dispatchers.IO) {
        prefs.edit {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
            }
        }
    }
}

private fun triggerThemeReapply() {
    themeReapply.value = !themeReapply.value
}