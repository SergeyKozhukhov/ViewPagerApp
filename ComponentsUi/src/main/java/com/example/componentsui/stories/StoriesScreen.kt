package com.example.componentsui.stories

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.componentsui.legacy.elements.CloseButton
import com.example.componentsui.stories.page.PageState
import com.example.componentsui.stories.page.story.StoryScreen
import com.example.componentsui.story.StoryScreenBorderEvent
import com.example.componentsui.story.StoryScreenEvent
import kotlinx.coroutines.launch

/** Длительность просмотра одного экрана страницы Истории */
const val SCREEN_DURATION = 5000L // ms

// https://www.composables.com/components/foundation/horizontalpager
// TODO: position - [1...], index - [0...]
// TODO: derivedStateOf

/**
 * Базовая имплементация плеера Историй. Позволяет принимать на вход любые данные.
 * Для запуска отображения экранов страницы Истории следует передать список.
 *
 * @param initialPage позцция начальной страницы [0, size - 1]
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <Screen, Page : PageState<Screen>> StoriesScreen(
    initialPage: Int,
    contentStates: List<Page>,
    isActive: Boolean = true,
    onInitStories: (position: Int) -> Unit,
    onCurrentPageChanged: (nextPosition: Int) -> Unit,
    onSettledPageChanged: (position: Int) -> Unit,
    onCloseClick: (position: Int) -> Unit,
    onScreenEvent: (event: StoryScreenEvent, page: Int, screen: Int) -> Unit,
    onBorderEvent: (event: StoryScreenBorderEvent, page: Int) -> Unit,
    onNextPageSwipe: (position: Int) -> Unit,
    onPreviousPageSwipe: (position: Int) -> Unit,
    onPageScreenShow: (pagePosition: Int, screenPosition: Int) -> Unit,
    screenFactory: @Composable (Screen, onScreenReady: () -> Unit) -> Unit,
    pageFactory: @Composable (Page) -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val pagerState = rememberPagerState(initialPage = initialPage)
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState) {
        // Collect from the pager state a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onCurrentPageChanged.invoke(page)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            onSettledPageChanged.invoke(page)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.targetPage }.collect { page ->
            Log.d("ContentViewModel", "targetPage: ${page}")
        }
    }

    LaunchedEffect(key1 = initialPage, block = {
        onInitStories.invoke(initialPage)
    })

    val userScrollEnabled = remember { mutableStateOf(true) }
    var isRunningTimerAllowed by remember { mutableStateOf(true) }

    DisposableEffect(lifecycleOwner) {
        val observer = createObserver(
            onPause = { isRunningTimerAllowed = false },
            onResume = { isRunningTimerAllowed = true })
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    HorizontalPager(
        state = pagerState,
        pageCount = contentStates.size,
        modifier = Modifier.fillMaxSize(),
        beyondBoundsPageCount = 1,
        userScrollEnabled = userScrollEnabled.value
    ) { currentPosition ->
        Log.d("ContentViewModel", "preload position: $currentPosition")
        Box(
            modifier = Modifier
                .background(color = Color.Yellow)
                .fillMaxSize()
                .border(width = Dp(4f), color = Color.Black)
        ) {
            val currentStateV2 = contentStates[currentPosition]
            if (currentStateV2.screens != null) {
                SuccessState<Screen>(
                    screens = currentStateV2.screens!!,
                    currentPosition = currentPosition,
                    settledPosition = pagerState.settledPage,
                    isActive = isActive && currentPosition == pagerState.settledPage && !pagerState.isScrollInProgress && isRunningTimerAllowed,
                    onScreenEvent = onScreenEvent,
                    onBorderEvent = { event, page ->
                        onBorderEvent.invoke(event, page)
                        if (event == StoryScreenBorderEvent.NEXT_SCREEN_TAP || event == StoryScreenBorderEvent.NEXT_SCREEN_TIME) {
                            scope.launch {
                                userScrollEnabled.value = false
                                pagerState.animateScrollToPage(page + 1)
                                userScrollEnabled.value = true
                            }
                        }

                        if (event == StoryScreenBorderEvent.PREVIOUS_SCREEN_TAP) {
                            scope.launch { pagerState.animateScrollToPage(page - 1) }
                        }
                    },
                    onNextPageSwipe = onNextPageSwipe,
                    onPreviousPageSwipe = onPreviousPageSwipe,
                    onPageScreenShow = onPageScreenShow,
                    storyScreenContent = screenFactory,
                )
            } else {
                pageFactory.invoke(currentStateV2)
            }
            CloseButton(onClick = { onCloseClick.invoke(currentPosition) })
        }
    }
}

@Composable
fun <Screen> SuccessState(
    screens: List<Screen>,
    currentPosition: Int,
    settledPosition: Int,
    isActive: Boolean,
    onScreenEvent: (event: StoryScreenEvent, page: Int, screen: Int) -> Unit,
    onBorderEvent: (event: StoryScreenBorderEvent, page: Int) -> Unit,
    onNextPageSwipe: (position: Int) -> Unit,
    onPreviousPageSwipe: (position: Int) -> Unit,
    onPageScreenShow: (pagePosition: Int, screenPosition: Int) -> Unit,
    storyScreenContent: @Composable (Screen, onScreenReady: () -> Unit) -> Unit,
) {
    // TODO: to api
    StoryScreen(
        settledId = settledPosition,
        isActive = isActive,
        screens = screens,
        screenDuration = SCREEN_DURATION,
        onScreenEvent = { event, screen ->
            onScreenEvent.invoke(event, currentPosition, screen)
        },
        onBorderEvent = { event -> onBorderEvent.invoke(event, currentPosition) },
        screenContent = storyScreenContent
    )
}

private fun createObserver(onPause: () -> Unit, onResume: () -> Unit) =
    object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) = onResume.invoke()

        override fun onPause(owner: LifecycleOwner) = onPause.invoke()
    }