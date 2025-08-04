package com.remtrik.m3khelper.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.dropUnlessResumed
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import com.remtrik.m3khelper.util.FontSize
import com.remtrik.m3khelper.util.LineHeight
import com.remtrik.m3khelper.util.sdp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    navigator: DestinationsNavigator,
    text: Int,
    isNavigate: Boolean,
    isPopBack: Boolean,
    destination: DirectionDestinationSpec? = null,
    icon: ImageVector? = null
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(text),
                fontSize = FontSize,
                lineHeight = LineHeight,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (isPopBack) {
                IconButton(
                    onClick = dropUnlessResumed { navigator.popBackStack() },
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(20.sdp())
                    )
                }
            }
        },
        actions = {
            if (isNavigate) {
                IconButton(
                    onClick = { navigator.navigate(destination!!) }
                ) {
                    Icon(
                        imageVector = icon!!,
                        contentDescription = null,
                        modifier = Modifier.size(25.sdp())
                    )
                }
            }
        },
    )
}