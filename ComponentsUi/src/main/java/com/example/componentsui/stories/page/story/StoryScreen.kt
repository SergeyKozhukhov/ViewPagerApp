package com.example.componentsui.stories.page.story

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.example.componentsui.stories.page.StoryPage
import com.example.componentsui.stories.page.story.image.StoryImageContent
import com.example.componentsui.stories.page.story.video.StoryVideoContent
import com.example.componentsui.stories.elements.LinearIndicator

private const val TAG = "StoriesScreen"

@Composable // TODO: rename to StoryPage
fun StoryScreen(
    positionPage: Int,
    isActive: Boolean,
    story: StoryPage,
    onNextScreenTap: (page: Int, screen: Int) -> Unit,
    onPreviousScreenTap: (page: Int, screen: Int) -> Unit,
    onNextScreenTime: (page: Int, screen: Int) -> Unit,
    onNextPageTap: () -> Unit,
    onPreviousPageTap: () -> Unit,
    onNextPageTime: () -> Unit,
    viewModel: StoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel<StoryViewModel>(
        key = positionPage.toString(),
        factory = StoryViewModel.provide(story.screens.size)
    ).also { LocalLifecycleOwner.current.lifecycle.addObserver(it) },
    screenContent: @Composable (StoryPage.Custom) -> Unit
) {
    val screenState by viewModel.screenState

    if (screenState.isNextScreenTap) {
        viewModel.resetScreen()
        onNextScreenTap.invoke(positionPage, screenState.screenIndex)
    }

    if (screenState.isPreviousScreenTap) {
        viewModel.resetScreen()
        onPreviousScreenTap.invoke(positionPage, screenState.screenIndex)
    }

    if (screenState.isNextScreenTime) {
        viewModel.resetScreen()
        onNextScreenTime.invoke(positionPage, screenState.screenIndex)
    }

    if (screenState.isNextPageTime) {
        viewModel.resetPage()
        onNextPageTime.invoke()
    }

    if (screenState.isNextPageTap) {
        viewModel.resetPage()
        onNextPageTap.invoke()
    }

    if (screenState.isPreviousPageTap) {
        viewModel.resetPage()
        onPreviousPageTap.invoke()
    }

    CurrentScreen(
        progress = screenState.progress,
        currentSlice = screenState.screenIndex,
        story = story,
        onNextScreenTap = { viewModel.onNextTap() },
        onPreviousScreenTap = { viewModel.onPreviousTap() },
        onPauseLongPress = { viewModel.onPause() },
        screenContent = screenContent
    )

    LaunchedEffect(key1 = isActive, key2 = positionPage, key3 = screenState.screenIndex) {
        if (isActive) {
            viewModel.startTimer()
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
    screenContent: @Composable (StoryPage.Custom) -> Unit
) {
    Box(
        modifier = Modifier.pointerInput(Unit) {
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
        },
    ) {
        when (val screen = story.screens[currentSlice]) {
            is StoryPage.Image -> {
                StoryImageContent(
                    image = screen.image,
                    title = screen.title,
                )
            }

            is StoryPage.Video -> {
                StoryVideoContent(video = screen.video, title = screen.title)
            }

            is StoryPage.Custom -> {
                screenContent.invoke(screen)
            }
        }

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.padding(4.dp))

            ProgressSlices(
                progress = progress,
                currentPage = currentSlice,
                numberOfPage = story.screens.size,
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

@Composable
fun RowScope.ProgressSlices(
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