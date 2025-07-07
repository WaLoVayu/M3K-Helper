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
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
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
import com.remtrik.m3khelper.ui.component.AboutCard
import com.remtrik.m3khelper.ui.component.NoRoot
import com.remtrik.m3khelper.ui.component.UnknownDevice
import com.remtrik.m3khelper.ui.theme.M3KHelperTheme
import com.remtrik.m3khelper.util.CurrentDeviceCard
import com.remtrik.m3khelper.util.FontSize
import com.remtrik.m3khelper.util.LineHeight
import com.remtrik.m3khelper.util.PaddingValue
import com.remtrik.m3khelper.util.Warning
import com.remtrik.m3khelper.util.showAboutCard
import com.remtrik.m3khelper.util.vars
import com.remtrik.m3khelper.util.sdp
import com.remtrik.m3khelper.util.ssp
import com.topjohnwu.superuser.Shell

class MainActivity : ComponentActivity() {

    @SuppressLint("SourceLockedOrientationActivity", "UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false

        setContent {
            requestedOrientation = if (Build.DEVICE == "nabu" || Build.DEVICE == "emu64xa") {
                SCREEN_ORIENTATION_FULL_USER
            } else SCREEN_ORIENTATION_USER_PORTRAIT

            M3KHelperTheme {
                vars()
                if (Shell.isAppGrantedRoot() == true) {
                    LineHeight = 20.ssp()
                    FontSize = 15.ssp()
                    PaddingValue = 10.sdp()
                    val navController = rememberNavController()
                    val navigator = navController.rememberDestinationsNavigator()
                    Scaffold(
                        bottomBar = {
                            NavigationBar(
                                tonalElevation = 12.dp,
                                windowInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout).only(
                                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                                )
                            ) {
                                Destinations.entries.forEach { destination ->
                                    if (CurrentDeviceCard.noLinks && destination.route == LinksScreenDestination) {
                                    } else {
                                        val isCurrentDestOnBackStack by navController.isRouteOnBackStackAsState(destination.route)
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
                                                    Icon(destination.iconSelected, stringResource(destination.label))
                                                } else {
                                                    Icon(destination.iconNotSelected, stringResource(destination.label))
                                                }
                                            },
                                            label = { Text(stringResource(destination.label)) },
                                            alwaysShowLabel = false
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        when {
                            showAboutCard.value -> {
                                AboutCard()
                            }
                        }
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            navController = navController,
                            defaultTransitions = object : NavHostAnimatedDestinationStyle() {
                                override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition
                                    get() = { fadeIn(animationSpec = tween(340)) }
                                override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition
                                    get() = { fadeOut(animationSpec = tween(340)) }
                            }
                        )
                        when {
                            Warning.value -> {
                                UnknownDevice()
                            }
                        }
                    }
                } else NoRoot()
            }
        }
    }
}