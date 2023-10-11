package com.example.componentsui.stories

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.example.componentsui.stories.elements.CloseButton
import com.example.componentsui.stories.page.ContentPage
import com.example.componentsui.stories.page.PageState
import com.example.componentsui.stories.page.StoryPage
import com.example.componentsui.stories.page.VideoPage
import com.example.componentsui.stories.page.story.StoryScreen
import com.example.componentsui.stories.page.video.VideoScreen
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
    onPageChanged: (previousPosition: Int, nextPosition: Int) -> Unit,
    onCloseClick: (position: Int) -> Unit,
    onNextScreenTap: (page: Int, screen: Int) -> Unit,
    onPreviousScreenTap: (page: Int, screen: Int) -> Unit,
    onNextScreenTime: (page: Int, screen: Int) -> Unit,
    onNextPageTap: (position: Int) -> Unit,
    onPreviousPageTap: (position: Int) -> Unit,
    onNextPageSwipe: (position: Int) -> Unit,
    onPreviousPageSwipe: (position: Int) -> Unit,
    onNextPageTime: (position: Int) -> Unit,
    onPageScreenShow: (pagePosition: Int, screenPosition: Int) -> Unit,
    onError: (position: Int, e: Throwable) -> Unit,
    storyScreenContent: @Composable (StoryPage.Custom) -> Unit,
    pageContent: @Composable (position: Int) -> Unit
) {
    val pagerState =
        rememberPagerState(initialPage = initialPage, pageCount = { contentStates.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState) {
        // Collect from the pager state a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onPageChanged.invoke(pagerState.currentPage, page)
        }
    }

    LaunchedEffect(key1 = initialPage, block = {
        onInitStories.invoke(initialPage)
    })



    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState, modifier = Modifier.fillMaxSize()
        ) { currentPosition ->
            // onPageChanged.invoke(pagerState.currentPage, currentPosition)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(color = Color.Yellow)
                    .fillMaxSize()
                    .border(width = Dp(4f), color = Color.Black)
            ) {
                when (val currentState = contentStates[currentPosition]) {
                    is PageState.Idle -> Text("Idle")
                    is PageState.Loading -> Text("Loading")
                    is PageState.Success -> SuccessState(
                        contentPage = currentState.content,
                        positionPage = currentPosition,
                        isActive = currentPosition == pagerState.settledPage,
                        onNextScreenTap = onNextScreenTap,
                        onPreviousScreenTap = onPreviousScreenTap,
                        onNextScreenTime = onNextScreenTime,
                        onNextPageTap = { newPosition ->
                            scope.launch { pagerState.animateScrollToPage(newPosition + 1) }
                            onNextPageTap.invoke(newPosition + 1)
                        },
                        onPreviousPageTap = { newPosition ->
                            scope.launch { pagerState.animateScrollToPage(newPosition - 1) }
                            onPreviousPageTap.invoke(newPosition + 1)
                        },
                        onNextPageSwipe = onNextPageSwipe,
                        onPreviousPageSwipe = onPreviousPageSwipe,
                        onNextPageTime = { newPosition ->
                            scope.launch { pagerState.animateScrollToPage(newPosition + 1) }
                            onNextPageTime.invoke(newPosition + 1)
                        },
                        onPageScreenShow = onPageScreenShow,
                        storyScreenContent = storyScreenContent,
                        pageContent = pageContent
                    )

                    is PageState.Error -> {
                        onError.invoke(currentPosition, currentState.e)
                        Text("Error")
                    }
                }
                CloseButton(onClick = { onCloseClick.invoke(currentPosition) })
            }
        }
    }
}

@Composable
fun SuccessState(
    contentPage: ContentPage,
    positionPage: Int,
    isActive: Boolean,
    onNextScreenTap: (page: Int, screen: Int) -> Unit,
    onPreviousScreenTap: (page: Int, screen: Int) -> Unit,
    onNextScreenTime: (page: Int, screen: Int) -> Unit,
    onNextPageTap: (position: Int) -> Unit,
    onPreviousPageTap: (position: Int) -> Unit,
    onNextPageSwipe: (position: Int) -> Unit,
    onPreviousPageSwipe: (position: Int) -> Unit,
    onNextPageTime: (position: Int) -> Unit,
    onPageScreenShow: (pagePosition: Int, screenPosition: Int) -> Unit,
    storyScreenContent: @Composable (StoryPage.Custom) -> Unit,
    pageContent: @Composable (position: Int) -> Unit
) {
    // TODO: to api
    when (contentPage) {
        is StoryPage -> {
            StoryScreen(
                positionPage = positionPage,
                isActive = isActive,
                story = contentPage,
                onNextScreenTap = onNextScreenTap,
                onPreviousScreenTap = onPreviousScreenTap,
                onNextScreenTime = onNextScreenTime,
                onNextPageTap = { onNextPageTap.invoke(positionPage) },
                onPreviousPageTap = { onPreviousPageTap.invoke(positionPage) },
                onNextPageTime = { onNextPageTime.invoke(positionPage) },
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