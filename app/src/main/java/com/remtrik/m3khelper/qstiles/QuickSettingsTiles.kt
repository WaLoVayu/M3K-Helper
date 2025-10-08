package com.remtrik.m3khelper.qstiles

import android.Manifest
import android.service.quicksettings.Tile.STATE_ACTIVE
import android.service.quicksettings.Tile.STATE_UNAVAILABLE
import android.service.quicksettings.TileService
import androidx.annotation.RequiresPermission
import com.remtrik.m3khelper.R
import com.remtrik.m3khelper.util.funcs.MountStatus
import com.remtrik.m3khelper.util.funcs.string
import com.remtrik.m3khelper.util.variables.commandHandler
import com.remtrik.m3khelper.util.variables.device
import com.remtrik.m3khelper.util.variables.firstBoot

abstract class CommonTileService : TileService() {
    protected fun disableTile(subtitleString: Int?) {
        qsTile.apply {
            state = STATE_UNAVAILABLE
            subtitleString?.let { subtitle = it.string() }
            updateTile()
        }
    }

    protected fun enableTile(labelString: Int? = null, subtitleString: Int? = null) {
        qsTile.apply {
            state = STATE_ACTIVE
            labelString?.let { label = it.string() }
            subtitleString?.let { subtitle = it.string() }
        }
    }
}

class MountTile : CommonTileService() { // more than just a PoC
    private val supported: Boolean
        get() = !firstBoot && !device.savedDeviceCard.noMount

    override fun onStartListening() {
        super.onStartListening()
        if (!supported) {
            disableTile(R.string.qs_unsupported)
            return
        }

        if (commandHandler.isMounted() == MountStatus.NOT_MOUNTED) {
            enableTile(R.string.mnt_question)
        } else {
            enableTile(R.string.umnt_question)
        }
    }

    override fun onClick() {
        super.onClick()

        if (commandHandler.isMounted() == MountStatus.NOT_MOUNTED) commandHandler.mountWindows() else commandHandler.umountWindows()
        onStartListening()
    }

}

class QuickBootTile : CommonTileService() { // more than just a PoC
    private val supported: Boolean
        get() = !firstBoot && !device.savedDeviceCard.noFlash

    private val uefiPath: String?
        get() = device.uefiCardsArray.firstOrNull()?.uefiPath

    override fun onStartListening() {
        super.onStartListening()
        when {
            !supported -> disableTile(R.string.qs_unsupported)
            uefiPath == null -> disableTile(R.string.uefi_not_found_title)
            else -> enableTile()
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onClick() {
        super.onClick()
        when {
            uefiPath == null -> {
                disableTile(R.string.uefi_not_found_title)
                return
            }

            else -> commandHandler.quickBoot(uefiPath!!)
        }
    }

}