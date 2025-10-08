package com.remtrik.m3khelper.util.funcs

import android.content.Context
import android.content.Intent
import com.remtrik.m3khelper.R
import com.remtrik.m3khelper.util.variables.commandHandler
import com.remtrik.m3khelper.util.variables.device
import com.remtrik.m3khelper.util.variables.SDCARD_PATH
import com.remtrik.m3khelper.util.variables.bootBackupStatus
import com.remtrik.m3khelper.util.variables.commandError
import com.remtrik.m3khelper.util.variables.commandResult
import com.remtrik.m3khelper.util.variables.showBootBackupErrorDialog
import com.remtrik.m3khelper.util.variables.showMountErrorDialog
import com.remtrik.m3khelper.util.variables.showQuickBootErrorDialog
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils

internal val BlockWindowsPath by lazy {
    ShellUtils.fastCmd("readlink -fn /dev/block/bootdevice/by-name/win")
}

abstract class Commands {
    lateinit var internalLastCommandResult: Shell.Result
    lateinit var internalCommandResult: Shell.Result

    fun dumpBoot(type: ErrorType, where: Int): CommandResult {
        val slot = ShellUtils.fastCmd("getprop ro.boot.slot_suffix")
        when (where) {
            1 -> {
                if (!withMountedWindows(type)
                    {
                        internalLastCommandResult =
                            Shell.cmd("dd if=/dev/block/bootdevice/by-name/boot$slot of=${SDCARD_PATH}/Windows/boot.img bs=32M")
                                .exec()
                    }
                ) return CommandResult(
                    false,
                    MutableList(1) { R.string.mount_error_default.string() },
                    MutableList(1) { R.string.mount_error_default.string() }
                )
            }

            2 -> {
                ShellUtils.fastCmd("rm -rf ${SDCARD_PATH}/m3khelper || true ")
                internalLastCommandResult =
                    Shell.cmd("dd if=/dev/block/bootdevice/by-name/boot$slot of=${SDCARD_PATH}/boot.img")
                        .exec()
            }
        }
        bootBackupStatus()
        return try {
            CommandResult(
                internalLastCommandResult.isSuccess,
                internalLastCommandResult.out,
                internalLastCommandResult.err
            )
        } catch (_: UninitializedPropertyAccessException) {
            try {
                CommandResult(
                    internalCommandResult.isSuccess,
                    internalCommandResult.out,
                    internalCommandResult.err
                )
            } catch (_: UninitializedPropertyAccessException) {
                CommandResult(
                    false,
                    MutableList(1) { R.string.mount_error_default.string() },
                    MutableList(1) { R.string.mount_error_default.string() }
                )
            }
        }
    }

    fun mountWindows(): CommandResult {
        Shell.cmd("mkdir ${SDCARD_PATH}/Windows").exec()
        internalCommandResult =
            Shell.cmd("su -mm -c mount.ntfs /dev/block/by-name/win ${SDCARD_PATH}/Windows")
                .exec()
        return CommandResult(
            internalCommandResult.isSuccess,
            internalCommandResult.out,
            internalCommandResult.err
        )
    }

    fun umountWindows(): CommandResult {
        internalCommandResult =
            Shell.cmd("su -mm -c umount ${SDCARD_PATH}/Windows")
                .exec()
        Shell.cmd("rmdir ${SDCARD_PATH}/Windows").exec()
        return CommandResult(
            internalCommandResult.isSuccess,
            internalCommandResult.out,
            internalCommandResult.err
        )
    }

    fun isMounted(): MountStatus {
        val result = Shell.cmd("mount | grep $BlockWindowsPath").exec()
        return if (result.isSuccess && result.out[0].contains("Windows")) MountStatus.MOUNTED else MountStatus.NOT_MOUNTED
    }

    private fun checkSensors(type: ErrorType): Boolean {
        return if (!device.currentDeviceCard.sensors) true
        else {
            var check = false
            withMountedWindows(type)
            {
                check =
                    ShellUtils.fastCmd("find ${SDCARD_PATH}/Windows/Windows/System32/Drivers/DriverData/QUALCOMM/fastRPC/persist/sensors/*")
                        .isNotEmpty()
            }
            check
        }
    }

    private fun dumpSensors(type: ErrorType): CommandResult {
        withMountedWindows(type)
        {
            internalLastCommandResult =
                Shell.cmd("cp -r /vendor/persist/sensors/* ${SDCARD_PATH}/Windows/Windows/System32/Drivers/DriverData/QUALCOMM/fastRPC/persist/sensors")
                    .exec()
        }
        return CommandResult(
            internalLastCommandResult.isSuccess,
            internalLastCommandResult.out,
            internalLastCommandResult.err
        )
    }

    private fun dumpModem(type: ErrorType): CommandResult {
        withMountedWindows(type)
        {
            val path =
                ShellUtils.fastCmd("find ${SDCARD_PATH}/Windows/Windows/System32/DriverStore/FileRepository -name qcremotefs8150.inf_arm64_*")
            internalLastCommandResult =
                Shell.cmd("dd if=/dev/block/bootdevice/by-name/modemst1 of=$path/bootmodem_fs1 bs=8388608")
                    .exec()
            internalLastCommandResult =
                Shell.cmd("dd if=/dev/block/bootdevice/by-name/modemst2 of=$path/bootmodem_fs2 bs=8388608")
                    .exec()
        }
        return CommandResult(
            internalLastCommandResult.isSuccess,
            internalLastCommandResult.out,
            internalLastCommandResult.err
        )
    }

    private fun uefitell(uefiPath: String): Shell.Result {
        return Shell.cmd("dd if=$uefiPath of=/dev/block/bootdevice/by-name/boot${device.slot}").exec()
    }

    fun flashUEFI(uefiPath: String): CommandResult {
        internalLastCommandResult = uefitell(uefiPath)
        return CommandResult(
            internalLastCommandResult.isSuccess,
            internalLastCommandResult.out,
            internalLastCommandResult.err
        )
    }

    fun quickBoot(uefiPath: String) {
        if (!device.currentDeviceCard.noMount) {
            if (ShellUtils.fastCmd("find ${SDCARD_PATH}/Windows/boot.img")
                    .isEmpty()
            ) {
                commandResult = dumpBoot(ErrorType.QUICKBOOT_ERROR, 1)
                if (!commandResult.isSuccess) {
                    handleErrorType(ErrorType.QUICKBOOT_ERROR, commandResult)
                    return
                }
            }
            if (!device.currentDeviceCard.noModem) {
                commandResult = dumpModem(ErrorType.QUICKBOOT_ERROR)
                if (!commandResult.isSuccess) {
                    handleErrorType(ErrorType.QUICKBOOT_ERROR, commandResult)
                    return
                }
            }
            if (device.currentDeviceCard.sensors && !checkSensors(ErrorType.QUICKBOOT_ERROR)) {
                commandResult = dumpSensors(ErrorType.QUICKBOOT_ERROR)
                if (!commandResult.isSuccess) {
                    handleErrorType(ErrorType.QUICKBOOT_ERROR, commandResult)
                    return
                }
            }
        }
        if (ShellUtils.fastCmd("find ${SDCARD_PATH}/boot.img")
                .isEmpty()
        ) {
            commandResult = dumpBoot(ErrorType.QUICKBOOT_ERROR, 2)
            if (!commandResult.isSuccess) {
                handleErrorType(ErrorType.QUICKBOOT_ERROR, commandResult)
                return
            }
        }
        commandResult = flashUEFI(uefiPath)
        if (!commandResult.isSuccess) {
            handleErrorType(ErrorType.QUICKBOOT_ERROR, commandResult)
            return
        }
        Shell.cmd("svc power reboot").exec()
    }

    fun withMountedWindows(type: ErrorType, block: () -> Unit): Boolean {
        val wasMounted = isMounted()
        try {
            if (wasMounted == MountStatus.NOT_MOUNTED && !device.currentDeviceCard.noMount) commandResult = commandHandler.mountWindows()
            if (
                try {
                    !internalCommandResult.isSuccess
                } catch (_: UninitializedPropertyAccessException) {
                    return false
                }
            ) {
                handleErrorType(type, commandResult)
                if (type != ErrorType.MOUNT_ERROR) {
                    return false
                }
            }
            block()
        } finally {
            if (wasMounted == MountStatus.NOT_MOUNTED && !device.currentDeviceCard.noMount) commandResult = commandHandler.umountWindows()
            if (
                try {
                    !internalCommandResult.isSuccess
                } catch (_: UninitializedPropertyAccessException) {
                    return false
                }
            ) {
                handleErrorType(type, commandResult)
            }
            return true
        }
    }

    private fun handleErrorType(type: ErrorType, result: CommandResult) {
        commandError.value = result.output.firstOrNull() ?: ""
        when (type) {
            ErrorType.MOUNT_ERROR -> showMountErrorDialog.value = true
            ErrorType.BOOTBACKUP_ERROR -> showBootBackupErrorDialog.value = true
            ErrorType.QUICKBOOT_ERROR -> showQuickBootErrorDialog.value = true
        }
    }
}

fun Context.restart() {
    runCatching {
        packageManager.getLaunchIntentForPackage(packageName)?.let {
            startService(Intent.makeRestartActivityTask(it.component))
            Runtime.getRuntime().exit(0)
        }
    }.onFailure { e ->
        println("M3K Helper - $e")
    }
}

data class CommandResult(
    val isSuccess: Boolean,
    val output: MutableList<String>,
    val error: MutableList<String>
)

enum class ErrorType {
    MOUNT_ERROR,
    BOOTBACKUP_ERROR,
    QUICKBOOT_ERROR
}

enum class MountStatus {
    NOT_MOUNTED,
    MOUNTED,
}