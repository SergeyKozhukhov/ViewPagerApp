package com.example.viewpagerapp.presentation.content

import com.example.componentsui.stories.page.PageState

sealed class ContentPageState<out T> : PageState<T> {

    object Idle : ContentPageState<Nothing>() {
        override val screens: List<Nothing>? = null
    }

    object Loading : ContentPageState<Nothing>() {
        override val screens: List<Nothing>? = null
    }

    data class Success<T>(override val screens: List<T>) : ContentPageState<T>()

    object Error : ContentPageState<Nothing>() {
        override val screens: List<Nothing>? = null
    }
}