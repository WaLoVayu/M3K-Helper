package com.remtrik.m3khelper.util.variables

import android.app.ActivityManager
import android.content.Context
import com.remtrik.m3khelper.M3KApp
import java.io.File

private data class MemoryRange(val minMB: Long, val maxMB: Long, val label: String)

private val memoryRanges = listOf(
    MemoryRange(14_000L, Long.MAX_VALUE, "16GB"),
    MemoryRange(10_000L, 13_999L, "12GB"),
    MemoryRange(7_000L, 9_999L, "8GB"),
    MemoryRange(5_000L, 6_999L, "6GB"),
    MemoryRange(0L, 4_999L, "4GB")
)

fun getMemory(): String {
    val totalMemMB = getTotalMemoryInMB()
    return memoryRanges.firstOrNull { totalMemMB in it.minMB..it.maxMB }?.label ?: "Unknown"
}

private fun getTotalMemoryInMB(): Long = runCatching {
    (M3KApp.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).run {
        ActivityManager.MemoryInfo().also { getMemoryInfo(it) }.totalMem / (1024 * 1024)
    }
}.getOrElse { readTotalMemFromProc() }

private fun readTotalMemFromProc(): Long = runCatching {
    File("/proc/meminfo").useLines { lines ->
        lines.firstOrNull { it.startsWith("MemTotal:") }
            ?.substringAfter("MemTotal:")
            ?.substringBefore("kB")
            ?.trim()
            ?.toLongOrNull()
            ?.div(1024)
            ?: 4000L
    }
}.getOrElse { 4000L }