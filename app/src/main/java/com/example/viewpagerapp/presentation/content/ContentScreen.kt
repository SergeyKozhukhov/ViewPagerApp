package com.example.viewpagerapp.presentation.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.componentsui.stories.page.PageState
import com.example.componentsui.stories.StoriesScreen

private const val TAG = "StoriesScreen"

@Composable
fun ContentScreen(
    currentId: Int,
    ids: IntArray,
    viewModel: ContentViewModel = viewModel(factory = ContentViewModel.provide(ids)),
    onCloseClick: () -> Unit
) {

    val initialPage = remember { ids.indexOfFirst { it == currentId } }
    LaunchedEffect(key1 = true) {
        viewModel.onPageChanged(
            previousPosition = 0,
            nextPosition = initialPage
        )
    }

    val contentStates = viewModel.itemStates
    val uiState = viewModel.uiState
    when (uiState.value) {
        ContentUiState.IDLE -> {}
        ContentUiState.Process -> CBroStoriesScreen(
            initialPage = initialPage,
            contentStates = contentStates,
            viewModel = viewModel
        )

        ContentUiState.Finish -> onCloseClick.invoke()
    }
}

@Composable
fun CBroStoriesScreen(
    initialPage: Int,
    contentStates: List<PageState>,
    viewModel: ContentViewModel
) {
    StoriesScreen(
        initialPage = initialPage,
        contentStates = contentStates.toList(),
        onInitStories = { position -> viewModel.onInitStories(position) },
        onPageChanged = { previous, next -> viewModel.onPageChanged(previous, next) },
        onCloseClick = { position -> viewModel.onCloseClick(position) },
        onNextScreenTap = { page, screen -> viewModel.onNextScreenTap(page, screen) },
        onPreviousScreenTap = { page, screen -> viewModel.onPreviousScreenTap(page, screen) },
        onNextScreenTime = { page, screen -> viewModel.onNextScreenTime(page, screen) },
        onNextPageTap = { position -> viewModel.onNextPageTap(position) },
        onPreviousPageTap = { position -> viewModel.onPreviousPageTap(position) },
        onNextPageSwipe = { position -> viewModel.onNextPageSwipe(position) },
        onPreviousPageSwipe = { position -> viewModel.onPreviousPageSwipe(position) },
        onNextPageTime = { position -> viewModel.onNextPageTime(position) },
        onPageScreenShow = { pagePosition, screenPosition ->
            viewModel.onPageScreenShow(pagePosition, screenPosition)
        },
        onError = { position, e -> viewModel.onError(position, e) }
    )
}