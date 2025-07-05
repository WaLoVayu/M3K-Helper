package com.remtrik.m3khelper.util

import android.content.Context
import android.content.Intent
import com.topjohnwu.superuser.ShellUtils

fun dumpBoot(where: Int) {
    val slot = ShellUtils.fastCmd("getprop ro.boot.slot_suffix")
    when (where) {
        1 -> {
            if (mountStatus()) {
                mountWindows()
                ShellUtils.fastCmd("dd if=/dev/block/bootdevice/by-name/boot$slot of=/sdcard/Windows/boot.img bs=32MB")
                umountWindows()
            } else ShellUtils.fastCmd("dd if=/dev/block/bootdevice/by-name/boot$slot of=/sdcard/Windows/boot.img bs=32MB")
        }

        2 -> {
            ShellUtils.fastCmd("rm -rf /sdcard/m3khelper || true ")
            ShellUtils.fastCmd("dd if=/dev/block/bootdevice/by-name/boot$slot of=/sdcard/boot.img")
        }
    }
}

fun mountStatus(): Boolean {
    return ShellUtils.fastCmd("mount | grep " + ShellUtils.fastCmd("readlink -fn /dev/block/bootdevice/by-name/win"))
        .isEmpty()
}

fun mountWindows() {
    ShellUtils.fastCmd("mkdir /mnt/sdcard/Windows || true")
    ShellUtils.fastCmd("su -mm -c mount.ntfs /dev/block/by-name/win /sdcard/Windows")
}

fun umountWindows() {
    ShellUtils.fastCmd("su -mm -c umount /mnt/sdcard/Windows")
    ShellUtils.fastCmd("rmdir /mnt/sdcard/Windows")
}

fun dumpModem() {
    if (mountStatus()) {
        mountWindows()
        val path = ShellUtils.fastCmd("find /sdcard/Windows/Windows/System32/DriverStore/FileRepository -name qcremotefs8150.inf_arm64_*")
        ShellUtils.fastCmd("dd if=/dev/block/bootdevice/by-name/modemst1 of=$path/bootmodem_fs1 bs=8388608")
        ShellUtils.fastCmd("dd if=/dev/block/bootdevice/by-name/modemst2 of=$path/bootmodem_fs2 bs=8388608")
        umountWindows()
    } else {
        val path = ShellUtils.fastCmd("find /sdcard/Windows/Windows/System32/DriverStore/FileRepository -name qcremotefs8150.inf_arm64_*")
        ShellUtils.fastCmd("dd if=/dev/block/bootdevice/by-name/modemst1 of=$path/bootmodem_fs1 bs=8388608")
        ShellUtils.fastCmd("dd if=/dev/block/bootdevice/by-name/modemst2 of=$path/bootmodem_fs2 bs=8388608")
    }
}

fun flashUEFI(uefiPath: String) {
    val slot = ShellUtils.fastCmd("getprop ro.boot.slot_suffix")
    ShellUtils.fastCmd("dd if=$uefiPath of=/dev/block/bootdevice/by-name/boot$slot")
}

fun checkSensors(): Boolean {
    return if (!CurrentDeviceCard.sensors) true
    else {
        if (mountStatus()) {
            mountWindows()
            val check =
                ShellUtils.fastCmd("find /sdcard/Windows/Windows/System32/Drivers/DriverData/QUALCOMM/fastRPC/persist/sensors/*")
                    .isNotEmpty()
            umountWindows()
            check
        } else {
            val check =
                ShellUtils.fastCmd("find /sdcard/Windows/Windows/System32/Drivers/DriverData/QUALCOMM/fastRPC/persist/sensors/*")
                    .isNotEmpty()
            check
        }
    }
}

fun dumpSensors() {
    if (mountStatus()) {
        mountWindows()
        ShellUtils.fastCmd("cp -r /vendor/persist/sensors/* /sdcard/Windows/Windows/System32/Drivers/DriverData/QUALCOMM/fastRPC/persist/sensors")
        umountWindows()
    } else {
        ShellUtils.fastCmd("cp -r /vendor/persist/sensors/* /sdcard/Windows/Windows/System32/Drivers/DriverData/QUALCOMM/fastRPC/persist/sensors")
    }
}

fun quickboot(uefiPath: String) {
    if (!CurrentDeviceCard.noMount) {
        if (ShellUtils.fastCmd("find /sdcard/Windows/boot.img")
                .isEmpty()
        ) {
            dumpBoot(1)
        }
        if (!CurrentDeviceCard.noModem) {
            dumpModem()
        }
        if (CurrentDeviceCard.sensors && !checkSensors()) {
            dumpSensors()
        }
    }
    if (ShellUtils.fastCmd("find /sdcard/boot.img")
            .isEmpty()
    ) {
        dumpBoot(2)
    }
    flashUEFI(uefiPath)
    ShellUtils.fastCmd("svc power reboot")
}

fun Context.restart() {
    val packageManager = packageManager
    val intent = packageManager.getLaunchIntentForPackage(packageName)!!
    val componentName = intent.component!!
    val restartIntent = Intent.makeRestartActivityTask(componentName)
    startActivity(restartIntent)
    Runtime.getRuntime().exit(0)
}
