package com.remtrik.m3khelper.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.remtrik.m3khelper.util.PaddingValue
import com.remtrik.m3khelper.util.prefs
import com.remtrik.m3khelper.util.sdp
import androidx.core.content.edit
import com.remtrik.m3khelper.util.FontSize
import com.remtrik.m3khelper.util.LineHeight
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPicker() {
    val red = rememberSaveable {
        mutableFloatStateOf(
            prefs.getFloat("themeengine_red", 0f)
        )
    }
    val green = rememberSaveable {
        mutableFloatStateOf(
            prefs.getFloat("themeengine_green", 0f)
        )
    }
    val blue = rememberSaveable {
        mutableFloatStateOf(
            prefs.getFloat("themeengine_blue", 0f)
        )
    }

    val color by remember {
        derivedStateOf {
            Color(red.floatValue, green.floatValue, blue.floatValue, 1f)
        }
    }

    val coroutineScope = rememberCoroutineScope()

    Column {
        Row {
            Box(
                modifier = Modifier
                    .padding(PaddingValue)
                    .fillMaxWidth()
                    .height(80.sdp())
                    .background(color, shape = MaterialTheme.shapes.large)
            )
        }

        Column(
            modifier = Modifier.padding(PaddingValue),
            verticalArrangement = Arrangement.spacedBy(10.sdp())
        ) {
            ColorSlider(
                "R",
                red,
                { newValue ->
                    red.floatValue = newValue
                    coroutineScope.launch {
                        prefs.edit { putFloat("themeengine_red", newValue) }
                    }
                },
                Color.Red
            )
            ColorSlider(
                "G",
                green,
                { newValue ->
                    green.floatValue = newValue
                    coroutineScope.launch {
                        prefs.edit { putFloat("themeengine_green", newValue) }
                    }
                },
                Color.Green
            )
            ColorSlider(
                "B",
                blue,
                { newValue ->
                    blue.floatValue = newValue
                    coroutineScope.launch {
                        prefs.edit { putFloat("themeengine_blue", newValue) }
                    }
                },
                Color.Blue
            )
        }
    }
}

@Composable
fun ColorSlider(
    label: String,
    valueState: MutableState<Float>,
    onValueChange: (Float) -> Unit,
    color: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.sdp())
    ) {
        Text(text = label, fontSize = FontSize, lineHeight = LineHeight)
        Slider(
            value = valueState.value,
            onValueChange = onValueChange,
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

    Thread {
        when (label) {
            "R" -> prefs.edit { putFloat("themeengine_red", valueState.value) }
            "G" -> prefs.edit { putFloat("themeengine_green", valueState.value) }
            "B" -> prefs.edit { putFloat("themeengine_blue", valueState.value) }
        }
    }.start()
}

fun Float.toColorInt(): Int = (this * 255 + 0.5f).toInt()