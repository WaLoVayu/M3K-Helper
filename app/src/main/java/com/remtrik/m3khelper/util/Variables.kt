package com.remtrik.m3khelper.util

import android.annotation.SuppressLint
import android.os.Build
import android.os.Environment
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.remtrik.m3khelper.BuildConfig
import com.remtrik.m3khelper.M3KApp
import com.remtrik.m3khelper.R.string
import com.remtrik.m3khelper.prefs
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
    var savedDeviceCard: DeviceCard =
        try {
            deviceCardsArray[prefs.getInt("saved_device_card", 0)]
        } catch (_: ArrayIndexOutOfBoundsException) {
            unknownCard
        },
    val ram: String = getMemory(M3KApp),
    val slot: String = ShellUtils.fastCmd("getprop ro.boot.slot_suffix").drop(1).uppercase(),
    var panelType: MutableState<String> = mutableStateOf(
        prefs.getString("saved_device_panel", M3KApp.getString(string.unknown_panel)).toString()
    ),
    var uefiCardsArray: Array<UEFICard> = arrayOf(),
    var special: MutableState<Boolean> = mutableStateOf(false)
)

@Parcelize
data class DeviceCommands(
    var mountPath: String = ""
) : Parcelable

// device state


val Device: DeviceData by lazy { DeviceData() }


// ui state
private var BootIsPresent: MutableState<Int> = mutableIntStateOf(string.no)
private var WindowsIsPresent: MutableState<Int> = mutableIntStateOf(string.no)
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

val SdcardPath: String = Environment.getExternalStorageDirectory().path

var PermissiveAble: Boolean = false

@SuppressLint("RestrictedApi")
fun vars() {
    if (prefs.getString("version", "3.4") != BuildConfig.VERSION_NAME) {
        prefs.edit {
            putBoolean("firstboot", true)
            putString("version", BuildConfig.VERSION_NAME)
        }
    }
    if (prefs.getBoolean("firstboot", true)) {
        deviceCardsArray.forEachIndexed { cardNum, card ->
            if (card.deviceCodename.any { Device.deviceCodenames.contains(it) }) {
                Device.currentDeviceCard = card; Device.currentDeviceCard.deviceCodename[0]
                prefs.edit {
                    putInt(
                        "saved_device_card",
                        cardNum
                    )
                    putBoolean(
                        "firstboot",
                        false
                    )
                    putBoolean("unknown", false)
                }
                Device.savedDeviceCard = card
                isSpecial(card)
                Warning.value = false
                return@forEachIndexed
            }
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

    withMountedWindows {
        dynamicVars()
        bootBackupStatus()
    }


    if (BuildConfig.DEBUG) {
        println("M3K Helper - First Boot: $FirstBoot")
        println("M3K Helper - Boot is present: ${M3KApp.getString(BootIsPresent.value)}")
        println("M3K Helper - Windows is present: ${M3KApp.getString(WindowsIsPresent.value)}")
        println("M3K Helper - Panel Type: ${Device.panelType.value}")
        Device.deviceCodenames.forEach { println("M3K Helper - Device codename: $it") }
        println("M3K Helper - Current device: ${Device.currentDeviceCard.deviceName}")
        println("M3K Helper - Saved device: ${Device.savedDeviceCard.deviceName}")
        println(
            "M3K Helper - Override device enabled: $OverrideDevice ${
                if (OverrideDevice) {
                    "\nM3K Helper - Override device codename: ${
                        prefs.getString(
                            "overriden_device_codename",
                            "vayu"
                        )
                    }"
                } else {
                }
            }"
        )
        println("M3K Helper - Current mount path: ${CurrentDeviceCommands.mountPath}")
    }
}

fun fastLoadSavedDevice(override: Boolean = OverrideDevice) {
    Device.currentDeviceCard = if (override) {
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
    isSpecial(Device.currentDeviceCard)
    Warning.value = false
}

private fun getPanel() {
    Device.panelType.value = getPanelNative()
    if (Device.panelType.value == "Invalid") Device.panelType.value =
        M3KApp.getString(string.unknown_panel)
    prefs.edit { putString("saved_device_panel", Device.panelType.value) }
}

fun bootBackupStatus() {
    BootIsPresent.value = when (checkBootImages(Device.currentDeviceCard.noMount, SdcardPath)) {
        3 -> string.backup_both
        2 -> string.backup_windows
        1 -> string.backup_android
        else -> string.no
    }
}

private fun dynamicVars() {
    PermissiveAble = ShellUtils.fastCmd("getenforce") == "Permissive"
    WindowsIsPresent.value = when {
        ShellUtils.fastCmd("find $SdcardPath/Windows/explorer.exe")
            .isNotEmpty() -> string.yes

        else -> string.no
    }
    // TODO: Move to c++ implementation
    if (Device.uefiCardsArray.isEmpty()) {
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
}

// MAKE THIS THE MAIN THING
// MAKE THIS THE MAIN THING
// MAKE THIS THE MAIN THING
// MAKE THIS THE MAIN THING
// MAKE THIS THE MAIN THING
// MAKE THIS THE MAIN THING
// MAKE THIS THE MAIN THING
// MAKE THIS THE MAIN THING
// MAKE THIS THE MAIN THING
@Composable
fun rememberDeviceStrings(): DeviceStrings {
    return remember(BootIsPresent.value, Device.currentDeviceCard, WindowsIsPresent.value) {
        DeviceStrings(
            woa = M3KApp.getString(string.woa),
            model = M3KApp.getString(
                string.model,
                Device.currentDeviceCard.deviceName,
                Device.currentDeviceCard.deviceCodename[0]
            ),
            ram = M3KApp.getString(string.ramvalue, Device.ram),
            panel = M3KApp.getString(string.paneltype, Device.panelType.value),
            bootState = if (!Device.currentDeviceCard.noBoot && !Device.currentDeviceCard.noMount) {
                M3KApp.getString(string.backup_boot_state, M3KApp.getString(BootIsPresent.value))
            } else null,
            slot = if (Device.slot.isNotEmpty()) {
                M3KApp.getString(string.slot, Device.slot)
            } else null,
            windowsStatus = if (!Device.currentDeviceCard.noMount) {
                M3KApp.getString(string.windows_status, M3KApp.getString(WindowsIsPresent.value))
            } else null
        )
    }
}

@Suppress("NOTHING_TO_INLINE")
private inline fun isSpecial(card: DeviceCard) {
    Device.special.value = specialDeviceCardsArray.contains(card)
}

data class DeviceStrings(
    val woa: String,
    val model: String,
    val ram: String,
    val panel: String,
    val bootState: String?,
    val slot: String?,
    val windowsStatus: String?
)
