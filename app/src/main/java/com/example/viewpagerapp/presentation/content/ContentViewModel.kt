package com.example.viewpagerapp.presentation.content

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.viewpagerapp.data.DataSource
import com.example.viewpagerapp.data.Repository
import com.example.viewpagerapp.data.converters.ContentConverter
import com.example.viewpagerapp.data.converters.EntryPointConverter
import com.example.componentsui.stories.page.PageState
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch


private const val TAG = "ContentViewModel"

class ContentViewModel(
    private val ids: IntArray,
    private val repository: Repository,
    private val storyConverter: com.example.viewpagerapp.presentation.content.ContentConverter
) : ViewModel() {


    val uiState = mutableStateOf<ContentUiState>(ContentUiState.IDLE)
    val itemStates: SnapshotStateList<PageState> = ids.map { PageState.Idle }.toMutableStateList()

    fun onPageChanged(previousPosition: Int, nextPosition: Int) {
        uiState.value = ContentUiState.Process
        viewModelScope.launch {
            val currentStoryState = itemStates[nextPosition]
            try {
                if (currentStoryState is PageState.Idle) {
                    itemStates[nextPosition] = PageState.Loading
                    val response = repository.getContent(ids[nextPosition])
                    val contentPage = storyConverter.convert(response)
                    itemStates[nextPosition] = PageState.Success(contentPage)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                itemStates[nextPosition] = PageState.Error(e)
            }
        }
    }

    fun onInitStories(position: Int) {
        Log.d(TAG, "onInitStories: $position")
    }

    fun onCloseClick(position: Int) {
        Log.d(TAG, "onCloseClick: $position")
        uiState.value = ContentUiState.Finish
    }

    fun onNextScreenTap(page: Int, screen: Int) {
        Log.d(TAG, "onNextScreenTap: $page $screen")
    }

    fun onPreviousScreenTap(page: Int, screen: Int) {
        Log.d(TAG, "onPreviousScreenTap: $page $screen")
    }

    fun onNextScreenTime(page: Int, screen: Int) {
        Log.d(TAG, "onNextScreenTime: $page $screen")
    }

    fun onNextPageTap(position: Int) {
        Log.d(TAG, "onNextPageTap: $position")
    }

    fun onPreviousPageTap(position: Int) {
        Log.d(TAG, "onPreviousPageTap: $position")
    }

    fun onNextPageSwipe(position: Int) {
        Log.d(TAG, "onNextPageSwipe: $position")
    }

    fun onPreviousPageSwipe(position: Int) {
        Log.d(TAG, "onPreviousPageSwipe: $position")
    }

    fun onNextPageTime(position: Int) {
        Log.d(TAG, "onNextPageTime: $position")
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
                    com.example.viewpagerapp.presentation.content.ContentConverter()
                ) as T
            }
        }
    }
}