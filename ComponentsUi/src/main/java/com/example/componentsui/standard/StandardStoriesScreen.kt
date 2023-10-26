package com.example.componentsui.standard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import com.example.componentsui.standard.screen.StoryImageContent
import com.example.componentsui.standard.screen.StoryVideoContent
import com.example.componentsui.stories.StoriesScreen
import com.example.componentsui.story.StoryScreenBorderEvent
import com.example.componentsui.story.StoryScreenEvent

// https://www.composables.com/components/foundation/horizontalpager
// TODO: position - [1...], index - [0...]
// TODO: derivedStateOf
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StandardStoriesScreen(
    initialPage: Int,
    contentStates: List<StandardStoryPage>,
    isActive: Boolean,
    onInitStories: (position: Int) -> Unit,
    onCurrentPageChanged: (nextPosition: Int) -> Unit,
    onSettledPageChanged: (position: Int) -> Unit,
    onCloseClick: (position: Int) -> Unit,
    onScreenEvent: (event: StoryScreenEvent, page: Int, screen: Int) -> Unit,
    onBorderEvent: (event: StoryScreenBorderEvent, page: Int) -> Unit,
    onNextPageSwipe: (position: Int) -> Unit,
    onPreviousPageSwipe: (position: Int) -> Unit,
    onPageScreenShow: (pagePosition: Int, screenPosition: Int) -> Unit,
) {
    StoriesScreen(initialPage = initialPage,
        contentStates = contentStates,
        onInitStories = onInitStories,
        isActive = isActive,
        onCurrentPageChanged = onCurrentPageChanged,
        onSettledPageChanged = onSettledPageChanged,
        onCloseClick = onCloseClick,
        onScreenEvent = onScreenEvent,
        onBorderEvent = onBorderEvent,
        onNextPageSwipe = onNextPageSwipe,
        onPreviousPageSwipe = onPreviousPageSwipe,
        onPageScreenShow = onPageScreenShow,
        screenFactory = { screen, onScreenReady -> ScreenFactoryImpl(screen, onScreenReady) },
        pageFactory = { _ -> /* do nothing */ })
}

@Composable
private fun ScreenFactoryImpl(screen: StandardStoryPage.Screen, onContentLoaded: () -> Unit) {
    when (screen) {
        is StandardStoryPage.Image -> StoryImageContent(
            image = screen.image, title = screen.title, onContentLoaded = onContentLoaded
        )

        is StandardStoryPage.Video -> StoryVideoContent(
            video = screen.video, title = screen.title
        )
    }
}