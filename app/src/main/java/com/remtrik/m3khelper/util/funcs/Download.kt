package com.remtrik.m3khelper.util.funcs

import android.util.Log
import com.remtrik.m3khelper.M3KApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.json.JSONObject

// sorry I took that from kernel su
data class LatestVersionInfo(
    val versionCode: Int = 0,
    val versionName: String = "",
    val downloadUrl: String = "",
    val changelog: String = ""
)

object Download {
    private const val TAG = "M3K: Download"
    private const val RELEASE_API_URL =
        "https://api.github.com/repos/WaLoVayu/M3K-Helper/releases/latest"


    suspend fun checkNewVersion(): LatestVersionInfo = withContext(Dispatchers.IO) {
        // default null value if failed
        val defaultValue = LatestVersionInfo()
        try {
            val request = Request.Builder()
                .url(RELEASE_API_URL)
                .header("Accept", "application/vnd.github+json")
                .build()

            M3KApp.okhttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.w(TAG, "GitHub API failed: ${response.code}")
                    return@withContext defaultValue
                }

                val body = response.body.string()
                if (body.isEmpty()) {
                    Log.w(TAG, "Empty response body")
                    return@withContext defaultValue
                }

                val json = JSONObject(body)
                val changelog = json.optString("body", "")
                val assets = json.optJSONArray("assets") ?: return@withContext defaultValue

                for (i in 0 until assets.length()) {
                    val asset = assets.optJSONObject(i) ?: continue
                    val name = asset.optString("name", "")
                    if (!name.endsWith(".apk")) continue

                    val regex = Regex("v(.+?)_(\\d+)-")
                    val match = regex.find(name) ?: continue

                    val versionName = match.groupValues.getOrNull(1) ?: continue
                    val versionCode = match.groupValues.getOrNull(2)?.toIntOrNull() ?: continue
                    val downloadUrl = asset.optString("browser_download_url", "")

                    if (downloadUrl.isNotEmpty()) {
                        return@withContext LatestVersionInfo(
                            versionCode = versionCode,
                            versionName = versionName,
                            downloadUrl = downloadUrl,
                            changelog = changelog
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "checkNewVersion failed", e)
        }
        return@withContext defaultValue
    }
}