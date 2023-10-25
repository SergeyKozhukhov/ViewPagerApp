package com.example.componentsui.story

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalViewConfiguration
import kotlinx.coroutines.launch

private const val TAG = "StoriesScreen"

// https://www.droidcon.com/2022/10/12/how-to-handle-viewmodel-one-time-events-in-jetpack-compose/
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoryViewer(
    settledId: Int,
    isActive: Boolean,
    screenCount: Int,
    screenDuration: Long, // ms
    onScreenEvent: (event: StoryScreenEvent, screen: Int) -> Unit,
    onBorderEvent: (event: StoryScreenBorderEvent) -> Unit,
    screenContent: @Composable (screen: Int) -> Unit,
) {

    val pagerState = rememberPagerState(initialPage = 0)
    val scope = rememberCoroutineScope()

    var isPaused by remember { mutableStateOf(false) }

    HorizontalPager(
        state = pagerState,
        pageCount = screenCount,
        beyondBoundsPageCount = 1,
        userScrollEnabled = false
    ) { position ->
        CurrentScreen(
            currentSlice = position,
            onNextScreenTap = {
                if (pagerState.canScrollForward) {
                    onScreenEvent.invoke(StoryScreenEvent.NEXT_SCREEN_TAP, position)
                    scope.launch { pagerState.scrollToPage(position + 1) }
                } else {
                    onBorderEvent.invoke(StoryScreenBorderEvent.NEXT_SCREEN_TAP)
                }
            },
            onPreviousScreenTap = {
                if (pagerState.canScrollBackward) {
                    onScreenEvent.invoke(
                        StoryScreenEvent.PREVIOUS_SCREEN_TAP,
                        pagerState.settledPage
                    )
                    scope.launch { pagerState.scrollToPage(position - 1) }
                } else {
                    onBorderEvent.invoke(StoryScreenBorderEvent.PREVIOUS_SCREEN_TAP)
                }
            },
            onPauseLongPress = { isPaused = true },
            screenContent = screenContent
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
    ) {
        AnimatedProgressBar(
            id = settledId,
            screenPosition = pagerState.currentPage,
            screenCount = screenCount,
            screenDuration = screenDuration,
            isRunning = isActive && !isPaused,
            onFinish = {
                if (pagerState.canScrollForward) {
                    onScreenEvent.invoke(StoryScreenEvent.NEXT_SCREEN_TIME, pagerState.currentPage)
                    scope.launch { pagerState.scrollToPage(pagerState.currentPage + 1) }
                } else {
                    onBorderEvent.invoke(StoryScreenBorderEvent.NEXT_SCREEN_TIME)
                }
            }
        )
    }
}


@Composable
fun CurrentScreen(
    currentSlice: Int,
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