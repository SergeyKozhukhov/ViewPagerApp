package com.example.componentsui.story

import androidx.compose.runtime.State
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
    private val screenCount: Int,
    private val screenDuration: Int // s
) : ViewModel(), DefaultLifecycleObserver {

    private val delayInMillis = ((screenDuration * 1000) / 100).toLong() // ms

    val uiState: State<StoryState> get() = _uiState
    private val _uiState = mutableStateOf(StoryState())

    private var progress = 0.00f

    fun startProgress() {
        viewModelScope.coroutineContext.cancelChildren()
        launchTimer(progress)
    }

    private fun launchTimer(startPosition: Float) {
        viewModelScope.launch {
            progress = startPosition
            while (progress < 1f) {
                progress += DEFAULT_DELTA
                _uiState.value =
                    StoryState(
                        currentScreenIndex = uiState.value.currentScreenIndex,
                        screenProgress = progress
                    )
                delay(delayInMillis)
            }

            onNextTime()
        }
    }

    private fun onNextTime() {
        progress = 0f

        _uiState.value = if (uiState.value.currentScreenIndex < screenCount - 1) {
            StoryState(
                currentScreenIndex = uiState.value.currentScreenIndex + 1,
                screenProgress = 0f,
                event = StoryScreenEvent.NEXT_SCREEN_TIME,
            )
        } else {
            uiState.value.copy(borderEvent = StoryScreenBorderEvent.NEXT_SCREEN_TIME)
        }
    }

    fun onNextTap() {
        progress = 0f

        _uiState.value = if (uiState.value.currentScreenIndex < screenCount - 1) {
            StoryState(
                currentScreenIndex = uiState.value.currentScreenIndex + 1,
                screenProgress = 0f,
                event = StoryScreenEvent.NEXT_SCREEN_TAP,
            )
        } else {
            uiState.value.copy(borderEvent = StoryScreenBorderEvent.NEXT_SCREEN_TAP)
        }
    }

    fun onPreviousTap() {
        progress = 0f

        _uiState.value = if (uiState.value.currentScreenIndex > 0) {
            StoryState(
                currentScreenIndex = uiState.value.currentScreenIndex - 1,
                event = StoryScreenEvent.PREVIOUS_SCREEN_TAP,
            )
        } else {
            StoryState(
                screenProgress = 0f,
                borderEvent = StoryScreenBorderEvent.PREVIOUS_SCREEN_TAP
            )
        }
    }

    fun onPause() {
        viewModelScope.coroutineContext.cancelChildren()
    }

    fun resetPage() {
        progress = 0f
        _uiState.value =
            uiState.value.copy(
                event = StoryScreenEvent.IDLE,
                borderEvent = StoryScreenBorderEvent.IDLE,
            )
    }

    fun onUpdateScreen() {
        progress = 0f
        _uiState.value =
            uiState.value.copy(
                screenProgress = 0f,
                event = StoryScreenEvent.IDLE,
            )
    }


    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        viewModelScope.coroutineContext.cancelChildren()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        launchTimer(progress)
    }

    companion object {
        const val DEFAULT_DELTA = 0.01f


        @Suppress("UNCHECKED_CAST")
        fun provide(screenCount: Int, screenDuration: Int) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StoryViewModel(screenCount, screenDuration) as T
            }
        }
    }
}