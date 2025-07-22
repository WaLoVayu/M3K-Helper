package com.remtrik.m3khelper.ui.component

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.remtrik.m3khelper.M3KApp
import com.remtrik.m3khelper.R
import com.remtrik.m3khelper.util.dumpBoot
import com.remtrik.m3khelper.util.mountStatus
import com.remtrik.m3khelper.util.mountWindows
import com.remtrik.m3khelper.util.quickBoot
import com.remtrik.m3khelper.util.umountWindows
import com.remtrik.m3khelper.util.CurrentDeviceCard
import com.remtrik.m3khelper.util.FontSize
import com.remtrik.m3khelper.util.LineHeight
import com.remtrik.m3khelper.util.PaddingValue
import com.remtrik.m3khelper.util.UEFICardsArray
import com.remtrik.m3khelper.util.UEFIList
import com.remtrik.m3khelper.util.sdp

@Composable
fun CommandButton(
    title: Int,
    subtitle: Int,
    question: Int,
    command: () -> Unit,
    icon: Int
) {
    val showDialog = remember { mutableStateOf(false) }
    val showSpinner = remember { mutableStateOf(false) }
    ElevatedCard(
        onClick = { showDialog.value = true },
        Modifier
            .height(105.sdp())
            .fillMaxWidth(),
    ) {
        when {
            showSpinner.value -> {
                StatusDialog(
                    icon = painterResource(id = icon),
                    title = R.string.please_wait,
                    showDialog = showSpinner.value,
                )
            }
        }
        when {
            showDialog.value -> {
                Dialog(
                    icon = painterResource(id = icon),
                    title = null,
                    description = M3KApp.getString(question),
                    showDialog = showDialog.value,
                    onDismiss = { showDialog.value = false },
                    onConfirm = ({
                        Thread {
                            showDialog.value = false
                            showSpinner.value = true
                            command()
                            showSpinner.value = false
                        }.start()
                    })
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(PaddingValue),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.sdp())
        ) {
            Icon(
                modifier = Modifier
                    .size(40.sdp()),
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    M3KApp.getString(title),
                    fontWeight = FontWeight.Bold,
                    fontSize = FontSize,
                    lineHeight = LineHeight,
                )
                Text(
                    M3KApp.getString(subtitle),
                    lineHeight = LineHeight,
                    fontSize = FontSize
                )
            }
        }
    }
}

@Composable
fun LinkButton(
    title: String,
    subtitle: String?,
    link: String,
    icon: Any,
    localUriHandler: UriHandler
) {
    ElevatedCard(
        onClick = { localUriHandler.openUri(link) },
        Modifier
            .height(105.sdp())
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(PaddingValue),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.sdp())
        ) {
            if (icon is ImageVector) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    Modifier.size(40.sdp()),
                    tint = MaterialTheme.colorScheme.primary
                )
            } else if (icon is Int) {
                Icon(
                    modifier = Modifier
                        .size(40.sdp()),
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = FontSize,
                    lineHeight = LineHeight,
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        lineHeight = LineHeight,
                        fontSize = FontSize
                    )
                }
            }
        }
    }
}

@Composable
fun BackupButton() {
    val showBackupDialog = remember { mutableStateOf(false) }
    val showBackupSpinner = remember { mutableStateOf(false) }
    ElevatedCard(
        onClick = { showBackupDialog.value = true },
        Modifier
            .height(105.sdp())
            .fillMaxWidth(),
    ) {
        when {
            showBackupSpinner.value -> {
                StatusDialog(
                    icon = painterResource(id = R.drawable.ic_backup),
                    title = R.string.please_wait,
                    showDialog = showBackupSpinner.value,
                )
            }
        }
        when {
            showBackupDialog.value -> {
                AlertDialog(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_backup),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.sdp())
                        )
                    },
                    title = {
                    },
                    text = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = M3KApp.getString(R.string.backup_boot_question),
                            textAlign = TextAlign.Center,
                            fontSize = FontSize,
                            lineHeight = LineHeight
                        )
                    },
                    onDismissRequest = ({ showBackupDialog.value = false; }),
                    dismissButton = {
                        Row(
                            Modifier.align(Alignment.CenterHorizontally),
                            horizontalArrangement = Arrangement.spacedBy(10.sdp())
                        ) {
                            AssistChip(
                                onClick = {
                                    Thread {
                                        showBackupDialog.value = false
                                        showBackupSpinner.value = true
                                        dumpBoot(2)
                                        showBackupSpinner.value = false
                                    }.start()
                                },
                                label = {
                                    Text(
                                        modifier = Modifier.padding(
                                            top = 2.sdp(),
                                            bottom = 2.sdp()
                                        ),
                                        text = M3KApp.getString(R.string.android),
                                        fontSize = FontSize
                                    )
                                }
                            )
                            when {
                                !CurrentDeviceCard.noMount -> {
                                    AssistChip(
                                        onClick = {
                                            Thread {
                                                showBackupDialog.value = false
                                                showBackupSpinner.value = true
                                                dumpBoot(1)
                                                showBackupSpinner.value = false
                                            }.start()
                                        },
                                        label = {
                                            Text(
                                                modifier = Modifier.padding(
                                                    top = 2.sdp(),
                                                    bottom = 2.sdp()
                                                ),
                                                text = M3KApp.getString(R.string.windows),
                                                fontSize = FontSize
                                            )
                                        }
                                    )
                                }
                            }
                            AssistChip(
                                onClick = ({ showBackupDialog.value = false; }),
                                label = {
                                    Text(
                                        modifier = Modifier.padding(
                                            top = 2.sdp(),
                                            bottom = 2.sdp()
                                        ),
                                        text = M3KApp.getString(R.string.no),
                                        fontSize = FontSize
                                    )
                                }
                            )
                        }
                    },
                    confirmButton = {
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(PaddingValue),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.sdp())
        ) {
            Icon(
                modifier = Modifier
                    .size(40.sdp()),
                painter = painterResource(id = R.drawable.ic_backup),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    M3KApp.getString(R.string.backup_boot_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = FontSize,
                    lineHeight = LineHeight,
                )
                Text(
                    M3KApp.getString(R.string.backup_boot_subtitle),
                    lineHeight = LineHeight,
                    fontSize = FontSize
                )
            }
        }
    }
}

@Composable
fun MountButton() {
    val showMountDialog = remember { mutableStateOf(false) }
    var mount by remember { mutableStateOf(mountStatus()) }
    ElevatedCard(
        onClick = { showMountDialog.value = true },
        Modifier
            .height(105.sdp())
            .fillMaxWidth(),
    ) {
        when {
            showMountDialog.value -> {
                if (mount) {
                    Dialog(
                        icon = painterResource(id = R.drawable.ic_folder_open),
                        title = null,
                        description = M3KApp.getString(R.string.mnt_question),
                        showDialog = showMountDialog.value,
                        onDismiss = { showMountDialog.value = false },
                        onConfirm = ({
                            Thread {
                                mountWindows()
                                showMountDialog.value = false
                                mount = mountStatus()
                            }.start()
                        })
                    )
                } else {
                    Dialog(
                        painterResource(id = R.drawable.ic_folder),
                        null,
                        M3KApp.getString(R.string.umnt_question),
                        showMountDialog.value,
                        onDismiss = ({ showMountDialog.value = false; }),
                        onConfirm = ({
                            Thread {
                                umountWindows()
                                showMountDialog.value = false
                                mount = mountStatus()
                            }.start()
                        })
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(PaddingValue),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.sdp())
        ) {
            Icon(
                modifier = Modifier
                    .size(40.sdp()),
                painter = painterResource(
                    id = if (mount) {
                        R.drawable.ic_folder_open
                    } else {
                        R.drawable.ic_folder
                    }
                ),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                val mounted: Int =
                    if (mount) {
                        R.string.mnt_title
                    } else {
                        R.string.umnt_title
                    }
                Text(
                    M3KApp.getString(mounted),
                    fontWeight = FontWeight.Bold,
                    lineHeight = LineHeight,
                    fontSize = FontSize
                )
                Text(
                    M3KApp.getString(R.string.mnt_subtitle),
                    lineHeight = LineHeight,
                    fontSize = FontSize
                )
            }
        }
    }
}

@Composable
fun QuickBootButton() {
    val showQuickBootDialog = remember { mutableStateOf(false) }
    val showQuickBootSpinner = remember { mutableStateOf(false) }
    ElevatedCard(
        onClick = { showQuickBootDialog.value = true },
        Modifier
            .height(105.sdp())
            .fillMaxWidth(),
        enabled = UEFIList.isNotEmpty()
    ) {
        when {
            showQuickBootSpinner.value -> {
                StatusDialog(
                    icon = painterResource(id = R.drawable.ic_windows),
                    title = R.string.please_wait,
                    showDialog = showQuickBootSpinner.value,
                )
            }
        }
        when {
            showQuickBootDialog.value -> {
                AlertDialog(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_windows),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.sdp())
                        )
                    },
                    title = {
                    },
                    text = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = M3KApp.getString(R.string.quickboot_question1),
                            textAlign = TextAlign.Center,
                            fontSize = FontSize
                        )
                    },
                    onDismissRequest = ({ showQuickBootDialog.value = false; }),
                    dismissButton = {
                        Row(
                            Modifier.align(Alignment.CenterHorizontally),
                            horizontalArrangement = Arrangement.spacedBy(10.sdp())
                        ) {
                            for (type: Int in UEFIList) {
                                AssistChip(
                                    onClick = {
                                        Thread {
                                            showQuickBootDialog.value = false
                                            showQuickBootSpinner.value = true
                                            quickBoot(
                                                UEFICardsArray[
                                                    when (type) {
                                                        120 -> 3
                                                        90 -> 2
                                                        60 -> 1
                                                        else -> 0
                                                    }
                                                ].uefiPath
                                            )
                                            showQuickBootSpinner.value = false
                                        }.start()
                                    },
                                    label = {
                                        Text(
                                            modifier = Modifier.padding(
                                                top = 2.sdp(),
                                                bottom = 2.sdp()
                                            ),
                                            text = M3KApp.getString(
                                                when (type) {
                                                    120 -> R.string.quickboot_question120
                                                    90 -> R.string.quickboot_question90
                                                    60 -> R.string.quickboot_question60
                                                    else -> R.string.yes
                                                }
                                            ),
                                            fontSize = FontSize
                                        )
                                    }
                                )
                            }
                            AssistChip(
                                onClick = ({ showQuickBootDialog.value = false; }),
                                label = {
                                    Text(
                                        modifier = Modifier.padding(
                                            top = 2.sdp(),
                                            bottom = 2.sdp()
                                        ),
                                        text = M3KApp.getString(R.string.no),
                                        fontSize = FontSize
                                    )
                                }
                            )
                        }
                    },
                    confirmButton = {
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(PaddingValue),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.sdp())
        ) {
            Icon(
                modifier = Modifier
                    .size(40.sdp()),
                painter = painterResource(id = R.drawable.ic_windows),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                val title: Int
                val subtitle: Int
                if (UEFIList.isNotEmpty()) {
                    title = R.string.quickboot_title
                    subtitle = when (CurrentDeviceCard.noModem) {
                        true -> R.string.quickboot_subtitle_nomodem
                        else -> R.string.quickboot_subtitle
                    }
                } else {
                    title = R.string.uefi_not_found_title
                    subtitle = R.string.uefi_not_found_subtitle
                }
                Text(
                    M3KApp.getString(title),
                    fontWeight = FontWeight.Bold,
                    lineHeight = LineHeight,
                    fontSize = FontSize
                )
                Text(
                    M3KApp.getString(subtitle),
                    lineHeight = LineHeight,
                    fontSize = FontSize
                )
            }
        }
    }
}

@Composable
fun SwitchItem(
    icon: ImageVector? = null,
    title: String?,
    summary: String? = null,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    ListItem(
        modifier = Modifier
            .toggleable(
                value = checked,
                interactionSource = interactionSource,
                role = Role.Switch,
                enabled = enabled,
                indication = LocalIndication.current,
                onValueChange = onCheckedChange
            ),
        headlineContent = {},
        leadingContent = icon?.let {
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PaddingValue),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.sdp())
                ) {
                    Icon(icon, title, Modifier.size(25.sdp()))
                    Column() {
                        if (title != null) {
                            Text(text = title, fontSize = FontSize, lineHeight = LineHeight)
                        }
                        if (summary != null) {
                            Text(text = summary, fontSize = FontSize, lineHeight = LineHeight)
                        }
                    }
                    Spacer(Modifier.fillMaxWidth())
                    Switch(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        checked = checked,
                        enabled = enabled,
                        onCheckedChange = onCheckedChange,
                        interactionSource = interactionSource
                    )
                }
            }
        },
        trailingContent = {},
        supportingContent = {}
    )
}