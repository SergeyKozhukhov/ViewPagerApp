package com.example.componentsui.story

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

private const val TAG = "StoriesScreen"

// https://www.droidcon.com/2022/10/12/how-to-handle-viewmodel-one-time-events-in-jetpack-compose/
@Composable
fun StoryViewer(
    id: Int,
    isActive: Boolean,
    screenCount: Int,
    onScreenEvent: (event: StoryScreenEvent, screen: Int) -> Unit,
    onBorderEvent: (event: StoryScreenBorderEvent) -> Unit,
    viewModel: StoryViewModel = viewModel(
        key = "StoryScreen: $id",
        factory = StoryViewModel.provide(screenCount = screenCount, screenDuration = 5)
    ),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    screenContent: @Composable (screen: Int) -> Unit,
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
        screenCount = screenCount,
        onNextScreenTap = { viewModel.onNextTap() },
        onPreviousScreenTap = { viewModel.onPreviousTap() },
        onPauseLongPress = { viewModel.onPause() },
        screenContent = screenContent
    )

    LaunchedEffect(key1 = isActive, key2 = id, key3 = screenState.currentScreenIndex) {
        if (isActive) {
            viewModel.startProgress()
        } else {
            viewModel.onPause()
        }
    }
}

@Composable
fun CurrentScreen(
    progress: Float,
    currentSlice: Int,
    screenCount: Int,
    onNextScreenTap: () -> Unit,
    onPreviousScreenTap: () -> Unit,
    onPauseLongPress: () -> Unit,
    screenContent: @Composable (screen: Int) -> Unit,
) {
    Box(
        modifier = Modifier.storyScreenGestures(
            onPauseLongPress = onPauseLongPress,
            onNextScreenTap = onNextScreenTap,
            onPreviousScreenTap = onPreviousScreenTap
        )
    ) {
        screenContent.invoke(currentSlice)

        ProgressBar(
            progress = progress,
            currentScreen = currentSlice,
            screenCount = screenCount
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
            onLongPress = { offset ->
                Log.d(TAG, "AppScreen: onLongPress $offset")
                onPauseLongPress.invoke()
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