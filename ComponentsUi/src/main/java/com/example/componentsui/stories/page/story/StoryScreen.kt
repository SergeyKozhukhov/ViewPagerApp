package com.example.componentsui.stories.page.story

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.componentsui.stories.elements.ProgressBar
import com.example.componentsui.stories.page.StoryPage
import com.example.componentsui.stories.page.story.image.StoryImageContent
import com.example.componentsui.stories.page.story.video.StoryVideoContent

private const val TAG = "StoriesScreen"

// https://www.droidcon.com/2022/10/12/how-to-handle-viewmodel-one-time-events-in-jetpack-compose/
@Composable // TODO: rename to StoryPage
fun StoryScreen(
    id: Int,
    isActive: Boolean,
    story: StoryPage,
    onScreenEvent: (event: StoryScreenEvent, screen: Int) -> Unit,
    onBorderEvent: (event: StoryScreenBorderEvent) -> Unit,
    viewModel: StoryViewModel = viewModel(
        key = "StoryScreen: $id",
        factory = StoryViewModel.provide(screenCount = story.screens.size, screenDuration = 5)
    ),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    screenContent: @Composable (StoryPage.Custom) -> Unit
) {
    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(viewModel)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(viewModel)
        }
    }

    val screenState by viewModel.uiState

    if (screenState.event != StoryScreenEvent.IDLE) {
        onScreenEvent.invoke(screenState.event, screenState.currentScreenIndex)
        viewModel.onUpdateScreen()
    }

    if (screenState.borderEvent != StoryScreenBorderEvent.IDLE) {
        onBorderEvent.invoke(screenState.borderEvent)
        viewModel.resetPage()
    }

    CurrentScreen(
        progress = screenState.screenProgress,
        currentSlice = screenState.currentScreenIndex,
        story = story,
        onNextScreenTap = { viewModel.onNextTap() },
        onPreviousScreenTap = { viewModel.onPreviousTap() },
        onPauseLongPress = { viewModel.onPause() },
        onPrepared = { },
        screenContent = screenContent
    )

    LaunchedEffect(key1 = isActive, key2 = id, key3 = screenState.currentScreenIndex) {
        if (isActive) {
            viewModel.startProgress()
        }
    }
}

@Composable
fun CurrentScreen(
    progress: Float,
    currentSlice: Int,
    story: StoryPage,
    onNextScreenTap: () -> Unit,
    onPreviousScreenTap: () -> Unit,
    onPauseLongPress: () -> Unit,
    onPrepared: () -> Unit, // loaded
    screenContent: @Composable (StoryPage.Custom) -> Unit
) {
    Box(
        modifier = Modifier.storyScreenGestures(
            onPauseLongPress = onPauseLongPress,
            onNextScreenTap = onNextScreenTap,
            onPreviousScreenTap = onPreviousScreenTap
        )
    ) {
        ContentItem(
            screen = story.screens[currentSlice],
            screenContent = screenContent,
            onPrepared = onPrepared
        )
        ProgressBar(
            progress = progress,
            currentScreen = currentSlice,
            screenCount = story.screens.size
        )
    }
}

private fun Modifier.storyScreenGestures(
    onPauseLongPress: () -> Unit,
    onNextScreenTap: () -> Unit,
    onPreviousScreenTap: () -> Unit
) =
    this.pointerInput(Unit) {
        detectTapGestures(
            onDoubleTap = { offset ->
                Log.d(TAG, "AppScreen: onDoubleTap $offset")
            },
            onLongPress = { offset ->
                Log.d(TAG, "AppScreen: onLongPress $offset")
                onPauseLongPress.invoke()
            },
            onPress = { offset ->
                Log.d(TAG, "AppScreen: onPress $offset")
            },
            onTap = { offset ->
                Log.d(TAG, "AppScreen: onTap $offset")
                if (offset.x < 500) {
                    onPreviousScreenTap.invoke()
                } else {
                    onNextScreenTap.invoke()
                }
            })
    }

@Composable
private fun ContentItem(
    screen: StoryPage.Screen,
    screenContent: @Composable (StoryPage.Custom) -> Unit,
    onPrepared: () -> Unit, // loaded
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