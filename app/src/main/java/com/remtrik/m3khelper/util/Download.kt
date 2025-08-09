package com.remtrik.m3khelper.util

import com.remtrik.m3khelper.M3KApp
import okhttp3.Request
import org.json.JSONObject


// sorry i took that from kernel su
fun checkNewVersion(): LatestVersionInfo {
    val url = "https://api.github.com/repos/WaLoVayu/M3K-Helper/releases/latest"
    // default null value if failed
    val defaultValue = LatestVersionInfo()
    runCatching {
        M3KApp.okhttpClient.newCall(Request.Builder().url(url).build()).execute()
            .use { response ->
                if (!response.isSuccessful) {
                    return defaultValue
                }
                val body = response.body.string()
                val json = JSONObject(body)
                val changelog = json.optString("body")

                val assets = json.getJSONArray("assets")
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    val name = asset.getString("name")
                    if (!name.endsWith(".apk")) {
                        continue
                    }
                    val regex = Regex("v(.+?)_(\\d+)-")
                    val matchResult = regex.find(name) ?: continue
                    val versionName = matchResult.groupValues[1]
                    val versionCode = matchResult.groupValues[2].toInt()
                    val downloadUrl = asset.getString("browser_download_url")

                    return LatestVersionInfo(
                        versionCode,
                        versionName,
                        downloadUrl,
                        changelog
                    )
                }

            }
    }
    return defaultValue
}

data class LatestVersionInfo(
    val versionCode: Int = 0,
    val versionName: String = "",
    val downloadUrl: String = "",
    val changelog: String = ""
)