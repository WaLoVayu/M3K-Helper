package com.remtrik.m3khelper.util

import com.remtrik.m3khelper.M3KApp
import com.remtrik.m3khelper.R

data class DeviceCard(
    val deviceCodename: Array<String>,
    val deviceName: String,
    val deviceImage: Int,
    val deviceGuide: String,
    val deviceGroup: String,
    val deviceDrivers: String,
    val deviceUEFI: String,
    val noModem: Boolean, val noFlash: Boolean,
    val noBoot: Boolean, val noMount: Boolean,
    val sensors: Boolean, val noGuide: Boolean,
    val noGroup: Boolean, val noDrivers: Boolean,
    val noUEFI: Boolean, val unifiedDriversUEFI: Boolean,
    val noLinks: Boolean = false
)

val vayuCard: DeviceCard = DeviceCard(
    arrayOf("vayu", "bhima"),
    "POCO X3 Pro",
    R.drawable.vayu,
    "https://github.com/woa-vayu/POCOX3Pro-Guides",
    "https://t.me/windowsonvayu",
    "https://github.com/woa-vayu/POCOX3Pro-Releases/releases/latest",
    "",
    noModem = true, noFlash = false,
    noBoot = false, noMount = false,
    sensors = false, noGuide = false,
    noGroup = false, noDrivers = false,
    noUEFI = false, unifiedDriversUEFI = true
)

val nabuCard: DeviceCard = DeviceCard(
    arrayOf("nabu"),
    "Xiaomi Pad 5",
    R.drawable.nabu,
    "https://github.com/erdilS/Port-Windows-11-Xiaomi-Pad-5",
    "https://t.me/nabuwoa",
    "https://github.com/erdilS/Port-Windows-11-Xiaomi-Pad-5/releases/tag/Drivers",
    "https://github.com/erdilS/Port-Windows-11-Xiaomi-Pad-5/releases/tag/UEFI",
    noModem = true, noFlash = false,
    noBoot = false, noMount = false,
    sensors = false, noGuide = false,
    noGroup = false, noDrivers = false,
    noUEFI = false, unifiedDriversUEFI = false
)

val raphaelCard: DeviceCard = DeviceCard(
    arrayOf("raphael"),
    "Xiaomi Mi 9T Pro",
    R.drawable.raphael,
    "https://github.com/graphiks/woa-raphael",
    "https://t.me/woaraphael",
    "https://github.com/woa-raphael/raphael-drivers/releases/latest",
    "https://github.com/woa-raphael/woa-raphael/releases/tag/raphael-uefi",
    noModem = false, noFlash = false,
    noBoot = false, noMount = false,
    sensors = true, noGuide = false,
    noGroup = false, noDrivers = false,
    noUEFI = false, unifiedDriversUEFI = false
)

val raphaelinCard: DeviceCard =
    raphaelCard.copy(deviceCodename = arrayOf("raphaelin"), deviceName = "Redmi K20 Pro")
val raphaelsCard: DeviceCard =
    raphaelCard.copy(deviceCodename = arrayOf("raphaels"), deviceName = "Redmi K20 Pro Premium")

val cepheusCard: DeviceCard = DeviceCard(
    arrayOf("cepheus"),
    "Xiaomi Mi 9",
    R.drawable.cepheus,
    "https://github.com/ivnvrvnn/Port-Windows-XiaoMI-9",
    "https://t.me/woacepheus",
    "https://github.com/qaz6750/XiaoMi9-Drivers/releases/latest",
    "",
    noModem = true, noFlash = false,
    noBoot = false, noMount = false,
    sensors = false, noGuide = false,
    noGroup = false, noDrivers = false,
    noUEFI = false, unifiedDriversUEFI = true
)

val berylliumCard: DeviceCard = DeviceCard(
    arrayOf("beryllium"),
    "POCO F1",
    R.drawable.beryllium,
    "https://github.com/n00b69/woa-beryllium",
    "https://t.me/WinOnF1",
    "https://github.com/n00b69/woa-beryllium/releases/tag/Drivers",
    "https://github.com/n00b69/woa-beryllium/releases/tag/UEFI",
    noModem = false, noFlash = false,
    noBoot = false, noMount = false,
    sensors = false, noGuide = false,
    noGroup = false, noDrivers = false,
    noUEFI = false, unifiedDriversUEFI = false
)

val miatollCard: DeviceCard = DeviceCard(
    arrayOf("miatoll", "durandal", "curtana_india", "joyeuse"),
    "Redmi Note 9 Pro",
    R.drawable.miatoll,
    "https://github.com/woa-miatoll/Port-Windows-11-Redmi-Note-9-Pro",
    "http://t.me/woamiatoll",
    "https://github.com/woa-miatoll/Miatoll-Releases/releases/latest",
    "",
    noModem = true, noFlash = false,
    noBoot = false, noMount = false,
    sensors = false, noGuide = false,
    noGroup = false, noDrivers = false,
    noUEFI = false, unifiedDriversUEFI = true
)
val curtanaCard: DeviceCard =
    miatollCard.copy(deviceCodename = arrayOf("curtana"), deviceName = "Redmi Note 9S")
val excaliburCard: DeviceCard =
    miatollCard.copy(deviceCodename = arrayOf("excalibur"), deviceName = "Redmi Note 9 Pro Max")
val gramCard: DeviceCard =
    miatollCard.copy(deviceCodename = arrayOf("gram"), deviceName = "POCO M2 Pro")

val alphaCard: DeviceCard = DeviceCard(
    arrayOf("alpha"),
    "LG G8",
    R.drawable.alpha,
    "https://github.com/n00b69/woa-alphaplus",
    "https://t.me/lgedevices",
    "https://github.com/n00b69/woa-alphaplus/releases/tag/Drivers",
    "https://github.com/n00b69/woa-alphaplus/releases/tag/UEFI",
    noModem = false, noFlash = false,
    noBoot = false, noMount = false,
    sensors = false, noGuide = false,
    noGroup = false, noDrivers = false,
    noUEFI = false, unifiedDriversUEFI = false
)

val mh2lm5gCard: DeviceCard = DeviceCard(
    arrayOf("mh2lm5g"),
    "LG V50S",
    R.drawable.mh2,
    "https://github.com/n00b69/woa-mh2lm5g",
    "https://t.me/lgedevices",
    "https://github.com/n00b69/woa-mh2lm5g/releases/tag/Drivers",
    "https://github.com/n00b69/woa-mh2lm5g/releases/tag/UEFI",
    noModem = false, noFlash = false,
    noBoot = false, noMount = false,
    sensors = false, noGuide = false,
    noGroup = false, noDrivers = false,
    noUEFI = false, unifiedDriversUEFI = false
)

val mh2Card: DeviceCard = mh2lm5gCard.copy(
    deviceCodename = arrayOf("mh2"),
    deviceName = "LG G8X",
    deviceGuide = "https://github.com/n00b69/woa-mh2lm"
)

val betaCard: DeviceCard = DeviceCard(
    arrayOf("beta"),
    "LG G8S",
    R.drawable.beta,
    "https://github.com/n00b69/woa-betalm",
    "https://t.me/lgedevices",
    "https://github.com/n00b69/woa-betalm/releases/tag/Drivers",
    "https://github.com/n00b69/woa-betalm/releases/tag/UEFI",
    noModem = true, noFlash = false,
    noBoot = false, noMount = false,
    sensors = false, noGuide = false,
    noGroup = false, noDrivers = false,
    noUEFI = false, unifiedDriversUEFI = false
)

val flashCard: DeviceCard = mh2lm5gCard.copy(
    deviceCodename = arrayOf("flash"),
    deviceName = "LG V50",
    deviceGuide = "https://github.com/n00b69/woa-flashlmdd",
    deviceImage = R.drawable.flashlm
)

val guacamoleCard: DeviceCard = DeviceCard(
    arrayOf("guacamole", "OnePlus7Pro"),
    "OnePlus 7 Pro",
    R.drawable.guacamole,
    "",
    "https://t.me/onepluswoachat",
    "",
    "",
    noModem = true, noFlash = true,
    noBoot = true, noMount = false,
    sensors = false, noGuide = true,
    noGroup = false, noDrivers = true,
    noUEFI = true, unifiedDriversUEFI = false
)

val hotdogCard: DeviceCard = guacamoleCard.copy(
    deviceCodename = arrayOf("hotdog", "OnePlus7TPro"),
    deviceName = "OnePlus 7T Pro",
    deviceImage = R.drawable.hotdog
)

val suryaCard: DeviceCard = DeviceCard(
    arrayOf("surya", "karna"),
    "POCO X3",
    R.drawable.vayu,
    "https://github.com/woa-surya/POCOX3NFC-Guides",
    "https://t.me/windows_on_pocox3_nfc",
    "",
    "",
    noModem = true, noFlash = true,
    noBoot = true, noMount = false,
    sensors = false, noGuide = true,
    noGroup = true, noDrivers = true,
    noUEFI = true, unifiedDriversUEFI = true,
    noLinks = true
)

val a52sxqCard: DeviceCard = DeviceCard(
    arrayOf("a52sxq"),
    "Samsung Galaxy A52s",
    R.drawable.a52sxq,
    "https://github.com/woa-a52s/Samsung-A52s-5G-Guides",
    "https://t.me/a52sxq_uefi",
    "https://github.com/woa-a52s/Samsung-A52s-5G-Releases/releases/latest",
    "",
    noModem = true, noFlash = false,
    noBoot = false, noMount = false,
    sensors = false, noGuide = false,
    noGroup = false, noDrivers = false,
    noUEFI = false, unifiedDriversUEFI = true
)

val beyond1Card: DeviceCard = DeviceCard(
    arrayOf("beyond1"),
    "Samsung Galaxy S10",
    R.drawable.beyond1,
    "",
    "https://t.me/woahelperchat",
    "",
    "",
    noModem = true, noFlash = false,
    noBoot = false, noMount = false,
    sensors = false, noGuide = true,
    noGroup = false, noDrivers = true,
    noUEFI = true, unifiedDriversUEFI = false
)

val emu64xaCard: DeviceCard = DeviceCard(
    arrayOf("emu64xa"),
    "emu64xa",
    R.drawable.vayu,
    "https://google.com",
    "https://google.com",
    "https://google.com",
    "https://google.com",
    noModem = false, noFlash = false,
    noBoot = false, noMount = false,
    sensors = false, noGuide = false,
    noGroup = false, noDrivers = false,
    noUEFI = false, unifiedDriversUEFI = true
)

val unknownCard: DeviceCard = DeviceCard(
    arrayOf("unknown"),
    M3KApp.getString(R.string.unknown_device),
    R.drawable.ic_device_unknown,
    "",
    "",
    "",
    "",
    noModem = true, noFlash = true,
    noBoot = true, noMount = false,
    sensors = false, noGuide = true,
    noGroup = true, noDrivers = true,
    noUEFI = true, unifiedDriversUEFI = false,
    noLinks = true
)
