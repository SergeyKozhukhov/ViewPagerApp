package com.example.componentsui.stories.page.story

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StoryViewModel(
    private val screenCount: Int
) : ViewModel(), DefaultLifecycleObserver {

    var delta = DEFAULT_DELTA

    val screenState = mutableStateOf(StoryState())

    private var progress = 0.00f

    fun startTimer() {
        viewModelScope.coroutineContext.cancelChildren()
        launchTimer(0f)
    }

    private fun launchTimer(startPosition: Float) {
        viewModelScope.launch {
            progress = startPosition
            while (progress < 1f) {
                progress += delta
                screenState.value =
                    StoryState(screenIndex = screenState.value.screenIndex, progress = progress)
                delay(delayInMillis.toLong())
            }

            onNextTime()
        }
    }

    private fun onNextTime() {
        progress = 0f

        screenState.value = if (screenState.value.screenIndex < screenCount - 1) {
            StoryState(
                screenIndex = screenState.value.screenIndex + 1,
                progress = 0f,
                isNextScreenTime = true
            )
        } else {
            screenState.value.copy(isNextPageTime = true)
        }
    }

    fun onNextTap() {
        progress = 0f

        screenState.value = if (screenState.value.screenIndex < screenCount - 1) {
            StoryState(
                screenIndex = screenState.value.screenIndex + 1,
                progress = 0f,
                isNextScreenTap = true
            )
        } else {
            screenState.value.copy(isNextPageTap = true)
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        viewModelScope.coroutineContext.cancelChildren()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        launchTimer(progress)
    }

    fun onPreviousTap() {
        progress = 0f

        screenState.value = if (screenState.value.screenIndex > 0) {
            StoryState(screenIndex = screenState.value.screenIndex - 1, isPreviousScreenTap = true)
        } else {
            StoryState(progress = 0f, isPreviousPageTap = true)
        }
    }

    fun onPause() {
        viewModelScope.coroutineContext.cancelChildren()
    }

    fun resetPage() {
        progress = 0f
        screenState.value =
            screenState.value.copy(
                isNextScreenTap = false,
                isPreviousScreenTap = false,
                isNextScreenTime = false,
                isNextPageTap = false,
                isPreviousPageTap = false,
                isNextPageTime = false
            )
    }

    fun resetScreen() {
        progress = 0f
        screenState.value =
            screenState.value.copy(
                progress = 0f,
                isNextScreenTap = false,
                isPreviousScreenTap = false,
                isNextScreenTime = false,
            )
    }

    companion object {
        private const val SLIDE_DURATION = 5 // s
        const val delayInMillis = (SLIDE_DURATION * 1000) / 100 // ms

        const val DEFAULT_DELTA = 0.01f


        @Suppress("UNCHECKED_CAST")
        fun provide(count: Int) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StoryViewModel(count) as T
            }
        }
    }
}