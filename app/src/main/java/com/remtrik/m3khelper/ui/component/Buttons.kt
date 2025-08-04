package com.remtrik.m3khelper.ui.component

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.remtrik.m3khelper.R.drawable.ic_backup
import com.remtrik.m3khelper.R.drawable.ic_folder
import com.remtrik.m3khelper.R.drawable.ic_folder_open
import com.remtrik.m3khelper.R.drawable.ic_windows
import com.remtrik.m3khelper.R.string
import com.remtrik.m3khelper.util.Device
import com.remtrik.m3khelper.util.FontSize
import com.remtrik.m3khelper.util.LineHeight
import com.remtrik.m3khelper.util.PaddingValue
import com.remtrik.m3khelper.util.UEFICard
import com.remtrik.m3khelper.util.dumpBoot
import com.remtrik.m3khelper.util.isMounted
import com.remtrik.m3khelper.util.mountWindows
import com.remtrik.m3khelper.util.quickBoot
import com.remtrik.m3khelper.util.sdp
import com.remtrik.m3khelper.util.umountWindows
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    val scope = rememberCoroutineScope()

    ElevatedCard(
        onClick = { showDialog.value = true },
        modifier = Modifier
            .height(105.sdp())
            .fillMaxWidth(),
    ) {
        when {
            showSpinner.value -> {
                StatusDialog(
                    icon = painterResource(id = icon),
                    title = string.please_wait,
                    showDialog = showSpinner.value,
                )
            }
        }
        when {
            showDialog.value -> {
                Dialog(
                    icon = painterResource(id = icon),
                    title = null,
                    description = stringResource(question),
                    showDialog = showDialog.value,
                    onDismiss = { showDialog.value = false },
                    onConfirm = {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                showDialog.value = false
                                showSpinner.value = true
                                command()
                                showSpinner.value = false
                            }
                        }
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
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    stringResource(title),
                    fontWeight = FontWeight.Bold,
                    fontSize = FontSize,
                    lineHeight = LineHeight,
                )
                Text(
                    stringResource(subtitle),
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
    icon: Any?,
    localUriHandler: UriHandler
) {
    ElevatedCard(
        onClick = { localUriHandler.openUri(link) },
        modifier = Modifier
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
            if (icon != null) {
                if (icon is ImageVector) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(40.sdp()),
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
    val scope = rememberCoroutineScope()

    ElevatedCard(
        onClick = { showBackupDialog.value = true },
        modifier = Modifier
            .height(105.sdp())
            .fillMaxWidth(),
    ) {
        when {
            showBackupSpinner.value -> {
                StatusDialog(
                    icon = painterResource(id = ic_backup),
                    title = string.please_wait,
                    showDialog = showBackupSpinner.value,
                )
            }
        }
        when {
            showBackupDialog.value -> {
                AlertDialog(
                    icon = {
                        Icon(
                            painter = painterResource(id = ic_backup),
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
                            text = stringResource(string.backup_boot_question),
                            textAlign = TextAlign.Center,
                            fontSize = FontSize,
                            lineHeight = LineHeight
                        )
                    },
                    onDismissRequest = { showBackupDialog.value = false; },
                    dismissButton = {
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            horizontalArrangement = Arrangement.spacedBy(10.sdp())
                        ) {
                            AssistChip(
                                onClick = {
                                    scope.launch {
                                        withContext(Dispatchers.IO) {
                                            showBackupDialog.value = false
                                            showBackupSpinner.value = true
                                            dumpBoot(2)
                                            showBackupSpinner.value = false
                                        }
                                    }
                                },
                                label = {
                                    Text(
                                        modifier = Modifier.padding(
                                            top = 2.sdp(),
                                            bottom = 2.sdp()
                                        ),
                                        text = stringResource(string.android),
                                        fontSize = FontSize
                                    )
                                }
                            )
                            when {
                                !Device.currentDeviceCard.noMount -> {
                                    AssistChip(
                                        onClick = {
                                            scope.launch {
                                                withContext(Dispatchers.IO) {
                                                    showBackupDialog.value = false
                                                    showBackupSpinner.value = true
                                                    dumpBoot(1)
                                                    showBackupSpinner.value = false
                                                }
                                            }
                                        },
                                        label = {
                                            Text(
                                                modifier = Modifier.padding(
                                                    top = 2.sdp(),
                                                    bottom = 2.sdp()
                                                ),
                                                text = stringResource(string.windows),
                                                fontSize = FontSize
                                            )
                                        }
                                    )
                                }
                            }
                            AssistChip(
                                onClick = { showBackupDialog.value = false; },
                                label = {
                                    Text(
                                        modifier = Modifier.padding(
                                            top = 2.sdp(),
                                            bottom = 2.sdp()
                                        ),
                                        text = stringResource(string.no),
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
                painter = painterResource(id = ic_backup),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    stringResource(string.backup_boot_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = FontSize,
                    lineHeight = LineHeight,
                )
                Text(
                    stringResource(string.backup_boot_subtitle),
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
    var mount by remember { mutableStateOf(isMounted()) }

    val scope = rememberCoroutineScope()

    ElevatedCard(
        onClick = { showMountDialog.value = true },
        modifier = Modifier
            .height(105.sdp())
            .fillMaxWidth(),
    ) {
        when {
            showMountDialog.value -> {
                if (mount) {
                    Dialog(
                        icon = painterResource(id = ic_folder_open),
                        title = null,
                        description = stringResource(string.mnt_question),
                        showDialog = showMountDialog.value,
                        onDismiss = { showMountDialog.value = false },
                        onConfirm = {
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    mountWindows()
                                    showMountDialog.value = false
                                    mount = isMounted()
                                }
                            }
                        }
                    )
                } else {
                    Dialog(
                        painterResource(id = ic_folder),
                        null,
                        stringResource(string.umnt_question),
                        showMountDialog.value,
                        onDismiss = { showMountDialog.value = false; },
                        onConfirm = {
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    umountWindows()
                                    showMountDialog.value = false
                                    mount = isMounted()
                                }
                            }
                        }
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
                        ic_folder_open
                    } else {
                        ic_folder
                    }
                ),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                val mounted: Int =
                    if (mount) {
                        string.mnt_title
                    } else {
                        string.umnt_title
                    }
                Text(
                    stringResource(mounted),
                    fontWeight = FontWeight.Bold,
                    lineHeight = LineHeight,
                    fontSize = FontSize
                )
                Text(
                    stringResource(string.mnt_subtitle),
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
    val scope = rememberCoroutineScope()

    ElevatedCard(
        onClick = { showQuickBootDialog.value = true },
        modifier = Modifier
            .height(105.sdp())
            .fillMaxWidth(),
        enabled = Device.uefiCardsArray.isNotEmpty()
    ) {
        when {
            showQuickBootSpinner.value -> {
                StatusDialog(
                    icon = painterResource(id = ic_windows),
                    title = string.please_wait,
                    showDialog = showQuickBootSpinner.value,
                )
            }
        }
        when {
            showQuickBootDialog.value -> {
                AlertDialog(
                    icon = {
                        Icon(
                            painter = painterResource(id = ic_windows),
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
                            text = stringResource(string.quickboot_question1),
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
                            for (type: UEFICard in Device.uefiCardsArray) {
                                println("${type.uefiType} ${type.uefiPath}")
                                AssistChip(
                                    onClick = {
                                        scope.launch {
                                            withContext(Dispatchers.IO) {
                                                showQuickBootDialog.value = false
                                                showQuickBootSpinner.value = true
                                                quickBoot(
                                                    Device.uefiCardsArray[
                                                        when (type.uefiType) {
                                                            120 -> 3
                                                            90 -> 2
                                                            60 -> 1
                                                            else -> 0
                                                        }
                                                    ].uefiPath
                                                )
                                                showQuickBootSpinner.value = false
                                            }
                                        }
                                    },
                                    label = {
                                        Text(
                                            modifier = Modifier.padding(
                                                top = 2.sdp(),
                                                bottom = 2.sdp()
                                            ),
                                            text = stringResource(
                                                when (type.uefiType) {
                                                    120 -> string.quickboot_question120
                                                    90 -> string.quickboot_question90
                                                    60 -> string.quickboot_question60
                                                    else -> string.yes
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
                                        text = stringResource(string.no),
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
                painter = painterResource(id = ic_windows),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                val title: Int
                val subtitle: Int
                if (Device.uefiCardsArray.isNotEmpty()) {
                    title = string.quickboot_title
                    subtitle = when (Device.currentDeviceCard.noModem) {
                        true -> string.quickboot_subtitle_nomodem
                        else -> string.quickboot_subtitle
                    }
                } else {
                    title = string.uefi_not_found_title
                    subtitle = string.uefi_not_found_subtitle
                }
                Text(
                    stringResource(title),
                    fontWeight = FontWeight.Bold,
                    lineHeight = LineHeight,
                    fontSize = FontSize
                )
                Text(
                    stringResource(subtitle),
                    lineHeight = LineHeight,
                    fontSize = FontSize
                )
            }
        }
    }
}

@Composable
fun SwitchItem(
    icon: Any,
    title: String?,
    summary: String? = null,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .toggleable(
                value = checked,
                interactionSource = interactionSource,
                role = Role.Switch,
                enabled = enabled,
                indication = LocalIndication.current,
                onValueChange = onCheckedChange
            ),
    ) {
        Row(
            modifier = Modifier
                .padding(PaddingValue)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.sdp())
        ) {
            Column(Modifier.padding(end = 10.sdp())) {
                if (icon is ImageVector) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(25.sdp())
                            .align(Alignment.CenterHorizontally),
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else if (icon is Int) {
                    Icon(
                        modifier = Modifier
                            .size(25.sdp())
                            .align(Alignment.CenterHorizontally),
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (title != null) {
                    Text(text = title, fontSize = FontSize, lineHeight = LineHeight)
                }
                if (summary != null) {
                    Text(text = summary, fontSize = FontSize, lineHeight = LineHeight)
                }
            }
            Column {
                Switch(
                    checked = checked,
                    enabled = enabled,
                    onCheckedChange = onCheckedChange,
                    interactionSource = interactionSource
                )
            }
        }
    }
}

@Composable
fun ButtonItem(
    icon: Any,
    title: String?,
    summary: String? = null,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(PaddingValue)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.sdp())
        ) {
            Column(Modifier.padding(end = 10.sdp())) {
                if (icon is ImageVector) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(25.sdp())
                            .align(Alignment.CenterHorizontally),
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else if (icon is Int) {
                    Icon(
                        modifier = Modifier
                            .size(25.sdp())
                            .align(Alignment.CenterHorizontally),
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (title != null) {
                    Text(text = title, fontSize = FontSize, lineHeight = LineHeight)
                }
                if (summary != null) {
                    Text(text = summary, fontSize = FontSize, lineHeight = LineHeight)
                }
            }
        }
    }
}