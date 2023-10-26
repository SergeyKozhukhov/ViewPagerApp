package com.example.componentsui.story

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
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
    screenContent: @Composable (screen: Int, onScreenReady: () -> Unit) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = 0)
    val scope = rememberCoroutineScope()

    var isTimerPaused by remember { mutableStateOf(false) }
    var isScreenContentReady by remember { mutableStateOf(false) }

    var curPage by remember { mutableStateOf(0) }


    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page -> curPage = page }
    }

    HorizontalPager(
        state = pagerState,
        pageCount = screenCount,
        beyondBoundsPageCount = 1,
        userScrollEnabled = false
    ) { position ->
        var isPrepared by remember { mutableStateOf(false) }
        if (curPage == position) {
            Log.d("ContentViewModel", "StoryViewer HorizontalPager: $isPrepared pos:$position")
            isScreenContentReady = isPrepared
        }
        CurrentScreen(currentSlice = position,
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
                        StoryScreenEvent.PREVIOUS_SCREEN_TAP, pagerState.settledPage
                    )
                    scope.launch { pagerState.scrollToPage(position - 1) }
                } else {
                    onBorderEvent.invoke(StoryScreenBorderEvent.PREVIOUS_SCREEN_TAP)
                }
            },
            onLongPressStart = { isTimerPaused = true },
            onLongPressEnd = { isTimerPaused = false },
            screenContent = { pos -> screenContent.invoke(pos) { isPrepared = true } })
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
    ) {
        AnimatedProgressBar(id = settledId,
            screenPosition = pagerState.currentPage,
            screenCount = screenCount,
            screenDuration = screenDuration,
            isRunning = isActive && !isTimerPaused && isScreenContentReady,
            onFinish = {
                if (pagerState.canScrollForward) {
                    onScreenEvent.invoke(StoryScreenEvent.NEXT_SCREEN_TIME, pagerState.currentPage)
                    scope.launch { pagerState.scrollToPage(pagerState.currentPage + 1) }
                } else {
                    onBorderEvent.invoke(StoryScreenBorderEvent.NEXT_SCREEN_TIME)
                }
            })
    }
}


@Composable
fun CurrentScreen(
    currentSlice: Int,
    onNextScreenTap: () -> Unit,
    onPreviousScreenTap: () -> Unit,
    onLongPressStart: () -> Unit,
    onLongPressEnd: () -> Unit,
    screenContent: @Composable (screen: Int) -> Unit,
) {
    Box(
        modifier = Modifier.storyScreenGestures(
            onNextScreenTap = onNextScreenTap,
            onPreviousScreenTap = onPreviousScreenTap,
            onLongPressStart = onLongPressStart,
            onLongPressEnd = onLongPressEnd
        )
    ) {
        screenContent.invoke(currentSlice)
    }
}

// https://www.bam.tech/article/detect-instagram-like-gestures-with-jetpack-compose
// https://slack-chats.kotlinlang.org/t/1204341/any-modifier-for-hold-element-combinedclickable-have-onclick
private fun Modifier.storyScreenGestures(
    onNextScreenTap: () -> Unit,
    onPreviousScreenTap: () -> Unit,
    onLongPressStart: () -> Unit,
    onLongPressEnd: () -> Unit
) =
    pointerInput(Unit) {
        val halfWidth = this.size.width / 2f
        detectTapGestures(
            onTap = { offset ->
                Log.d(TAG, "AppScreen: onTap $offset")
                if (offset.x < halfWidth) {
                    onPreviousScreenTap.invoke()
                } else {
                    onNextScreenTap.invoke()
                }
            })
    }.pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false)
            onLongPressStart()
            println("Long press")
            while (true) {
                val event2 = awaitPointerEvent(PointerEventPass.Main)
                if (event2.type == PointerEventType.Release) {
                    println("Long press ending")
                    onLongPressEnd()
                    break
                }
            }
        }
    }