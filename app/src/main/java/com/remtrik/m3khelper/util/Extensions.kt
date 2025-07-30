package com.remtrik.m3khelper.util

import androidx.compose.runtime.Composable
import com.remtrik.m3khelper.M3KApp

@Composable
fun Int.string(vararg args: Any): String {
    return M3KApp.getString(this, *args)
}