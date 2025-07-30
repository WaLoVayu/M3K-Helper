package com.remtrik.m3khelper.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Environment
import android.os.Parcelable
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
import com.remtrik.m3khelper.R.string
import com.topjohnwu.superuser.ShellUtils
import kotlinx.parcelize.Parcelize

private external fun findUEFIImages(baseCmd: String): IntArray
private external fun checkBootImages(noMount: Boolean, path: String): Int
private external fun getPanelNative(): String

@Parcelize
data class UEFICard(
    var uefiPath: String,
    val uefiType: Int,
) : Parcelable

data class DeviceData(
    var currentDeviceCard: DeviceCard = unknownCard,
    val deviceCodenames: Array<String> =
        arrayOf(
            Build.DEVICE,
            ShellUtils.fastCmd("getprop ro.product.device"),
            ShellUtils.fastCmd("getprop ro.lineage.device")
        ),
    val savedDeviceCard: DeviceCard = deviceCardsArray[prefs.getInt("saved_device_card", 0)],
    val ram: String = getMemory(M3KApp),
    val slot: String = ShellUtils.fastCmd("getprop ro.boot.slot_suffix").drop(1).uppercase(),
    var panelType: MutableState<String> = mutableStateOf(
        prefs.getString("saved_device_panel", M3KApp.getString(string.unknown_panel)).toString()
    ),
    var uefiCardsArray: Array<UEFICard> = arrayOf()
)

@Parcelize
data class DeviceCommands(
    var mountPath: String = ""
) : Parcelable

@SuppressLint("UnknownNullness")
val prefs: SharedPreferences = M3KApp.getSharedPreferences("settings", Context.MODE_PRIVATE)

// device state


var Device: DeviceData = DeviceData()



// ui state
var BootIsPresent: MutableState<Int> = mutableIntStateOf(string.no)
var WindowsIsPresent: MutableState<Int> = mutableIntStateOf(string.no)
val Warning: MutableState<Boolean> = mutableStateOf(true)
var showAboutCard: MutableState<Boolean> = mutableStateOf(false)

// ui defaults
var FontSize: TextUnit = 0.sp
var PaddingValue: Dp = 0.dp
var LineHeight: TextUnit = 0.sp

// app state
var FirstBoot: Boolean = prefs.getBoolean("firstboot", true)

val OverrideDevice: Boolean by lazy { prefs.getBoolean("override_device", false) }

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
                if (Device.deviceCodenames.contains(num)) {
                    Device.currentDeviceCard = card; Device.currentDeviceCard.deviceCodename[0] =
                        Device.deviceCodenames[0]
                    prefs.edit {
                        putInt(
                            "saved_device_card",
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
            if (Device.currentDeviceCard != unknownCard) {
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
        ShellUtils.fastCmd("find /mnt/pass_through -maxdepth 0")
            .isNotEmpty() -> "/mnt/pass_through/0/emulated/0" // passthrough+getExternalStorageDirectory maybe?
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


    if (BuildConfig.DEBUG) {
        println("Boot is present: ${M3KApp.getString(BootIsPresent.value)}")
        println("Windows is present: ${M3KApp.getString(WindowsIsPresent.value)}")
        println("Panel Type: ${Device.panelType.value}")
        Device.deviceCodenames.forEach { println("Device codename: $it") }
        println("Current device: ${Device.currentDeviceCard.deviceName}")
        println("Saved device: ${Device.savedDeviceCard.deviceName}")
        println(
            "Override device enabled: $OverrideDevice ${
                if (OverrideDevice) {
                    "\nOverride device codename: ${
                        prefs.getString(
                            "overriden_device_codename",
                            "vayu"
                        )
                    }"
                } else {
                }
            }"
        )
        println("Current mount path: ${CurrentDeviceCommands.mountPath}")
    }
}

fun fastLoadSavedDevice() {
    Device.currentDeviceCard = if (OverrideDevice) {
        deviceCardsArray.find {
            it.deviceCodename.contains(
                prefs.getString(
                    "overriden_device_codename",
                    "vayu"
                ).toString()
            )
        }!!
    } else {
        Device.savedDeviceCard
    }
    Warning.value = false
}

private fun getPanel() {
    Device.panelType.value = getPanelNative()
    if (Device.panelType.value == "Invalid") Device.panelType.value = M3KApp.getString(string.unknown_panel)
    prefs.edit { putString("saved_device_panel", Device.panelType.value) }
}

fun bootBackupStatus() {
    BootIsPresent.value = when (checkBootImages(Device.currentDeviceCard.noMount, sdcardpath)) {
        3 -> string.backup_both
        2 -> string.backup_windows
        1 -> string.backup_android
        else -> string.no
    }
}

private fun dynamicVars() {
    permissiveAble = ShellUtils.fastCmd("getenforce") == "Permissive"
    WindowsIsPresent.value = when {
        ShellUtils.fastCmd("find $sdcardpath/Windows/explorer.exe")
            .isNotEmpty() -> string.yes

        else -> string.no
    }
    // TODO: Move to c++ implementation
    val find = ShellUtils.fastCmd("find /mnt/sdcard/UEFI/ -type f | grep .img")
    if (find.isNotEmpty()) {
        var index = 1
        for (uefi: String in arrayOf("60", "90", "120")) {
            val path =
                ShellUtils.fastCmd("find /mnt/sdcard/UEFI/ -type f  | grep .img | grep ${uefi}hz")
            if (path.isNotEmpty()
            ) {
                Device.uefiCardsArray += UEFICard(path, uefi.toInt())
            }
            index += 1
        }
        if (Device.uefiCardsArray.isEmpty()) {
            Device.uefiCardsArray += UEFICard(find, 1)
        }
    }
}
