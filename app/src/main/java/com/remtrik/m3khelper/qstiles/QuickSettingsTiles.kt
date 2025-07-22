package com.remtrik.m3khelper.qstiles

import android.service.quicksettings.Tile.STATE_ACTIVE
import android.service.quicksettings.Tile.STATE_UNAVAILABLE
import android.service.quicksettings.TileService
import com.remtrik.m3khelper.M3KApp
import com.remtrik.m3khelper.R
import com.remtrik.m3khelper.util.mountStatus
import com.remtrik.m3khelper.util.mountWindows
import com.remtrik.m3khelper.util.quickBoot
import com.remtrik.m3khelper.util.umountWindows
import com.remtrik.m3khelper.util.UEFICardsArray
import com.remtrik.m3khelper.util.UEFIList
import com.remtrik.m3khelper.util.deviceCardsArray
import com.remtrik.m3khelper.util.prefs

class MountTile : TileService() { // PoC

    override fun onStartListening() {
        super.onStartListening()
        if (prefs.getBoolean("firstboot", true) || deviceCardsArray[prefs.getInt("deviceCard", 0)].noMount) {
            qsTile.state = STATE_UNAVAILABLE
            qsTile.subtitle = M3KApp.getString(
                R.string.qs_unsupported
            )
            qsTile.updateTile()
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
        if (prefs.getBoolean("firstboot", true) || deviceCardsArray[prefs.getInt("deviceCard", 0)].noFlash) {
            qsTile.state = STATE_UNAVAILABLE
            qsTile.subtitle = M3KApp.getString(
                R.string.qs_unsupported
            )
            qsTile.updateTile()
        } else {
            if (UEFIList.isEmpty()) {
                qsTile.state = STATE_UNAVAILABLE
                qsTile.subtitle = M3KApp.getString(
                    R.string.uefi_not_found_title
                )
                qsTile.updateTile()
            } else {
                qsTile.state = STATE_ACTIVE; qsTile.subtitle = null
            }
        }
    }

    override fun onClick() {
        super.onClick()
        if (UEFIList.isNotEmpty()) {
            if (UEFIList.contains(120)) {
                quickBoot(UEFICardsArray[3].uefiPath)
            } else if (UEFIList.contains(90)) {
                quickBoot(UEFICardsArray[2].uefiPath)
            } else if (UEFIList.contains(60)) {
                quickBoot(UEFICardsArray[1].uefiPath)
            } else if (UEFIList.contains(1)) {
                quickBoot(UEFICardsArray[0].uefiPath)
            }
        } else {
            qsTile.state = STATE_UNAVAILABLE
            qsTile.subtitle = M3KApp.getString(
                R.string.uefi_not_found_title
            )
            qsTile.updateTile()
        }
    }

}
