package com.example.componentsui.stories.elements

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
    currentScreen: Int,
    screenCount: Int,
    isPaused: Boolean,
    onFinish: () -> Unit
) {
    val percent = remember { Animatable(0f) } // (1)
    LaunchedEffect(key1 = isPaused, block = {
        if (isPaused) percent.stop()
        else {
            percent.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = (5000 * (1f - percent.value)).toInt(), // (3)
                    easing = LinearEasing
                )
            )
            onFinish.invoke()
        }
    })

    ProgressBar(progress = percent.value, currentScreen = currentScreen, screenCount = screenCount)
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
            LinearProgressIndicator(
                trackColor = Color.LightGray,
                color = Color.Gray,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 12.dp, bottom = 12.dp)
                    .clip(RoundedCornerShape(12.dp)),
                progress = if (index == currentScreen) progress else 0f
            )

            Spacer(modifier = Modifier.padding(4.dp))
        }
    }
}