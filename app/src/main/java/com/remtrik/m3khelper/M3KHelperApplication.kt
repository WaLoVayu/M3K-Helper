package com.remtrik.m3khelper

import android.app.Application
import android.content.SharedPreferences
import com.topjohnwu.superuser.Shell
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.Locale

lateinit var M3KApp: M3KHelperApplication
//lateinit var GMNT_SHELL: Shell
//lateinit var SHELL: Shell

lateinit var prefs: SharedPreferences

class M3KHelperApplication : Application() {

    lateinit var okhttpClient: OkHttpClient

    init {
        System.loadLibrary("variables")
    }

    override fun onCreate() {
        super.onCreate()
        M3KApp = this
        prefs = this.getSharedPreferences("settings", MODE_PRIVATE)

        Shell.setDefaultBuilder(
            Shell.Builder.create().setFlags(Shell.FLAG_REDIRECT_STDERR).setTimeout(10)
        )
        //GMNT_SHELL = Shell.Builder.create().build("su")
        //SHELL = Shell.Builder.create().build("su", "-mm")

        okhttpClient =
            OkHttpClient.Builder().cache(Cache(File(cacheDir, "okhttp"), 10 * 1024 * 1024))
                .addInterceptor { block ->
                    block.proceed(
                        block.request().newBuilder()
                            .header("User-Agent", "M3KHelper/${BuildConfig.VERSION_CODE}")
                            .header("Accept-Language", Locale.getDefault().toLanguageTag()).build()
                    )
                }.build()
    }
}