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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.example.componentsui.stories.elements.CloseButton
import com.example.componentsui.stories.page.ContentPage
import com.example.componentsui.stories.page.PageState
import com.example.componentsui.stories.page.StoryPage
import com.example.componentsui.stories.page.VideoPage
import com.example.componentsui.stories.page.defaults.DefaultErrorState
import com.example.componentsui.stories.page.defaults.DefaultIdleState
import com.example.componentsui.stories.page.defaults.DefaultLoadingState
import com.example.componentsui.stories.page.story.StoryScreen
import com.example.componentsui.stories.page.video.VideoScreen
import com.example.componentsui.story.StoryScreenBorderEvent
import com.example.componentsui.story.StoryScreenEvent
import kotlinx.coroutines.launch

// https://www.composables.com/components/foundation/horizontalpager
// TODO: position - [1...], index - [0...]
// TODO: derivedStateOf
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoriesScreen(
    initialPage: Int,
    contentStates: List<PageState>,
    onInitStories: (position: Int) -> Unit,
    onCurrentPageChanged: (nextPosition: Int) -> Unit,
    onSettledPageChanged: (position: Int) -> Unit,
    onCloseClick: (position: Int) -> Unit,
    onScreenEvent: (event: StoryScreenEvent, page: Int, screen: Int) -> Unit,
    onBorderEvent: (event: StoryScreenBorderEvent, page: Int) -> Unit,
    onNextPageSwipe: (position: Int) -> Unit,
    onPreviousPageSwipe: (position: Int) -> Unit,
    onPageScreenShow: (pagePosition: Int, screenPosition: Int) -> Unit,
    onError: (position: Int, e: Throwable) -> Unit,
    idleContent: @Composable () -> Unit = { DefaultIdleState() },
    loadingContent: @Composable () -> Unit = { DefaultLoadingState() },
    storyScreenContent: @Composable (StoryPage.Custom) -> Unit,
    pageContent: @Composable (position: Int) -> Unit,
    errorContent: @Composable (e: Throwable) -> Unit = { DefaultErrorState(it) }
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

    HorizontalPager(
        state = pagerState,
        pageCount = contentStates.size,
        modifier = Modifier.fillMaxSize(),
        beyondBoundsPageCount = 1
    ) { currentPosition ->
        Log.d("ContentViewModel", "preload position: $currentPosition")
        Box(
            modifier = Modifier
                .background(color = Color.Yellow)
                .fillMaxSize()
                .border(width = Dp(4f), color = Color.Black)
        ) {
            when (val currentState = contentStates[currentPosition]) {
                is PageState.Idle -> idleContent.invoke()
                is PageState.Loading -> loadingContent.invoke()
                is PageState.Success -> SuccessState(
                    contentPage = currentState.content,
                    positionPage = currentPosition,
                    isActive = currentPosition == pagerState.settledPage && !pagerState.isScrollInProgress,
                    onScreenEvent = onScreenEvent,
                    onBorderEvent = { event, page ->
                        onBorderEvent.invoke(event, page)
                        if (event == StoryScreenBorderEvent.NEXT_SCREEN_TAP || event == StoryScreenBorderEvent.NEXT_SCREEN_TIME) {
                            scope.launch { pagerState.animateScrollToPage(page + 1) }
                        }

                        if (event == StoryScreenBorderEvent.PREVIOUS_SCREEN_TAP) {
                            scope.launch { pagerState.animateScrollToPage(page - 1) }
                        }
                    },
                    onNextPageSwipe = onNextPageSwipe,
                    onPreviousPageSwipe = onPreviousPageSwipe,
                    onPageScreenShow = onPageScreenShow,
                    storyScreenContent = storyScreenContent,
                    pageContent = pageContent
                )

                is PageState.Error -> {
                    onError.invoke(currentPosition, currentState.e)
                    errorContent.invoke(currentState.e)
                }
            }
            CloseButton(onClick = { onCloseClick.invoke(currentPosition) })
        }
    }
}

@Composable
fun SuccessState(
    contentPage: ContentPage,
    positionPage: Int,
    isActive: Boolean,
    onScreenEvent: (event: StoryScreenEvent, page: Int, screen: Int) -> Unit,
    onBorderEvent: (event: StoryScreenBorderEvent, page: Int) -> Unit,
    onNextPageSwipe: (position: Int) -> Unit,
    onPreviousPageSwipe: (position: Int) -> Unit,
    onPageScreenShow: (pagePosition: Int, screenPosition: Int) -> Unit,
    storyScreenContent: @Composable (StoryPage.Custom) -> Unit,
    pageContent: @Composable (position: Int) -> Unit
) {
    // TODO: to api
    when (contentPage) {
        is StoryPage -> {
            StoryScreen(
                id = positionPage,
                isActive = isActive,
                story = contentPage,
                onScreenEvent = { event, screen ->
                    onScreenEvent.invoke(event, positionPage, screen)
                },
                onBorderEvent = { event -> onBorderEvent.invoke(event, positionPage) },
                screenContent = storyScreenContent
            )
        }

        is VideoPage -> {
            VideoScreen(
                videoPage = contentPage, isActive = isActive
            )
        }

        else -> {
            pageContent.invoke(positionPage)
        }
    }
}