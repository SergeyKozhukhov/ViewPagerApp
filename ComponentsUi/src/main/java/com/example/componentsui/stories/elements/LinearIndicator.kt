package com.example.componentsui.stories.elements

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LinearIndicator(
    progress: Float,
    modifier: Modifier,
    isActive: Boolean
) {

    LinearProgressIndicator(
        trackColor = Color.LightGray,
        color = Color.Gray,
        modifier = modifier
            .padding(top = 12.dp, bottom = 12.dp)
            .clip(RoundedCornerShape(12.dp)),
        progress = if (isActive) progress else 0f
        // progress = if (isActive) 1f else 0f
    )
}