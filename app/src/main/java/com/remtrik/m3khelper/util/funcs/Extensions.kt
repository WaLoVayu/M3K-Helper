package com.remtrik.m3khelper.util.funcs

import com.remtrik.m3khelper.M3KApp

fun Int.string(vararg args: Any): String {
    return M3KApp.getString(this, *args)
}