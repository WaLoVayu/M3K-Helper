package com.remtrik.m3khelper.qstiles

import android.service.quicksettings.Tile.STATE_ACTIVE
import android.service.quicksettings.Tile.STATE_UNAVAILABLE
import android.service.quicksettings.TileService
import com.remtrik.m3khelper.R
import com.remtrik.m3khelper.util.Device
import com.remtrik.m3khelper.util.FirstBoot
import com.remtrik.m3khelper.util.isMounted
import com.remtrik.m3khelper.util.mountWindows
import com.remtrik.m3khelper.util.quickBoot
import com.remtrik.m3khelper.util.string
import com.remtrik.m3khelper.util.umountWindows

// might use in future
internal fun getUefiPath(): String? {
    return when {
        Device.uefiCardsArray.any { it.uefiType == 120 } -> Device.uefiCardsArray[3].uefiPath
        Device.uefiCardsArray.any { it.uefiType == 90 } -> Device.uefiCardsArray[2].uefiPath
        Device.uefiCardsArray.any { it.uefiType == 60 } -> Device.uefiCardsArray[1].uefiPath
        Device.uefiCardsArray.any { it.uefiType == 1 } -> Device.uefiCardsArray[0].uefiPath
        else -> null
    }
}

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
        get() = !FirstBoot && !Device.savedDeviceCard.noMount

    override fun onStartListening() {
        super.onStartListening()
        if (!supported) {
            disableTile(R.string.qs_unsupported)
            return
        }

        if (isMounted()) {
            enableTile(R.string.mnt_question)
        } else {
            enableTile(R.string.umnt_question)
        }
    }

    override fun onClick() {
        super.onClick()

        if (isMounted()) mountWindows() else umountWindows()

        onStartListening()
    }

}

class QuickBootTile : CommonTileService() { // more than just a PoC
    private val supported: Boolean
        get() = !FirstBoot && !Device.savedDeviceCard.noFlash

    private val uefiPath: String?
        get() = Device.uefiCardsArray.firstOrNull()?.uefiPath

    override fun onStartListening() {
        super.onStartListening()
        when {
            !supported -> disableTile(R.string.qs_unsupported)
            uefiPath == null -> disableTile(R.string.uefi_not_found_title)
            else -> enableTile()
        }
    }

    override fun onClick() {
        super.onClick()
        when {
            uefiPath == null -> {
                disableTile(R.string.uefi_not_found_title)
                return
            }

            else -> quickBoot(uefiPath!!)
        }
    }

}