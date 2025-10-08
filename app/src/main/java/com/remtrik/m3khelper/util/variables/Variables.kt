package com.remtrik.m3khelper.util.variables

import android.annotation.SuppressLint
import android.os.Build
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
import com.remtrik.m3khelper.util.DeviceCard
import com.remtrik.m3khelper.util.deviceCardsArray
import com.remtrik.m3khelper.util.funcs.CommandResult
import com.remtrik.m3khelper.util.funcs.Commands
import com.remtrik.m3khelper.util.funcs.ErrorType
import com.remtrik.m3khelper.util.funcs.string
import com.remtrik.m3khelper.util.specialDeviceCardsArray
import com.remtrik.m3khelper.util.unknownCard
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import kotlinx.parcelize.Parcelize
import java.util.concurrent.Executors

//private external fun findUEFIImages(baseCmd: String): IntArray
private external fun checkBootImages(noMount: Boolean, path: String): Int
private external fun getPanelNative(): String

@Parcelize
data class UEFICard(
    var uefiPath: String,
    val uefiType: Int,
) : Parcelable

@Parcelize
data class DeviceCommands(
    var mountPath: String = ""
) : Parcelable

data class DeviceData(
    var currentDeviceCard: DeviceCard = unknownCard,
    val deviceCodenames: List<String> =
        listOf(
            Build.DEVICE,
            ShellUtils.fastCmd("getprop ro.product.device"),
            ShellUtils.fastCmd("getprop ro.lineage.device")
        ),
    var savedDeviceCard: DeviceCard =
        deviceCardsArray.getOrNull(prefs.getInt("saved_device_card", 0))
            ?: unknownCard,
    var overrideDeviceCard: MutableState<Boolean> =
        mutableStateOf(prefs.getBoolean("override_device", false)),
    val ram: String = getMemory(),
    val slot: String = ShellUtils.fastCmd("getprop ro.boot.slot_suffix").drop(1).uppercase(),
    var panelType: MutableState<String> = mutableStateOf(
        prefs.getString("saved_device_panel", string.unknown_panel.string()).toString()
    ),
    var uefiCardsArray: List<UEFICard> = emptyList(),
    var special: MutableState<Boolean> = mutableStateOf(false),

    )

private val backgroundExecutor = Executors.newFixedThreadPool(2)

val device: DeviceData by lazy { DeviceData() }

//val SdcardPath: String by lazy { Environment.getExternalStorageDirectory().path }
const val SDCARD_PATH: String = "/sdcard"

val currentDeviceCommands: DeviceCommands by lazy { DeviceCommands() }

// UI State
private var BootIsPresent: MutableState<Int> = mutableIntStateOf(string.no)
private var WindowsIsPresent: MutableState<Int> = mutableIntStateOf(string.no)
val showWarningCard: MutableState<Boolean> = mutableStateOf(true)
var showAboutCard: MutableState<Boolean> = mutableStateOf(false)
var commandError: MutableState<String> = mutableStateOf("")
var showBootBackupErrorDialog: MutableState<Boolean> = mutableStateOf(false)
var showMountErrorDialog: MutableState<Boolean> = mutableStateOf(false)
var showQuickBootErrorDialog: MutableState<Boolean> = mutableStateOf(false)

lateinit var commandResult: CommandResult
val commandHandler: Commands = object : Commands() {}

// ui defaults
var FontSize: TextUnit = 0.sp
var PaddingValue: Dp = 0.dp
var LineHeight: TextUnit = 0.sp

// App State
val firstBoot: Boolean = prefs.getBoolean("firstboot", true)

@SuppressLint("RestrictedApi")
fun vars() {
    if (prefs.getString("version", "3.4") != BuildConfig.VERSION_NAME) {
        backgroundExecutor.execute {
            prefs.edit {
                putBoolean("firstboot", true)
                putString("version", BuildConfig.VERSION_NAME)
            }
            getPanel()
        }
        fetchDeviceCard()
    } else {
        backgroundExecutor.execute { fastLoadSavedDevice() }
    }

    // TODO: Examine the OS behavior with different paths
    /*CurrentDeviceCommands.mountPath = when {
        ShellUtils.fastCmd("find /mnt/pass_through -maxdepth 0")
            .isNotEmpty() -> "/mnt/pass_through/0/emulated/0" // passthrough+getExternalStorageDirectory maybe?
        else -> Environment.getExternalStorageDirectory().path
    }*/

    currentDeviceCommands.mountPath = "/sdcard"

    dynamicVars()
    bootBackupStatus()

    if (BuildConfig.DEBUG) {
        backgroundExecutor.execute(::debugLog)
    }
}

fun fetchDeviceCard() {
    var tmp = 0
    deviceCardsArray
        .find { card ->
            card.deviceCodename.any { deviceCodename ->
                device.deviceCodenames.any { normalizedCodename -> normalizedCodename == deviceCodename }
            }
        }?.let { card -> updateDeviceCard(deviceCardsArray.indexOf(card), card); tmp = 1 }
    if (tmp != 1) { // fallback to at least somewhat close device if cant find the exact match
        deviceCardsArray
            .find { card -> card.deviceCodename.any { device.deviceCodenames.contains(it) } }
            ?.let { card ->
                updateDeviceCard(deviceCardsArray.indexOf(card), card)
            }
    }
}

private fun updateDeviceCard(cardNum: Int, card: DeviceCard) {
    device.currentDeviceCard = card
    prefs.edit {
        putInt("saved_device_card", cardNum)
        putBoolean("firstboot", false)
        putBoolean("unknown", false)
    }
    device.savedDeviceCard = card
    isSpecial(card)
    showWarningCard.value = false
}

fun fastLoadSavedDevice(override: Boolean = device.overrideDeviceCard.value) {
    device.currentDeviceCard = if (override) {
        deviceCardsArray.find {
            it.deviceCodename.contains(
                prefs.getString(
                    "overriden_device_codename",
                    "vayu"
                ).toString()
            )
        } ?: device.savedDeviceCard
    } else {
        device.savedDeviceCard
    }
    if (device.panelType.value == string.unknown_panel.string()) getPanel()
    isSpecial(device.currentDeviceCard)
    showWarningCard.value = false
}

private fun getPanel() {
    device.panelType.value =
        getPanelNative().takeUnless { it == "Invalid" } ?: string.unknown_panel.string()
    prefs.edit { putString("saved_device_panel", device.panelType.value) }
}

fun bootBackupStatus() {
    backgroundExecutor.execute {
        commandHandler.withMountedWindows(ErrorType.MOUNT_ERROR) {
            BootIsPresent.value =
                when (checkBootImages(device.currentDeviceCard.noMount, SDCARD_PATH)) {
                    3 -> string.backup_both
                    2 -> string.backup_windows
                    1 -> string.backup_android
                    else -> string.no
                }
        }
    }
}

private fun dynamicVars() {
    commandHandler.withMountedWindows(ErrorType.MOUNT_ERROR) {
        WindowsIsPresent.value = when {
            ShellUtils.fastCmd("find $SDCARD_PATH/Windows/explorer.exe")
                .isNotEmpty() -> string.yes

            else -> string.no
        }
    }
    if (device.uefiCardsArray.isEmpty()) {
        val find = Shell.cmd("find /mnt/sdcard/UEFI/ -type f | grep .img").exec()
        if (find.isSuccess && device.uefiCardsArray.isEmpty()) {
            backgroundExecutor.execute {
                device.uefiCardsArray = find.out
                    .filter { it.contains("hz") }
                    .mapNotNull { path ->
                        when {
                            path.contains("120hz") -> UEFICard(path, 120)
                            path.contains("90hz") -> UEFICard(path, 90)
                            path.contains("60hz") -> UEFICard(path, 60)
                            else -> null
                        }
                    }
                    .ifEmpty { listOf(UEFICard(find.out.first(), 1)) }
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
    return remember(BootIsPresent.value, device.currentDeviceCard, WindowsIsPresent.value) {
        DeviceStrings(
            woa = string.woa.string(),
            model = M3KApp.getString(
                string.model,
                device.currentDeviceCard.deviceName,
                device.currentDeviceCard.deviceCodename[0]
            ),
            ram = M3KApp.getString(string.ramvalue, device.ram),
            panel = M3KApp.getString(string.paneltype, device.panelType.value),
            bootState = if (!device.currentDeviceCard.noBoot && !device.currentDeviceCard.noMount) {
                M3KApp.getString(string.backup_boot_state, M3KApp.getString(BootIsPresent.value))
            } else null,
            slot = if (device.slot.isNotEmpty()) {
                M3KApp.getString(string.slot, device.slot)
            } else null,
            windowsStatus = if (!device.currentDeviceCard.noMount) {
                M3KApp.getString(string.windows_status, M3KApp.getString(WindowsIsPresent.value))
            } else null
        )
    }
}

@Suppress("NOTHING_TO_INLINE")
private inline fun isSpecial(card: DeviceCard) {
    device.special.value = specialDeviceCardsArray.contains(card)
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

private fun debugLog() {
    println("M3K Helper - First Boot: $firstBoot")
    println("M3K Helper - Boot is present: ${BootIsPresent.value.string()}")
    println("M3K Helper - Windows is present: ${WindowsIsPresent.value.string()}")
    println("M3K Helper - Panel Type: ${device.panelType.value}")
    device.deviceCodenames
        .filterNot { it.isEmpty() }
        .forEach { println("M3K Helper - Device codename: $it") }
    println("M3K Helper - Current device: ${device.currentDeviceCard.deviceName}")
    println("M3K Helper - Saved device: ${device.savedDeviceCard.deviceName}")
    println(
        "M3K Helper - Override device enabled: ${device.overrideDeviceCard.value} ${
            if (device.overrideDeviceCard.value) {
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
    println("M3K Helper - Current mount path: ${currentDeviceCommands.mountPath}")
}