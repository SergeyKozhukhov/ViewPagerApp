package com.example.viewpagerapp.presentation.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.componentsui.stories.StoriesScreen
import com.example.componentsui.stories.page.PageState
import com.example.componentsui.stories.page.StoryPage
import com.example.componentsui.stories.page.story.image.StoryImageContent
import com.example.viewpagerapp.domain.ContentId
import com.example.viewpagerapp.domain.content.StoryContent
import com.example.viewpagerapp.presentation.content.stories.CustomStoriesPage
import com.example.viewpagerapp.presentation.content.stories.CustomStoryScreen
import com.example.viewpagerapp.presentation.content.view.ViewFactory

@Composable
fun ContentScreen(
    currentId: ContentId,
    ids: List<ContentId>,
    viewModel: ContentViewModel = viewModel(factory = ContentViewModel.provide(ids)),
    viewFactory: ViewFactory,
    onCloseClick: () -> Unit,
) {

    val initialPage = remember { ids.indexOfFirst { it == currentId } }

    val uiState = viewModel.uiState
    when (uiState.value) {
        ContentUiState.IDLE -> {}
        ContentUiState.Process -> ContentStoriesScreen(
            initialPage = initialPage,
            viewModel = viewModel,
            viewFactory = viewFactory
        )

        ContentUiState.Finish -> onCloseClick.invoke()
    }
}

@Composable
fun ContentStoriesScreen(
    initialPage: Int,
    viewModel: ContentViewModel,
    viewFactory: ViewFactory
) {

    val contentStates = viewModel.itemStates

    StoriesScreen(
        initialPage = initialPage,
        contentStates = contentStates.toList(),
        onInitStories = { position -> viewModel.onInitStories(position) },
        onCurrentPageChanged = { next -> viewModel.onCurrentPageChanged(next) },
        onSettledPageChanged = { position -> viewModel.onSettledPageChanged(position) },
        onCloseClick = { position -> viewModel.onCloseClick(position) },
        onScreenEvent = { event, page, screen -> viewModel.onScreenEvent(event, page, screen) },
        onBorderEvent = { event, page -> viewModel.onBorderEvent(event, page) },
        onNextPageSwipe = { position -> viewModel.onNextPageSwipe(position) },
        onPreviousPageSwipe = { position -> viewModel.onPreviousPageSwipe(position) },
        onPageScreenShow = { pagePosition, screenPosition ->
            viewModel.onPageScreenShow(pagePosition, screenPosition)
        },
        onError = { position, e -> viewModel.onError(position, e) },
        loadingContent = { LoadingContent() },
        storyScreenContent = { page -> CustomStoryScreen(page, viewFactory) },
        pageContent = { position -> CustomPageContent(pageState = contentStates[position]) }
    )
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CustomStoryScreen(page: StoryPage.Custom, viewFactory: ViewFactory) {
    if (page is CustomStoryScreen) {
        when (page.item) {
            is StoryContent.Story -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    StoryImageContent(
                        image = page.item.image,
                        title = page.item.title,
                        onPrepared = { }
                    )
                }
            }

            is StoryContent.Video -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    SmthView(viewFactory = viewFactory)
                    Text(text = "video: ${page.item}")
                }
            }
        }
    }
}

@Composable
private fun CustomPageContent(pageState: PageState) {
    if (pageState is PageState.Success) {
        val content = pageState.content
        if (content is CustomStoriesPage) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "custom page: ${content.title}")
            }
        }
    }
}

@Composable
private fun SmthView(viewFactory: ViewFactory, modifier: Modifier = Modifier) {
    AndroidView(factory = { context -> viewFactory.createSmth(context) }, modifier = modifier)
}