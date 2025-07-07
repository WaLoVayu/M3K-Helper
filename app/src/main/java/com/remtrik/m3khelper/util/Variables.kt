package com.remtrik.m3khelper.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.remtrik.m3khelper.M3KApp
import com.remtrik.m3khelper.R
import com.topjohnwu.superuser.ShellUtils
import kotlin.properties.Delegates

data class UEFICard(
    var uefiPath: String,
    val uefiType: Int,
)

// static vars
val deviceCardsArray: Array<DeviceCard> =
    arrayOf(
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

@SuppressLint("UnknownNullness")
val prefs: SharedPreferences = M3KApp.getSharedPreferences("settings", Context.MODE_PRIVATE)

// device info
val Ram: String = getMemory(M3KApp)
val Slot: String = ShellUtils.fastCmd("getprop ro.boot.slot_suffix").drop(1).uppercase()
private var Codename1: String = Build.DEVICE
private var Codename2: String = ShellUtils.fastCmd("getprop ro.product.device")
private var Codename3: String = ShellUtils.fastCmd("getprop ro.lineage.device")
lateinit var PanelType: String

var CurrentDeviceCard: DeviceCard = unknownCard

// dynamic vars
var BootIsPresent: Int by Delegates.notNull()
var WindowsIsPresent: Int by Delegates.notNull()
val Warning: MutableState<Boolean> = mutableStateOf(true)
var showAboutCard: MutableState<Boolean> = mutableStateOf(false)
var UEFIList: Array<Int> = arrayOf()

var FontSize: TextUnit = 0.sp
var PaddingValue: Dp = 0.dp
var LineHeight: TextUnit = 0.sp

@SuppressLint("RestrictedApi")
fun vars() {
    if ((prefs.getBoolean("firstboot", true)) || (prefs.getBoolean("unknown", true))) {
        var cardNum = 0
        for (card: DeviceCard in deviceCardsArray) {
            for (num: String in card.deviceCodename) {
                if ((prefs.getBoolean("override_device", false) && prefs.getString("overriden_device_codename", "vayu")?.contains(num) == true)) {
                    CurrentDeviceCard = card; break
                } else {
                    if (Codename1.contains(num)) {
                        CurrentDeviceCard = card; CurrentDeviceCard.deviceCodename[0] = Codename1
                        prefs.edit { putInt("deviceCard", cardNum) }; prefs.edit { putBoolean("firstboot", false) }; prefs.edit { putBoolean("unknown", false) }
                        break
                    } else if (Codename2.contains(num)) {
                        CurrentDeviceCard = card; CurrentDeviceCard.deviceCodename[0] = Codename2
                        prefs.edit { putInt("deviceCard", cardNum) }; prefs.edit { putBoolean("firstboot", false) }; prefs.edit { putBoolean("unknown", false) }
                        break
                    } else if (Codename3.contains(num)) {
                        CurrentDeviceCard = card; CurrentDeviceCard.deviceCodename[0] = Codename3
                        prefs.edit { putInt("deviceCard", cardNum) }; prefs.edit { putBoolean("firstboot", false) }; prefs.edit { putBoolean("unknown", false) }
                        break
                    }
                }
            }
            if (CurrentDeviceCard != unknownCard) {
                Warning.value = false; break
            }
            cardNum += 1
        }

        val panel = ShellUtils.fastCmd("cat /proc/cmdline")
        PanelType = when {
            panel.contains("j20s_42")
                    || panel.contains("k82_42")
                    || panel.contains("huaxing") -> "Huaxing"

            panel.contains("j20s_36")
                    || panel.contains("tianma")
                    || panel.contains("k82_36") -> "Tianma"

            panel.contains("ebbg") -> "EBBG"

            panel.contains("samsung")
                    || panel.contains("ea8076_f1mp")
                    || panel.contains("ea8076_f1p2")
                    || panel.contains("ea8076_global")
                    || panel.contains("S6E3FC3")
                    || panel.contains("AMS646YD01") -> "Samsung"

            else -> M3KApp.getString(R.string.unknown_panel)
        }
        prefs.edit { putString("devicePanel", PanelType) }

    } else {
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
        Warning.value = false;
        PanelType = prefs.getString("devicePanel", M3KApp.getString(R.string.unknown_panel)).toString()
        Warning.value = false
    }

    if (mountStatus()) {
        mountWindows()
        dynamicVars(); bootBackupStatus()
        umountWindows()
    } else {
        dynamicVars(); bootBackupStatus()
    }


}

fun bootBackupStatus() {
    val win = if (!CurrentDeviceCard.noMount) ShellUtils.fastCmd("find /sdcard/Windows/boot.img") else null
    val android = ShellUtils.fastCmd("find /sdcard/boot.img")
    BootIsPresent = when {
        !win.isNullOrEmpty() && android.isNotEmpty() -> R.string.backup_both
        !win.isNullOrEmpty() -> R.string.backup_windows
        android.isNotEmpty() -> R.string.backup_android

        else -> R.string.no
    }
}

fun dynamicVars() {
    WindowsIsPresent = when {
        ShellUtils.fastCmd("find /sdcard/Windows/Windows/explorer.exe")
            .isNotEmpty() -> R.string.yes

        else -> R.string.no
    }
    UEFIList = arrayOf()
    val find = ShellUtils.fastCmd("find /mnt/sdcard/UEFI/ -type f | grep .img")
    if (find.isNotEmpty()) {
        var index = 1
        for (uefi: String in arrayOf("60", "90", "120")) {
            val path = ShellUtils.fastCmd("find /mnt/sdcard/UEFI/ -type f  | grep .img | grep $uefi" + "hz")
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
