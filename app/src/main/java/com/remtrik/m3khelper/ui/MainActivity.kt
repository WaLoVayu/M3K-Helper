package com.remtrik.m3khelper.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_FULL_USER
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.NavHostAnimatedDestinationStyle
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.LinksScreenDestination
import com.ramcosta.composedestinations.utils.isRouteOnBackStackAsState
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator
import com.remtrik.m3khelper.BuildConfig
import com.remtrik.m3khelper.prefs
import com.remtrik.m3khelper.ui.component.NoRoot
import com.remtrik.m3khelper.ui.component.UnknownDevice
import com.remtrik.m3khelper.ui.component.UpdateDialog
import com.remtrik.m3khelper.ui.theme.M3KHelperTheme
import com.remtrik.m3khelper.util.Device
import com.remtrik.m3khelper.util.fadeEnterTransition
import com.remtrik.m3khelper.util.fadeExitTransition
import com.remtrik.m3khelper.util.FontSize
import com.remtrik.m3khelper.util.LatestVersionInfo
import com.remtrik.m3khelper.util.LineHeight
import com.remtrik.m3khelper.util.PaddingValue
import com.remtrik.m3khelper.util.Warning
import com.remtrik.m3khelper.util.checkNewVersion
import com.remtrik.m3khelper.util.collapseTransition
import com.remtrik.m3khelper.util.expandTransition
import com.remtrik.m3khelper.util.vars
import com.remtrik.m3khelper.util.sdp
import com.remtrik.m3khelper.util.slideEnterTransition
import com.remtrik.m3khelper.util.slideExitTransition
import com.remtrik.m3khelper.util.ssp
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.collections.toSet


class MainActivity : ComponentActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false


        setContent {
            requestedOrientation =
                if (shouldForceRotation()) {
                    SCREEN_ORIENTATION_FULL_USER
                } else SCREEN_ORIENTATION_USER_PORTRAIT

            M3KHelperTheme {
                vars()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun M3KRootContent() {
    val navController = rememberNavController()
    val navigator = navController.rememberDestinationsNavigator()
    val bottomBarRoutes = remember {
        Destinations.entries.map { it.route.route }.toSet()
    }

    val latestVersionInfo = LatestVersionInfo()
    val newVersion by produceState(initialValue = latestVersionInfo) {
        value = withContext(Dispatchers.IO) {
            checkNewVersion()
        }
    }

    val currentVersionCode = BuildConfig.VERSION_CODE
    val newVersionCode = newVersion.versionCode

    Scaffold(
        bottomBar = {
            NavigationBar(
                tonalElevation = 12.dp,
                windowInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout)
                    .only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                    )
            ) {
                Destinations.entries.forEach { destination ->
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
                        label = { Text(stringResource(destination.label)) },
                        alwaysShowLabel = false
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
            DestinationsNavHost(
                navGraph = NavGraphs.root,
                navController = navController,
                defaultTransitions = object : NavHostAnimatedDestinationStyle() {
                    override val enterTransition:
                            AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
                        {
                            if (targetState.destination.route !in bottomBarRoutes) {
                                slideEnterTransition
                            } else {
                                fadeEnterTransition
                            }
                        }
                    override val exitTransition:
                            AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
                        {
                            if (targetState.destination.route !in bottomBarRoutes) {
                                slideExitTransition
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