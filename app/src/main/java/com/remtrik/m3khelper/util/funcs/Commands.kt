package com.remtrik.m3khelper.util.funcs

import android.content.Context
import android.content.Intent
import com.remtrik.m3khelper.R
import com.remtrik.m3khelper.util.variables.CommandHandler
import com.remtrik.m3khelper.util.variables.Device
import com.remtrik.m3khelper.util.variables.SdcardPath
import com.remtrik.m3khelper.util.variables.bootBackupStatus
import com.remtrik.m3khelper.util.variables.commandError
import com.remtrik.m3khelper.util.variables.commandResult
import com.remtrik.m3khelper.util.variables.showBootBackupErrorDialog
import com.remtrik.m3khelper.util.variables.showMountErrorDialog
import com.remtrik.m3khelper.util.variables.showQuickBootErrorDialog
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils

private val BlockWindowsPath =
    ShellUtils.fastCmd("readlink -fn /dev/block/bootdevice/by-name/win")

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
                            Shell.cmd("dd if=/dev/block/bootdevice/by-name/boot$slot of=${SdcardPath}/Windows/boot.img bs=32M")
                                .exec()
                    }
                ) return CommandResult(
                    false,
                    MutableList(1) { R.string.mount_error_default.string() },
                    MutableList(1) { R.string.mount_error_default.string() }
                )
            }

            2 -> {
                ShellUtils.fastCmd("rm -rf ${SdcardPath}/m3khelper || true ")
                internalLastCommandResult =
                    Shell.cmd("dd if=/dev/block/bootdevice/by-name/boot$slot of=${SdcardPath}/boot.img")
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
        Shell.cmd("mkdir ${SdcardPath}/Windows").exec()
        internalCommandResult =
            Shell.cmd("su -mm -c mount.ntfs /dev/block/by-name/win ${SdcardPath}/Windows")
                .exec()
        return CommandResult(
            internalCommandResult.isSuccess,
            internalCommandResult.out,
            internalCommandResult.err
        )
    }

    fun umountWindows(): CommandResult {
        internalCommandResult =
            Shell.cmd("su -mm -c umount ${SdcardPath}/Windows")
                .exec()
        Shell.cmd("rmdir ${SdcardPath}/Windows").exec()
        return CommandResult(
            internalCommandResult.isSuccess,
            internalCommandResult.out,
            internalCommandResult.err
        )
    }

    fun isMounted(): Boolean {
        val tmp = Shell.cmd("mount | grep $BlockWindowsPath").exec()
        return tmp.isSuccess && tmp.out[0].contains("Windows")
    }

    private fun checkSensors(type: ErrorType): Boolean {
        return if (!Device.currentDeviceCard.sensors) true
        else {
            var check = false
            withMountedWindows(type)
            {
                check =
                    ShellUtils.fastCmd("find ${SdcardPath}/Windows/Windows/System32/Drivers/DriverData/QUALCOMM/fastRPC/persist/sensors/*")
                        .isNotEmpty()
            }
            check
        }
    }

    private fun dumpSensors(type: ErrorType): CommandResult {
        withMountedWindows(type)
        {
            internalLastCommandResult =
                Shell.cmd("cp -r /vendor/persist/sensors/* ${SdcardPath}/Windows/Windows/System32/Drivers/DriverData/QUALCOMM/fastRPC/persist/sensors")
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
                ShellUtils.fastCmd("find ${SdcardPath}/Windows/Windows/System32/DriverStore/FileRepository -name qcremotefs8150.inf_arm64_*")
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
        val slot = ShellUtils.fastCmd("getprop ro.boot.slot_suffix")
        return Shell.cmd("dd if=$uefiPath of=/dev/block/bootdevice/by-name/boot$slot").exec()
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
        if (!Device.currentDeviceCard.noMount) {
            if (ShellUtils.fastCmd("find ${SdcardPath}/Windows/boot.img")
                    .isEmpty()
            ) {
                commandResult = dumpBoot(ErrorType.QUICKBOOT_ERROR, 1)
                if (!commandResult.isSuccess) {
                    handleErrorType(ErrorType.QUICKBOOT_ERROR, commandResult)
                    return
                }
            }
            if (!Device.currentDeviceCard.noModem) {
                commandResult = dumpModem(ErrorType.QUICKBOOT_ERROR)
                if (!commandResult.isSuccess) {
                    handleErrorType(ErrorType.QUICKBOOT_ERROR, commandResult)
                    return
                }
            }
            if (Device.currentDeviceCard.sensors && !checkSensors(ErrorType.QUICKBOOT_ERROR)) {
                commandResult = dumpSensors(ErrorType.QUICKBOOT_ERROR)
                if (!commandResult.isSuccess) {
                    handleErrorType(ErrorType.QUICKBOOT_ERROR, commandResult)
                    return
                }
            }
        }
        if (ShellUtils.fastCmd("find ${SdcardPath}/boot.img")
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
            if (wasMounted) commandResult = CommandHandler.mountWindows()
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
            if (wasMounted) commandResult = CommandHandler.umountWindows()
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
    try {
        packageManager.getLaunchIntentForPackage(packageName)?.let {
            startService(Intent.makeRestartActivityTask(it.component))
            Runtime.getRuntime().exit(0)
        }
    } catch (e: Exception) {
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