package com.remtrik.m3khelper

import android.app.Application
import com.topjohnwu.superuser.Shell

lateinit var M3KApp: M3KHelperApplication
//lateinit var GMNT_SHELL: Shell
//lateinit var SHELL: Shell

class M3KHelperApplication : Application() {
    init {
        System.loadLibrary("variables")
    }

    override fun onCreate() {
        super.onCreate()
        M3KApp = this
        Shell.setDefaultBuilder(
            Shell.Builder.create().setFlags(Shell.FLAG_REDIRECT_STDERR).setTimeout(10)
        )
        //GMNT_SHELL = Shell.Builder.create().build("su")
        //SHELL = Shell.Builder.create().build("su", "-mm")
    }
}