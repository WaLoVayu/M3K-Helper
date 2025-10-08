package com.remtrik.m3khelper.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.remtrik.m3khelper.M3KApp
import com.remtrik.m3khelper.prefs

private val DarkColorScheme = darkColorScheme(
    primary = m3k_theme_dark_primary,
    onPrimary = m3k_theme_dark_onPrimary,
    primaryContainer = m3k_theme_dark_primaryContainer,
    secondary = m3k_theme_dark_secondary,
    onSecondary = m3k_theme_dark_onSecondary,
    secondaryContainer = m3k_theme_dark_secondaryContainer,
    onSecondaryContainer = m3k_theme_dark_onSecondaryContainer
)

private val LightColorScheme = lightColorScheme(
    primary = m3k_theme_light_primary,
    onPrimary = m3k_theme_light_onPrimary,
    primaryContainer = m3k_theme_light_primaryContainer,
    secondary = m3k_theme_light_secondary,
    onSecondary = m3k_theme_light_onSecondary,
    secondaryContainer = m3k_theme_light_secondaryContainer,
    onSecondaryContainer = m3k_theme_light_onSecondaryContainer
)

val themeReapply: MutableState<Boolean> = mutableStateOf(false)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun M3KHelperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val themeSettings = remember(themeReapply.value) {
        ThemeSettings(
            red = prefs.getFloat("theme_engine_color_R", 0f),
            green = prefs.getFloat("theme_engine_color_G", 0f),
            blue = prefs.getFloat("theme_engine_color_B", 0f),
            enableThemeEngine = prefs.getBoolean("theme_engine_enable", false),
            enableMaterialU = prefs.getBoolean("theme_engine_enable_materialu", true),
            style = runCatching {
                PaletteStyle.valueOf(
                    prefs.getString("theme_engine_palette_style", "TonalSpot").toString()
                )
            }.getOrDefault(PaletteStyle.TonalSpot)
        )
    }
    val colorScheme = remember(darkTheme, themeSettings) {
        when {
            themeSettings.enableMaterialU
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (darkTheme) dynamicDarkColorScheme(M3KApp) else dynamicLightColorScheme(M3KApp)
            }

            themeSettings.enableThemeEngine ->
                dynamicColorScheme(
                    keyColor = Color(
                        red = themeSettings.red,
                        green = themeSettings.green,
                        blue = themeSettings.blue
                    ),
                    isDark = darkTheme,
                    style = themeSettings.style
                )

            else -> if (darkTheme) DarkColorScheme else LightColorScheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        motionScheme = MotionScheme.expressive()
    )
}

private data class ThemeSettings(
    val red: Float,
    val green: Float,
    val blue: Float,
    val enableThemeEngine: Boolean,
    val enableMaterialU: Boolean,
    val style: PaletteStyle
)