package com.example.componentsui.stories.page.story

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.componentsui.stories.elements.LinearIndicator
import com.example.componentsui.stories.page.StoryPage
import com.example.componentsui.stories.page.story.image.StoryImageContent
import com.example.componentsui.stories.page.story.video.StoryVideoContent

private const val TAG = "StoriesScreen"

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
            currentPage = currentSlice,
            numberOfPage = story.screens.size
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

@Composable
private fun ProgressBar(
    progress: Float, // [0, 1]
    currentPage: Int,
    numberOfPage: Int
) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.padding(4.dp))

        ProgressSlices(
            progress = progress,
            currentPage = currentPage,
            numberOfPage = numberOfPage,
            modifier = Modifier
                .padding(top = 12.dp, bottom = 12.dp)
                .clip(RoundedCornerShape(12.dp))
        )
    }
}

@Composable
private fun RowScope.ProgressSlices(
    progress: Float,
    currentPage: Int,
    numberOfPage: Int,
    modifier: Modifier,
) {
    repeat(numberOfPage) { index ->
        LinearIndicator(
            progress = progress,
            modifier = modifier.weight(1f),
            isActive = index == currentPage
        )

        Spacer(modifier = Modifier.padding(4.dp))
    }
}