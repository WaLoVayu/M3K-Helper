package com.remtrik.m3khelper.util.variables

import android.app.ActivityManager
import android.content.Context
import com.remtrik.m3khelper.M3KApp
import java.io.File

private data class MemoryThreshold(val minMB: Long, val label: String)

private val memoryThresholds = listOf(
    MemoryThreshold(12_000L, "16GB"),
    MemoryThreshold(8_000L, "12GB"),
    MemoryThreshold(6_000L, "8GB"),
    MemoryThreshold(4_000L, "6GB"),
    MemoryThreshold(0L, "4GB")
)

fun getMemory(): String {
    val totalMemMB = getTotalMemoryInMB()
    return memoryThresholds.first { totalMemMB > it.minMB }.label
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
            ?: 4_000L
    }
}.getOrElse { 4_000L }