package com.remtrik.m3khelper.util

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.Alignment

val expandTransition: EnterTransition =
    fadeIn() + expandVertically(expandFrom = Alignment.Top)
val collapseTransition: ExitTransition =
    shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
val fadeEnterTransition: EnterTransition =
    fadeIn(tween(340))
val fadeExitTransition: ExitTransition =
    fadeOut(tween(340))
val slideEnterTransition: EnterTransition =
    slideInHorizontally(initialOffsetX = { it })
val slideExitTransition: ExitTransition =
    slideOutHorizontally(targetOffsetX = { -it / 4 }) + fadeOut()