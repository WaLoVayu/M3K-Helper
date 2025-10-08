package com.remtrik.m3khelper.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Interests
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.LinksScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import com.remtrik.m3khelper.R

enum class Destinations(
    val route: DirectionDestinationSpec,
    @StringRes val label: Int,
    val iconSelected: ImageVector,
    val iconNotSelected: ImageVector,
    val landscapeOnly: Boolean,
) {
    Home(
        HomeScreenDestination,
        R.string.home,
        Icons.Filled.Home,
        Icons.Outlined.Home,
        false
    ),
    Links(
        LinksScreenDestination,
        R.string.links,
        Icons.Filled.Interests,
        Icons.Outlined.Interests,
        false
    ),
    Settings(
        SettingsScreenDestination,
        R.string.settings,
        Icons.Filled.Settings,
        Icons.Outlined.Settings,
        true
    ),
}