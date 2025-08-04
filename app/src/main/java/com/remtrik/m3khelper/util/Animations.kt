package com.remtrik.m3khelper.util

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.ui.Alignment

val expandTransition: EnterTransition = fadeIn() + expandVertically(expandFrom = Alignment.Top)
val collapseTransition: ExitTransition = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()