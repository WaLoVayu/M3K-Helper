package com.remtrik.m3khelper.qstiles

import android.service.quicksettings.Tile
import android.service.quicksettings.Tile.STATE_ACTIVE
import android.service.quicksettings.Tile.STATE_UNAVAILABLE
import android.service.quicksettings.TileService
import com.remtrik.m3khelper.M3KApp
import com.remtrik.m3khelper.R
import com.remtrik.m3khelper.util.Device
import com.remtrik.m3khelper.util.FirstBoot
import com.remtrik.m3khelper.util.mountStatus
import com.remtrik.m3khelper.util.mountWindows
import com.remtrik.m3khelper.util.quickBoot
import com.remtrik.m3khelper.util.umountWindows

internal fun disableTile(qsTile: Tile, reason: Int) {
    qsTile.state = STATE_UNAVAILABLE
    qsTile.subtitle = M3KApp.getString(
        reason
    )
    qsTile.updateTile()
}

class MountTile : TileService() { // PoC

    override fun onStartListening() {
        super.onStartListening()
        if (FirstBoot || Device.savedDeviceCard.noMount) {
            disableTile(qsTile, R.string.qs_unsupported)
        } else {
            if (mountStatus()) {
                qsTile.state = STATE_ACTIVE
                qsTile.label = M3KApp.getString(
                    R.string.mnt_question
                )
            } else {
                qsTile.state = STATE_ACTIVE
                qsTile.label = M3KApp.getString(
                    R.string.umnt_question
                )
            }
        }
    }

    override fun onClick() {
        super.onClick()
        if (mountStatus()) {
            mountWindows()
        } else {
            umountWindows()
        }
    }

}

class QuickBootTile : TileService() { // PoC

    override fun onStartListening() {
        super.onStartListening()
        if (FirstBoot || Device.savedDeviceCard.noFlash) {
            disableTile(qsTile, R.string.qs_unsupported)
        } else {
            if (Device.uefiCardsArray.isEmpty()) {
                disableTile(qsTile, R.string.uefi_not_found_title)
            } else {
                qsTile.state = STATE_ACTIVE; qsTile.subtitle = null
            }
        }
    }

    override fun onClick() {
        super.onClick()
        if (Device.uefiCardsArray.isNotEmpty()) {
            if (Device.uefiCardsArray.find { it.uefiType == 120 } != null) {
                quickBoot(Device.uefiCardsArray[3].uefiPath)
            } else if (Device.uefiCardsArray.find { it.uefiType == 90 } != null) {
                quickBoot(Device.uefiCardsArray[2].uefiPath)
            } else if (Device.uefiCardsArray.find { it.uefiType == 60 } != null) {
                quickBoot(Device.uefiCardsArray[1].uefiPath)
            } else if (Device.uefiCardsArray.find { it.uefiType == 1 } != null) {
                quickBoot(Device.uefiCardsArray[0].uefiPath)
            }
        } else {
            disableTile(qsTile, R.string.uefi_not_found_title)
        }
    }

}
