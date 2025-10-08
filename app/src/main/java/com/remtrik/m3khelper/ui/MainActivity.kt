package com.remtrik.m3khelper.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_FULL_USER
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.NavHostAnimatedDestinationStyle
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.LinksScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.utils.isRouteOnBackStackAsState
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator
import com.remtrik.m3khelper.BuildConfig
import com.remtrik.m3khelper.prefs
import com.remtrik.m3khelper.ui.component.NoRoot
import com.remtrik.m3khelper.ui.component.UnknownDevice
import com.remtrik.m3khelper.ui.component.UpdateDialog
import com.remtrik.m3khelper.ui.theme.M3KHelperTheme
import com.remtrik.m3khelper.util.collapseTransition
import com.remtrik.m3khelper.util.expandTransition
import com.remtrik.m3khelper.util.fadeEnterTransition
import com.remtrik.m3khelper.util.fadeExitTransition
import com.remtrik.m3khelper.util.funcs.LatestVersionInfo
import com.remtrik.m3khelper.util.funcs.checkNewVersion
import com.remtrik.m3khelper.util.slideFromRightEnterTransition
import com.remtrik.m3khelper.util.slideToLeftExitTransition
import com.remtrik.m3khelper.util.slideToRightExitTransition
import com.remtrik.m3khelper.util.variables.Device
import com.remtrik.m3khelper.util.variables.FontSize
import com.remtrik.m3khelper.util.variables.LineHeight
import com.remtrik.m3khelper.util.variables.PaddingValue
import com.remtrik.m3khelper.util.variables.Warning
import com.remtrik.m3khelper.util.variables.sdp
import com.remtrik.m3khelper.util.variables.ssp
import com.remtrik.m3khelper.util.variables.vars
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false


        setContent {
            requestedOrientation =
                if (shouldForceRotation()) SCREEN_ORIENTATION_FULL_USER
                else SCREEN_ORIENTATION_USER_PORTRAIT

            vars()

            M3KHelperTheme {

                if (Shell.isAppGrantedRoot() == true) {
                    InitDimens()
                    M3KRootContent()
                } else NoRoot()
            }
        }
    }

    private fun shouldForceRotation() = Build.DEVICE == "nabu" ||
            (BuildConfig.DEBUG && Build.DEVICE == "emu64xa") ||
            prefs.getBoolean("force_rotation", false)
}

@Composable
internal fun InitDimens() {
    LineHeight = 20.ssp()
    FontSize = 15.ssp()
    PaddingValue = 10.sdp()
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun M3KRootContent() {
    val navController = rememberNavController()
    val navigator = navController.rememberDestinationsNavigator()
    val bottomBarRoutes = remember {
        Destinations.entries.map { it.route.route }.toSet()
    }

    val latestVersionInfo = LatestVersionInfo()
    val newVersion by produceState(initialValue = latestVersionInfo) {
        if (mutableStateOf(prefs.getBoolean("check_update", true)).value) {
            value = withContext(Dispatchers.IO) {
                checkNewVersion()
            }
        }
    }

    val currentVersionCode = BuildConfig.VERSION_CODE
    val newVersionCode = newVersion.versionCode

    Scaffold(
        bottomBar = {
            if (LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE) {
                BottomNavigationBar(navController, navigator) }
        },
    ) { innerPadding ->
        Row {
            if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                LeftNavigationBar(navController, navigator, innerPadding)
            }

            Box(
                modifier = Modifier
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .fillMaxSize(),
            ) {
                DestinationsNavHost(
                    navGraph = NavGraphs.root,
                    navController = navController,
                    defaultTransitions = object : NavHostAnimatedDestinationStyle() {
                        override val enterTransition:
                                AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
                            {
                                if (targetState.destination.route !in bottomBarRoutes) {
                                    slideFromRightEnterTransition
                                } else {
                                    fadeEnterTransition
                                }
                            }
                        override val exitTransition:
                                AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
                            {
                                if (targetState.destination.route !in bottomBarRoutes) {
                                    if (targetState.destination.route in listOf(
                                            "settings_screen",
                                            "theme_engine_screen"
                                        )
                                    ) slideToLeftExitTransition
                                    else slideToRightExitTransition
                                } else {
                                    fadeExitTransition
                                }
                            }
                    }
                )
                when {
                    Warning.value -> {
                        UnknownDevice()
                    }
                }
            }
            AnimatedVisibility(
                visible = newVersionCode > currentVersionCode,
                enter = expandTransition,
                exit = collapseTransition
            ) {
                UpdateDialog(newVersion)
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    navController: NavHostController,
    navigator: DestinationsNavigator
) {
    NavigationBar(
        tonalElevation = 12.dp,
        windowInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout)
            .only(
                WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
            ),
        modifier = Modifier.height(120.sdp()),
    ) {
        Destinations.entries
            .filterNot { it.landscapeOnly }
            .forEach { destination ->
                if (Device.currentDeviceCard.noLinks && destination.route == LinksScreenDestination) {
                    return@forEach
                }
                val isCurrentDestOnBackStack by navController.isRouteOnBackStackAsState(
                    destination.route
                )
                NavigationBarItem(
                    selected = isCurrentDestOnBackStack,
                    onClick = {
                        if (isCurrentDestOnBackStack) {
                            navigator.popBackStack(destination.route, false)
                        }
                        navigator.navigate(destination.route) {
                            popUpTo(NavGraphs.root) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        if (isCurrentDestOnBackStack) {
                            Icon(
                                imageVector = destination.iconSelected,
                                contentDescription = stringResource(
                                    destination.label
                                ),
                                modifier = Modifier.size(20.sdp())
                            )
                        } else {
                            Icon(
                                imageVector = destination.iconNotSelected,
                                contentDescription = stringResource(
                                    destination.label
                                ),
                                modifier = Modifier.size(20.sdp())
                            )
                        }
                    },
                    label = {
                        Text(
                            text = stringResource(destination.label),
                            fontSize = 10.ssp(),
                        )
                    },
                    alwaysShowLabel = false
                )
            }
    }
}

@Composable
private fun LeftNavigationBar(
    navController: NavHostController,
    navigator: DestinationsNavigator,
    innerPadding: PaddingValues
) {
    NavigationRail(
        modifier = Modifier.width(110.sdp()),
        windowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Bottom + WindowInsetsSides.Top)
    ) {
        println(innerPadding.calculateTopPadding())
        Destinations.entries.forEach { destination ->
            if (Device.currentDeviceCard.noLinks && destination.route == LinksScreenDestination) return@forEach
            if (destination.route == SettingsScreenDestination) Spacer(
                Modifier.weight(
                    1f
                )
            )
            val isCurrentDestOnBackStack by navController.isRouteOnBackStackAsState(
                destination.route
            )
            NavigationRailItem(
                selected = isCurrentDestOnBackStack,
                onClick = {
                    if (isCurrentDestOnBackStack) {
                        navigator.popBackStack(destination.route, false)
                    }
                    navigator.navigate(destination.route) {
                        popUpTo(NavGraphs.root) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    if (isCurrentDestOnBackStack) {
                        Icon(
                            imageVector = destination.iconSelected,
                            contentDescription = stringResource(
                                destination.label
                            ),
                            modifier = Modifier.size(20.sdp())
                        )
                    } else {
                        Icon(
                            imageVector = destination.iconNotSelected,
                            contentDescription = stringResource(
                                destination.label
                            ),
                            modifier = Modifier.size(20.sdp())
                        )
                    }
                },
                label = {
                    Text(
                        text = stringResource(destination.label),
                        fontSize = 10.ssp(),
                    )
                },
                alwaysShowLabel = false
            )
        }
    }
}