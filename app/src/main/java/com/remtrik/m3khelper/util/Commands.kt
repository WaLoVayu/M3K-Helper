package com.remtrik.m3khelper.util

import android.content.Context
import android.content.Intent
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils

fun dumpBoot(where: Int): Boolean {
    val slot = ShellUtils.fastCmd("getprop ro.boot.slot_suffix")
    var status = true
    when (where) {
        1 -> {
            withMountedWindows {
                status = Shell.cmd("dd if=/dev/block/bootdevice/by-name/boot$slot of=$SdcardPath/Windows/boot.img bs=32M").exec().isSuccess
            }
        }

        2 -> {
            ShellUtils.fastCmd("rm -rf $SdcardPath/m3khelper || true ")
            status = Shell.cmd("dd if=/dev/block/bootdevice/by-name/boot$slot of=$SdcardPath/boot.img").exec().isSuccess
        }
    }
    bootBackupStatus()
    return status
}

fun isMounted(): Boolean {
    return ShellUtils.fastCmd("mount | grep ${ShellUtils.fastCmd("readlink -fn /dev/block/bootdevice/by-name/win")}")
        .isEmpty()
}

fun mountWindows() {
    ShellUtils.fastCmd("mkdir $SdcardPath/Windows || true")
    ShellUtils.fastCmd("su -mm -c mount.ntfs /dev/block/by-name/win ${CurrentDeviceCommands.mountPath}/Windows")
}

fun umountWindows() {
    ShellUtils.fastCmd("su -mm -c umount ${CurrentDeviceCommands.mountPath}/Windows")
    ShellUtils.fastCmd("rmdir $SdcardPath/Windows")
}

fun withMountedWindows(block: () -> Unit) {
    val wasMounted = isMounted()
    try {
        if (wasMounted) mountWindows()
        block()
        true
    } finally {
        if (wasMounted) umountWindows()
    }
}

fun dumpModem() {
    withMountedWindows {
        val path =
            ShellUtils.fastCmd("find $SdcardPath/Windows/Windows/System32/DriverStore/FileRepository -name qcremotefs8150.inf_arm64_*")
        ShellUtils.fastCmd("dd if=/dev/block/bootdevice/by-name/modemst1 of=$path/bootmodem_fs1 bs=8388608")
        ShellUtils.fastCmd("dd if=/dev/block/bootdevice/by-name/modemst2 of=$path/bootmodem_fs2 bs=8388608")
    }
}

fun flashUEFI(uefiPath: String): Boolean {
    val slot = ShellUtils.fastCmd("getprop ro.boot.slot_suffix")
    return Shell.cmd("dd if=$uefiPath of=/dev/block/bootdevice/by-name/boot$slot").exec().isSuccess
}

fun checkSensors(): Boolean {
    return if (!Device.currentDeviceCard.sensors) true
    else {
        var check = false
        withMountedWindows {
            check =
                ShellUtils.fastCmd("find $SdcardPath/Windows/Windows/System32/Drivers/DriverData/QUALCOMM/fastRPC/persist/sensors/*")
                    .isNotEmpty()
        }
        check
    }
}

fun dumpSensors() {
    withMountedWindows {
        ShellUtils.fastCmd("cp -r /vendor/persist/sensors/* $SdcardPath/Windows/Windows/System32/Drivers/DriverData/QUALCOMM/fastRPC/persist/sensors")
    }
}

fun quickBoot(uefiPath: String) {
    if (!Device.currentDeviceCard.noMount) {
        if (ShellUtils.fastCmd("find $SdcardPath/Windows/boot.img")
                .isEmpty()
        ) {
            if (!dumpBoot(1)) {
                showBootBackupErorDialog.value = true
                return
            }
        }
        if (!Device.currentDeviceCard.noModem) {
            dumpModem()
        }
        if (Device.currentDeviceCard.sensors && !checkSensors()) {
            dumpSensors()
        }
    }
    if (ShellUtils.fastCmd("find $SdcardPath/boot.img")
            .isEmpty()
    ) {
        dumpBoot(2)
    }
    if (flashUEFI(uefiPath)) {
        ShellUtils.fastCmd("svc power reboot")
    } else {
        showUEFIFlashErorDialog.value = true
        return
    }
}

fun Context.restart() {
    try {
        packageManager.getLaunchIntentForPackage(packageName)?.let {
            startService(Intent.makeRestartActivityTask(it.component))
            Runtime.getRuntime().exit(0)
        }
    } catch (e: Exception) {
        println("M3K Helper - $e")
    }
}
