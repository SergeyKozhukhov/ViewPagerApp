package com.example.componentsui.stories.page

sealed interface PageState {

    object Idle : PageState
    object Loading : PageState
    data class Success(val content: ContentPage) : PageState
    data class Error(val e: Throwable) : PageState
}
