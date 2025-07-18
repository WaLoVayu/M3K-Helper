package com.remtrik.m3khelper.util

import android.app.ActivityManager
import android.content.Context

private const val GB_16 = 12_000L
private const val GB_12 = 8_000L
private const val GB_8 = 6_000L
private const val GB_6 = 4_000L

fun getMemory(context: Context): String {
    val totalMem = runCatching {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        ActivityManager.MemoryInfo().run {
            actManager.getMemoryInfo(this)
            totalMem / (1024 * 1024)
        }
    }.getOrElse {
        readTotalMemFromProc()
    }

    return when {
        totalMem > GB_16 -> "16GB"
        totalMem > GB_12 -> "12GB"
        totalMem > GB_8 -> "8GB"
        totalMem > GB_6 -> "6GB"
        else -> "4GB"
    }
}

private fun readTotalMemFromProc(): Long {
    return runCatching {
        java.io.File("/proc/meminfo").useLines { lines ->
            lines.firstOrNull { it.startsWith("MemTotal:") }
                ?.substringAfter("MemTotal:")
                ?.substringBefore("kB")
                ?.trim()
                ?.toLongOrNull()
                ?.div(1024)
                ?: 4_000L
        }
    }.getOrElse { 4_000L }
}