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
import com.remtrik.m3khelper.util.funcs.ErrorType
import com.remtrik.m3khelper.util.variables.CommandHandler
import com.remtrik.m3khelper.util.variables.Device
import com.remtrik.m3khelper.util.variables.FontSize
import com.remtrik.m3khelper.util.variables.LineHeight
import com.remtrik.m3khelper.util.variables.PaddingValue
import com.remtrik.m3khelper.util.variables.commandError
import com.remtrik.m3khelper.util.variables.commandResult
import com.remtrik.m3khelper.util.variables.sdp
import com.remtrik.m3khelper.util.variables.showBootBackupErrorDialog
import com.remtrik.m3khelper.util.variables.showMountErrorDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// --Commented out by Inspection START (10/3/2025 9:21 PM):
//@Composable
//fun CommandButton(
//    title: Int,
//    subtitle: Int,
//    question: Int,
//    command: () -> Unit,
//    icon: Int
//) {
//    val showDialog = remember { mutableStateOf(false) }
//    val showSpinner = remember { mutableStateOf(false) }
//
//    val scope = rememberCoroutineScope()
//
//    ElevatedCard(
//        onClick = { showDialog.value = true },
//        modifier = Modifier
//            .height(105.sdp())
//            .fillMaxWidth(),
//    ) {
//        when {
//            showSpinner.value -> {
//                StatusDialog(
//                    icon = painterResource(id = icon),
//                    title = string.please_wait,
//                    showDialog = showSpinner.value,
//                )
//            }
//        }
//        when {
//            showDialog.value -> {
//                Dialog(
//                    icon = painterResource(id = icon),
//                    title = null,
//                    description = stringResource(question),
//                    showDialog = showDialog.value,
//                    onDismiss = { showDialog.value = false },
//                    onConfirm = {
//                        scope.launch {
//                            withContext(Dispatchers.IO) {
//                                showDialog.value = false
//                                showSpinner.value = true
//                                command()
//                                showSpinner.value = false
//                            }
//                        }
//                    }
//                )
//            }
//        }
//        Row(
//            modifier = Modifier
//                .fillMaxHeight()
//                .padding(PaddingValue),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(5.sdp())
//        ) {
//            Icon(
//                modifier = Modifier
//                    .size(40.sdp()),
//                painter = painterResource(id = icon),
//                contentDescription = null,
//                tint = MaterialTheme.colorScheme.primary
//            )
//            Column {
//                Text(
//                    stringResource(title),
//                    fontWeight = FontWeight.Bold,
//                    fontSize = FontSize,
//                    lineHeight = LineHeight,
//                )
//                Text(
//                    stringResource(subtitle),
//                    lineHeight = LineHeight,
//                    fontSize = FontSize
//                )
//            }
//        }
//    }
//}
// --Commented out by Inspection STOP (10/3/2025 9:21 PM)

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
            icon?.let {
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
                    icon = painterResource(id = ic_backup),
                    title = string.please_wait,
                    showDialog = showSpinner.value,
                )
            }
        }
        when {
            showDialog.value -> {
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
                    onDismissRequest = { showDialog.value = false; },
                    dismissButton = {
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            horizontalArrangement = Arrangement.spacedBy(10.sdp())
                        ) {
                            AssistChip(
                                onClick = {
                                    scope.launch {
                                        withContext(Dispatchers.IO) {
                                            showDialog.value = false
                                            showSpinner.value = true
                                            commandResult =
                                                CommandHandler.dumpBoot(
                                                    ErrorType.QUICKBOOT_ERROR,
                                                    2
                                                )
                                            if (!commandResult.isSuccess) {
                                                commandError.value = commandResult.output[0]
                                                showBootBackupErrorDialog.value = true
                                            }
                                            showSpinner.value = false
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
                                                    showDialog.value = false
                                                    showSpinner.value = true
                                                    commandResult =
                                                        CommandHandler.dumpBoot(
                                                            ErrorType.BOOTBACKUP_ERROR,
                                                            1
                                                        )
                                                    if (!commandResult.isSuccess) {
                                                        commandError.value = commandResult.output[0]
                                                        showBootBackupErrorDialog.value = true
                                                    }
                                                    showSpinner.value = false
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
                                onClick = { showDialog.value = false; },
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
    val showDialog = remember { mutableStateOf(false) }
    var isMounted by remember(CommandHandler.isMounted()) { mutableStateOf(CommandHandler.isMounted()) }

    val scope = rememberCoroutineScope()

    ElevatedCard(
        onClick = { showDialog.value = true },
        modifier = Modifier
            .height(105.sdp())
            .fillMaxWidth(),
    ) {
        when {
            showDialog.value -> {
                if (isMounted) {
                    Dialog(
                        painterResource(id = ic_folder),
                        null,
                        stringResource(string.umnt_question),
                        showDialog.value,
                        onDismiss = { showDialog.value = false; },
                        onConfirm = {
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    commandResult = CommandHandler.umountWindows()
                                    if (!commandResult.isSuccess) {
                                        commandError.value = commandResult.output[0]
                                        showMountErrorDialog.value = true
                                    }
                                    showDialog.value = false
                                    isMounted = CommandHandler.isMounted()
                                }
                            }
                        }
                    )
                } else {
                    Dialog(
                        icon = painterResource(id = ic_folder_open),
                        title = null,
                        description = stringResource(string.mnt_question),
                        showDialog = showDialog.value,
                        onDismiss = { showDialog.value = false },
                        onConfirm = {
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    commandResult = CommandHandler.mountWindows()
                                    if (!commandResult.isSuccess) {
                                        commandError.value = commandResult.output[0]
                                        showMountErrorDialog.value = true
                                    }
                                    showDialog.value = false
                                    isMounted = CommandHandler.isMounted()
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
                    id = if (isMounted) {
                        ic_folder
                    } else {
                        ic_folder_open
                    }
                ),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                val mounted: Int =
                    if (isMounted) {
                        string.umnt_title
                    } else {
                        string.mnt_title
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
    val showDialog = remember { mutableStateOf(false) }
    val showSpinner = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val hasUefi = Device.uefiCardsArray.isNotEmpty()

    ElevatedCard(
        onClick = { showDialog.value = true },
        modifier = Modifier
            .height(105.sdp())
            .fillMaxWidth(),
        enabled = hasUefi
    ) {
        when {
            showSpinner.value -> {
                StatusDialog(
                    icon = painterResource(id = ic_windows),
                    title = string.please_wait,
                    showDialog = showSpinner.value,
                )
            }
        }
        when {
            showDialog.value -> {
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
                    onDismissRequest = ({ showDialog.value = false; }),
                    dismissButton = {
                        Row(
                            Modifier.align(Alignment.CenterHorizontally),
                            horizontalArrangement = Arrangement.spacedBy(10.sdp())
                        ) {
                            Device.uefiCardsArray.forEach {
                                AssistChip(
                                    onClick = {
                                        scope.launch {
                                            withContext(Dispatchers.IO) {
                                                showDialog.value = false
                                                showSpinner.value = true
                                                CommandHandler.quickBoot(
                                                    it.uefiPath
                                                )
                                                showSpinner.value = false
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
                                                when (it.uefiType) {
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
                                onClick = ({ showDialog.value = false; }),
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
                if (hasUefi) {
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
                title?.let {
                    Text(text = title, fontSize = FontSize, lineHeight = LineHeight)
                }
                summary?.let {
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
                title?.let {
                    Text(text = title, fontSize = FontSize, lineHeight = LineHeight)
                }
                summary?.let {
                    Text(text = summary, fontSize = FontSize, lineHeight = LineHeight)
                }
            }
        }
    }
}