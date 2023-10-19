package com.example.componentsui.story

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedProgressBar(
    id: Int,
    screenPosition: Int,
    screenCount: Int,
    screenDuration: Long,
    isRunning: Boolean,
    onFinish: () -> Unit
) {
    val percent = remember(key1 = id, key2 = screenPosition) { Animatable(0f) }
    LaunchedEffect(key1 = screenPosition, key2 = isRunning) {
        if (isRunning) {
            percent.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = (screenDuration * (1f - percent.value)).toInt(),
                    easing = LinearEasing
                )
            )
            onFinish.invoke()
        } else {
            percent.stop()
        }
    }

    ProgressBar(progress = percent.value, currentScreen = screenPosition, screenCount = screenCount)
}

@Composable
fun ProgressBar(
    progress: Float, // [0, 1]
    currentScreen: Int,
    screenCount: Int
) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.padding(4.dp))

        repeat(screenCount) { index ->
            if (currentScreen == index) {
                ProgressLine(progress = progress)
            } else {
                ProgressLine(progress = if (currentScreen < index) 0f else 1f)
            }

            Spacer(modifier = Modifier.padding(4.dp))
        }
    }
}

@Composable
private fun RowScope.ProgressLine(progress: Float) {
    LinearProgressIndicator(
        trackColor = Color.LightGray,
        color = Color.Gray,
        modifier = Modifier
            .weight(1f)
            .padding(top = 12.dp, bottom = 12.dp)
            .clip(RoundedCornerShape(12.dp)),
        progress = progress
    )
}