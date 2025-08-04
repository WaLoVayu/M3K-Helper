package com.remtrik.m3khelper

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.topjohnwu.superuser.Shell

lateinit var M3KApp: M3KHelperApplication
//lateinit var GMNT_SHELL: Shell
//lateinit var SHELL: Shell

lateinit var prefs: SharedPreferences

class M3KHelperApplication : Application() {
    init {
        System.loadLibrary("variables")
    }

    override fun onCreate() {
        super.onCreate()
        M3KApp = this
        prefs = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
        Shell.setDefaultBuilder(
            Shell.Builder.create().setFlags(Shell.FLAG_REDIRECT_STDERR).setTimeout(10)
        )
        //GMNT_SHELL = Shell.Builder.create().build("su")
        //SHELL = Shell.Builder.create().build("su", "-mm")
    }
}