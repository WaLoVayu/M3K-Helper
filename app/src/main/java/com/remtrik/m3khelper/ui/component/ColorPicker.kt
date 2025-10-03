package com.remtrik.m3khelper.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.edit
import com.remtrik.m3khelper.prefs
import com.remtrik.m3khelper.ui.theme.themeReapply
import com.remtrik.m3khelper.util.variables.FontSize
import com.remtrik.m3khelper.util.variables.LineHeight
import com.remtrik.m3khelper.util.variables.PaddingValue
import com.remtrik.m3khelper.util.variables.sdp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPicker() {
    val red = rememberSaveable {
        mutableFloatStateOf(
            prefs.getFloat("theme_engine_color_R", 0f)
        )
    }
    val green = rememberSaveable {
        mutableFloatStateOf(
            prefs.getFloat("theme_engine_color_G", 0f)
        )
    }
    val blue = rememberSaveable {
        mutableFloatStateOf(
            prefs.getFloat("theme_engine_color_B", 0f)
        )
    }

    val color by remember {
        derivedStateOf {
            Color(red.floatValue, green.floatValue, blue.floatValue, 1f)
        }
    }

    Column {
        Row {
            ColorPreview(color)
        }

        Column(
            modifier = Modifier.padding(PaddingValue),
            verticalArrangement = Arrangement.spacedBy(10.sdp())
        ) {
            ColorSlider(
                "R",
                red,
                {
                    red.floatValue = it
                },
                Color.Red
            )
            ColorSlider(
                "G",
                green,
                {
                    green.floatValue = it
                },
                Color.Green
            )
            ColorSlider(
                "B",
                blue,
                {
                    blue.floatValue = it
                },
                Color.Blue
            )
        }
    }
}

@Composable
private fun ColorPreview(color: Color) {
    Box(
        modifier = Modifier
            .padding(PaddingValue)
            .fillMaxWidth()
            .height(80.sdp())
            .background(color, shape = MaterialTheme.shapes.large)
            .border(
                width = 5.sdp(),
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.large
            )
    )
}

@Composable
private fun ColorSlider(
    label: String,
    valueState: MutableState<Float>,
    onValueChange: (Float) -> Unit,
    color: Color,
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.sdp())
    ) {
        Text(
            text = label,
            fontSize = FontSize,
            lineHeight = LineHeight
        )
        Slider(
            value = valueState.value,
            onValueChange = onValueChange,
            onValueChangeFinished = {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        prefs.edit { putFloat("theme_engine_color_$label", valueState.value) }
                        themeReapply.value = !themeReapply.value
                    }
                }
            },
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = color.copy(alpha = 0.14f)
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valueState.value.toColorInt().toString(),
            modifier = Modifier.width(35.sdp()),
            fontSize = FontSize,
            lineHeight = LineHeight,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

fun Float.toColorInt(): Int = (this * 255 + 0.5f).toInt()
