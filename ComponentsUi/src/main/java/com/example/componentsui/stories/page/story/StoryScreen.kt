package com.example.componentsui.stories.page.story

import androidx.compose.runtime.Composable
import com.example.componentsui.stories.page.StoryPage
import com.example.componentsui.stories.page.story.image.StoryImageContent
import com.example.componentsui.stories.page.story.video.StoryVideoContent
import com.example.componentsui.story.StoryScreenBorderEvent
import com.example.componentsui.story.StoryScreenEvent
import com.example.componentsui.story.StoryViewer

// https://www.droidcon.com/2022/10/12/how-to-handle-viewmodel-one-time-events-in-jetpack-compose/
@Composable // TODO: rename to StoryPage
fun StoryScreen(
    settledId: Int,
    isActive: Boolean,
    story: StoryPage,
    screenDuration: Long,
    onScreenEvent: (event: StoryScreenEvent, screen: Int) -> Unit,
    onBorderEvent: (event: StoryScreenBorderEvent) -> Unit,
    screenContent: @Composable (StoryPage.Custom) -> Unit
) {
    StoryViewer(
        settledId = settledId,
        isActive = isActive,
        screenCount = story.screens.size,
        screenDuration = screenDuration,
        onScreenEvent = onScreenEvent,
        onBorderEvent = onBorderEvent,
        screenContent = { position ->
            ContentItem(
                screen = story.screens[position],
                onPrepared = {},
                screenContent = { customScreen -> screenContent.invoke(customScreen) }
            )
        }
    )
}

@Composable
private fun ContentItem(
    screen: StoryPage.Screen,
    onPrepared: () -> Unit, // loaded
    screenContent: @Composable (StoryPage.Custom) -> Unit,
) {
    when (screen) {
        is StoryPage.Image -> {
            StoryImageContent(
                image = screen.image,
                title = screen.title,
                onPrepared = onPrepared
            )
        }

        is StoryPage.Video -> {
            StoryVideoContent(video = screen.video, title = screen.title)
        }

        is StoryPage.Custom -> {
            screenContent.invoke(screen)
        }
    }
}