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
import com.example.componentsui.stories.page.story.image.StoryImageContent
import com.example.viewpagerapp.domain.ContentId
import com.example.viewpagerapp.domain.content.StoryContent
import com.example.viewpagerapp.presentation.content.develop.ContentViewModelV2
import com.example.viewpagerapp.presentation.content.develop.StoriesScreenV2
import com.example.viewpagerapp.presentation.content.view.ViewFactory

@Composable
fun ContentScreenV2(
    currentId: ContentId,
    ids: List<ContentId>,
    viewModel: ContentViewModelV2 = viewModel(factory = ContentViewModelV2.provide(ids)),
    viewFactory: ViewFactory,
    onCloseClick: () -> Unit,
) {

    val initialPage = remember { ids.indexOfFirst { it == currentId } }

    val uiState = viewModel.uiState
    when (uiState.value) {
        ContentUiState.IDLE -> {}
        ContentUiState.Process -> ContentStoriesScreenV2(
            initialPage = initialPage,
            viewModel = viewModel,
            viewFactory = viewFactory
        )

        ContentUiState.Finish -> onCloseClick.invoke()
    }
}

@Composable
fun ContentStoriesScreenV2(
    initialPage: Int,
    viewModel: ContentViewModelV2,
    viewFactory: ViewFactory
) {

    val contentStates = viewModel.itemStates

    StoriesScreenV2(
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
        storyScreenContent = { page -> MyStoryScreen(page, viewFactory) },
        videoContent = { item -> Text("smth $item") }
    )
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun MyStoryScreen(item: StoryContent.Item, viewFactory: ViewFactory) {
    when (item) {
        is StoryContent.Story -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                StoryImageContent(
                    image = item.image,
                    title = item.title,
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
                Text(text = "video: $item")
            }
        }
    }
}

@Composable
private fun SmthView(viewFactory: ViewFactory, modifier: Modifier = Modifier) {
    AndroidView(factory = { context -> viewFactory.createSmth(context) }, modifier = modifier)
}