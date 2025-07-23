package com.remtrik.m3khelper.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Environment
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.remtrik.m3khelper.BuildConfig
import com.remtrik.m3khelper.M3KApp
import com.remtrik.m3khelper.R
import com.topjohnwu.superuser.ShellUtils

private external fun findUEFIImages(baseCmd: String): IntArray
private external fun checkBootImages(noMount: Boolean, path: String): Int
private external fun getPanelNative(): String

data class UEFICard(
    var uefiPath: String,
    val uefiType: Int,
)

// static vars
val deviceCardsArray: Array<DeviceCard> =
    arrayOf(
        unknownCard,
        vayuCard,
        suryaCard,
        nabuCard,
        raphaelCard, raphaelinCard, raphaelsCard,
        cepheusCard,
        berylliumCard,
        curtanaCard, excaliburCard, gramCard, miatollCard,
        alphaCard,
        mh2Card,
        betaCard,
        mh2lm5gCard,
        flashCard,
        guacamoleCard,
        hotdogCard,
        a52sxqCard,
        beyond1Card,
        emu64xaCard
    )

val specialDeviceCardsArray: Array<DeviceCard> =
    arrayOf(
        nabuCard,
        emu64xaCard
    )

var UEFICardsArray: Array<UEFICard> =
    arrayOf(
        UEFICard("", 1),
        UEFICard("", 60),
        UEFICard("", 90),
        UEFICard("", 120)
    )

data class DeviceCommands(
    var mountPath: String = ""
)

@SuppressLint("UnknownNullness")
val prefs: SharedPreferences = M3KApp.getSharedPreferences("settings", Context.MODE_PRIVATE)

// device state
var PanelType: MutableState<String> = mutableStateOf("Unknown")

var CurrentDeviceCard: DeviceCard = unknownCard
val Ram: String = getMemory(M3KApp)
val Slot: String = ShellUtils.fastCmd("getprop ro.boot.slot_suffix").drop(1).uppercase()

// ui state
var BootIsPresent: MutableState<Int> = mutableIntStateOf(R.string.no)
var WindowsIsPresent: Int = 0
val Warning: MutableState<Boolean> = mutableStateOf(true)
var showAboutCard: MutableState<Boolean> = mutableStateOf(false)
var UEFIList: Array<Int> = emptyArray()

// ui defaults
var FontSize: TextUnit = 0.sp
var PaddingValue: Dp = 0.dp
var LineHeight: TextUnit = 0.sp

// app state
var FirstBoot: Boolean = prefs.getBoolean("firstboot", true)

private val deviceCodenames by lazy {
    arrayOf(
        Build.DEVICE,
        ShellUtils.fastCmd("getprop ro.product.device"),
        ShellUtils.fastCmd("getprop ro.lineage.device")
    )
}

var CurrentDeviceCommands: DeviceCommands = DeviceCommands()

val sdcardpath: String = Environment.getExternalStorageDirectory().path

var permissiveAble: Boolean = false

@SuppressLint("RestrictedApi")
fun vars() {
    if (prefs.getString("version", "3.4") != BuildConfig.VERSION_NAME) {
        prefs.edit { putBoolean("firstboot", true) }
    }
    prefs.edit { putString("version", BuildConfig.VERSION_NAME) }
    if (prefs.run { getBoolean("firstboot", true) || getBoolean("unknown", true) }) {
        var cardNum = 0
        for (card: DeviceCard in deviceCardsArray) {
            for (num: String in card.deviceCodename) {
                if (deviceCodenames.contains(num)) {
                    CurrentDeviceCard = card; CurrentDeviceCard.deviceCodename[0] =
                        deviceCodenames[0]
                    prefs.edit {
                        putInt(
                            "deviceCard",
                            cardNum
                        )
                    }; prefs.edit {
                        putBoolean(
                            "firstboot",
                            false
                        )
                    }; prefs.edit { putBoolean("unknown", false) }
                    break

                }
            }
            if (CurrentDeviceCard != unknownCard) {
                Warning.value = false; break
            }
            cardNum += 1
        }
        Thread { getPanel() }.start()
    } else {
        fastLoadSavedDevice()
    }

    // TODO: Examine the OS behavior with different paths
    CurrentDeviceCommands.mountPath = when {
        ShellUtils.fastCmd("find /mnt/pass_through -maxdepth 0").isNotEmpty() -> "/mnt/pass_through/0/emulated/0" // passthrough+getExternalStorageDirectory maybe?
        else -> Environment.getExternalStorageDirectory().path
    }

    if (mountStatus()) {
        mountWindows()
        dynamicVars()
        bootBackupStatus()
        umountWindows()
    } else {
        dynamicVars()
        bootBackupStatus()
    }


}

// TODO: Adapt to .find
fun fastLoadSavedDevice() {
    if (prefs.getBoolean("override_device", false)) {
        for (card: DeviceCard in deviceCardsArray) {
            for (num: String in card.deviceCodename) {
                if (prefs.getString("overriden_device_codename", "vayu")?.contains(num) == true) {
                    CurrentDeviceCard = card; break
                }
            }
        }
    } else {
        CurrentDeviceCard = deviceCardsArray[prefs.getInt("deviceCard", 0)]
    }
    Warning.value = false
    PanelType.value =
        prefs.getString("devicePanel", M3KApp.getString(R.string.unknown_panel)).toString()
}

private fun getPanel() {
    PanelType.value = getPanelNative()
    prefs.edit { putString("devicePanel", PanelType.value) }
}

fun bootBackupStatus() {
    BootIsPresent.value = when (checkBootImages(CurrentDeviceCard.noMount, sdcardpath)) {
        3 -> R.string.backup_both
        2 -> R.string.backup_windows
        1 -> R.string.backup_android
        else -> R.string.no
    }
}

private fun dynamicVars() {
    permissiveAble = ShellUtils.fastCmd("getenforce") == "Permissive"
    WindowsIsPresent = when {
        ShellUtils.fastCmd("find $sdcardpath/Windows/explorer.exe")
            .isNotEmpty() -> R.string.yes

        else -> R.string.no
    }
    // TODO: Move to c++ implementation 
    UEFIList = arrayOf()
    val find = ShellUtils.fastCmd("find /mnt/sdcard/UEFI/ -type f | grep .img")
    if (find.isNotEmpty()) {
        var index = 1
        for (uefi: String in arrayOf("60", "90", "120")) {
            val path =
                ShellUtils.fastCmd("find /mnt/sdcard/UEFI/ -type f  | grep .img | grep ${uefi}hz")
            if (path.isNotEmpty()
            ) {
                UEFIList += uefi.toInt()
                UEFICardsArray[index].uefiPath = path
            }
            index += 1
        }
        if (UEFIList.isEmpty()) {
            UEFIList += 1
            UEFICardsArray[0].uefiPath = find
        }
    }
}
