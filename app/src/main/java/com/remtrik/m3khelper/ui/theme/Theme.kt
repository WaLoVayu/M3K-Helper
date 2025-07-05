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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.google.android.material.color.DynamicColors
import com.remtrik.m3khelper.M3KApp
import com.remtrik.m3khelper.util.prefs

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    onPrimaryContainer = md_theme_dark_primaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer
)

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    onPrimaryContainer = md_theme_light_primaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun M3KHelperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = DynamicColors.isDynamicColorAvailable(),
    content: @Composable () -> Unit
) {
    val red = rememberSaveable {
        mutableFloatStateOf(
            prefs.getFloat("themeengine_red", 0f)
        )
    }
    val green = rememberSaveable {
        mutableFloatStateOf(
            prefs.getFloat("themeengine_green", 0f)
        )
    }
    val blue = rememberSaveable {
        mutableFloatStateOf(
            prefs.getFloat("themeengine_blue", 0f)
        )
    }

    val color by remember {
        derivedStateOf {
            Color(red.floatValue, green.floatValue, blue.floatValue, 1f)
        }
    }
    var enableThemeEngine by rememberSaveable {
        mutableStateOf(
            prefs.getBoolean("enable_themeengine", false)
        )
    }
    var enableMaterialU by rememberSaveable {
        mutableStateOf(
            prefs.getBoolean("enable_materialu", true)
        )
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !enableThemeEngine && enableMaterialU -> {
            if (darkTheme) dynamicDarkColorScheme(M3KApp) else dynamicLightColorScheme(M3KApp)
        }

        darkTheme && !enableThemeEngine && !enableMaterialU -> DarkColorScheme
        !darkTheme && !enableThemeEngine && !enableMaterialU -> LightColorScheme

        enableThemeEngine && !enableMaterialU -> dynamicColorScheme(
            keyColor = color, isDark = isSystemInDarkTheme()
        )

        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        motionScheme = MotionScheme.expressive()
    )
}