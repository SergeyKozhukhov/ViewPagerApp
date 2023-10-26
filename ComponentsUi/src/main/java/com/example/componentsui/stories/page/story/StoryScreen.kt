package com.example.componentsui.stories.page.story

import androidx.compose.runtime.Composable
import com.example.componentsui.story.StoryScreenBorderEvent
import com.example.componentsui.story.StoryScreenEvent
import com.example.componentsui.story.StoryViewer

// https://www.droidcon.com/2022/10/12/how-to-handle-viewmodel-one-time-events-in-jetpack-compose/
@Composable // TODO: rename to StoryPage
fun <T> StoryScreen(
    settledId: Int,
    isActive: Boolean,
    screens: List<T>,
    screenDuration: Long,
    onScreenEvent: (event: StoryScreenEvent, screen: Int) -> Unit,
    onBorderEvent: (event: StoryScreenBorderEvent) -> Unit,
    screenContent: @Composable (T, onScreenReady: () -> Unit) -> Unit
) {
    StoryViewer(
        settledId = settledId,
        isActive = isActive,
        screenCount = screens.size,
        screenDuration = screenDuration,
        onScreenEvent = onScreenEvent,
        onBorderEvent = onBorderEvent,
        screenContent = { position, onScreenReady ->
            screenContent.invoke(screens[position], onScreenReady)
        }
    )
}