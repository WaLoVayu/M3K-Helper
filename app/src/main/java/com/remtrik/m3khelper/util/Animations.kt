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

private object AnimationConfig {
    const val FADE_DURATION = 340
    const val SLIDE_DIVIDER = 4
}

val expandTransition: EnterTransition
    get() = fadeIn() + expandVertically(expandFrom = Alignment.Top)

val collapseTransition: ExitTransition
    get() = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()

val fadeEnterTransition: EnterTransition
    get() = fadeIn(tween(AnimationConfig.FADE_DURATION))

val fadeExitTransition: ExitTransition
    get() = fadeOut(tween(AnimationConfig.FADE_DURATION))

val slideFromRightEnterTransition: EnterTransition
    get() = slideInHorizontally(initialOffsetX = { it })

val slideFromLeftEnterTransition: EnterTransition
    get() = slideInHorizontally(initialOffsetX = { -it / AnimationConfig.SLIDE_DIVIDER })

val slideToRightExitTransition: ExitTransition
    get() = slideOutHorizontally(targetOffsetX = { -it / AnimationConfig.SLIDE_DIVIDER })

val slideToLeftExitTransition: ExitTransition
    get() = slideOutHorizontally(targetOffsetX = { it })