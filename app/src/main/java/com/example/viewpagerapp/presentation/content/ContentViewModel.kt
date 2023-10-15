package com.example.viewpagerapp.presentation.content

import android.content.res.Resources.NotFoundException
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.componentsui.stories.page.PageState
import com.example.componentsui.story.StoryScreenBorderEvent
import com.example.componentsui.story.StoryScreenEvent
import com.example.viewpagerapp.data.DataSource
import com.example.viewpagerapp.data.Repository
import com.example.viewpagerapp.data.converters.ContentConverter
import com.example.viewpagerapp.data.converters.EntryPointConverter
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private const val TAG = "ContentViewModel"

class ContentViewModel(
    private val ids: IntArray,
    private val repository: Repository,
    private val storyConverter: com.example.viewpagerapp.presentation.content.ContentConverter,
    private val preload: Int = 0
) : ViewModel() {

    val uiState = mutableStateOf<ContentUiState>(ContentUiState.Process)

    val itemStates: SnapshotStateList<PageState> = ids.map { PageState.Idle }.toMutableStateList()

    var prevPosition = -1
    private var isNeedPagination = true

    fun onPageChanged(previousPosition: Int, nextPosition: Int) {
        Log.d(TAG, "onPageChanged: prev: $previousPosition next: $nextPosition")
        uiState.value = ContentUiState.Process
        viewModelScope.launch {
            val prev = if (prevPosition == -1) nextPosition else prevPosition
            fetch(prev, nextPosition)
            prevPosition = nextPosition
        }
    }

    private suspend fun fetch(prevPos: Int, nextPos: Int) {
        if (isNeedPagination) {
            isNeedPagination = false
            makeGroupRequest(nextPos)
        } else {
            makeSingleRequest(prevPos, nextPos)
        }
    }

    private suspend fun makeGroupRequest(position: Int) {
        val indexes = getGroupPositions(position = position)
        try {
            val requestIds = mutableListOf<Int>() // there are ids
            indexes.forEach { index ->
                if (itemStates[index] == PageState.Idle) {
                    requestIds.add(ids[index])
                    itemStates[index] = PageState.Loading
                }
            }
            val result = repository.getContent(requestIds.toList())
            requestIds.forEach { requestId ->
                itemStates[ids.indexOfFirst { it == requestId }] =
                    result.find { it.id == requestId }?.let { content ->
                        val contentPage = storyConverter.convert(content)
                        PageState.Success(contentPage)
                    } ?: run {
                        PageState.Error(NotFoundException())
                    }
            }
        } catch (e: CancellationException) {
            update(indexes, PageState.Error(e))
            throw e
        } catch (e: Exception) {
            update(indexes, PageState.Error(e))
        }
    }

    private fun update(indexes: IntRange, state: PageState) {
        (indexes.first..indexes.last).forEach { index -> itemStates[index] = state }
    }

    private suspend fun makeSingleRequest(prevPos: Int, nextPos: Int) {
        val pos = detectSinglePosition(prevPos, nextPos)
        if (pos < 0) return
        val currentStoryState = itemStates[pos]
        try {
            if (currentStoryState is PageState.Idle) {
                itemStates[pos] = PageState.Loading
                val result = repository.getContent(ids[pos])
                val ans = storyConverter.convert(result)
                itemStates[pos] = PageState.Success(ans)
            }
        } catch (e: CancellationException) {
            itemStates[pos] = PageState.Error(e)
            throw e
        } catch (e: Exception) {
            itemStates[pos] = PageState.Error(e)
        }
    }

    private fun getGroupPositions(position: Int): IntRange {
        val preStartPosition = position - preload
        val startPosition = if (preStartPosition >= 0) preStartPosition else 0

        val preEndPosition = position + preload
        val endPosition =
            if (preEndPosition < ids.size) preEndPosition else (ids.size - 1) // TODO: if size is 0

        return IntRange(start = startPosition, endInclusive = endPosition)
    }

    private fun detectSinglePosition(prevPos: Int, nextPos: Int): Int {
        val res = if (nextPos > prevPos) {
            nextPos + 1
        } else {
            nextPos - 1
        }
        return if (res >= 0 && res < ids.size) {
            res
        } else {
            -1
        }
    }

    fun onInitStories(position: Int) {
        Log.d(TAG, "onInitStories: $position")
    }

    fun onCloseClick(position: Int) {
        Log.d(TAG, "onCloseClick: $position")
        uiState.value = ContentUiState.Finish
    }

    fun onScreenEvent(event: StoryScreenEvent, page: Int, screen: Int) {
        when (event) {
            StoryScreenEvent.IDLE -> {}
            StoryScreenEvent.NEXT_SCREEN_TAP -> Log.d(TAG, "onNextScreenTap: $page $screen")
            StoryScreenEvent.PREVIOUS_SCREEN_TAP -> Log.d(TAG, "onPreviousScreenTap: $page $screen")
            StoryScreenEvent.NEXT_SCREEN_TIME -> Log.d(TAG, "onNextScreenTime: $page $screen")
        }
    }

    fun onBorderEvent(event: StoryScreenBorderEvent, page: Int) {
        when (event) {
            StoryScreenBorderEvent.IDLE -> {}
            StoryScreenBorderEvent.NEXT_SCREEN_TAP -> Log.d(TAG, "onNextPageTap: $page")
            StoryScreenBorderEvent.PREVIOUS_SCREEN_TAP -> Log.d(TAG, "onPreviousPageTap: $page")
            StoryScreenBorderEvent.NEXT_SCREEN_TIME -> Log.d(TAG, "onNextPageTime: $page")
        }
    }

    fun onNextPageSwipe(position: Int) {
        Log.d(TAG, "onNextPageSwipe: $position")
    }

    fun onPreviousPageSwipe(position: Int) {
        Log.d(TAG, "onPreviousPageSwipe: $position")
    }

    fun onPageScreenShow(pagePosition: Int, screenPosition: Int) {
        Log.d(TAG, "onPageScreenShow: $pagePosition $screenPosition")
    }

    fun onError(position: Int, e: Throwable) {
        Log.d(TAG, "onError: $position ${e}")
    }

    companion object {

        fun provide(ids: IntArray) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val repository = Repository(
                    dataSource = DataSource(
                        context = application, objectMapper = ObjectMapper()
                    ),
                    entryPointConverter = EntryPointConverter(),
                    contentConverter = ContentConverter()
                )
                return ContentViewModel(
                    ids,
                    repository,
                    com.example.viewpagerapp.presentation.content.ContentConverter(),
                    1
                ) as T
            }
        }
    }
}