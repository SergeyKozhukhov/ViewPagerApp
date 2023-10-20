package com.example.viewpagerapp.presentation.content.develop

import com.example.viewpagerapp.domain.content.Content

sealed interface ContentItemState {

    object Idle : ContentItemState
    object Loading : ContentItemState
    data class Success(val item: Content) : ContentItemState
    data class Error(val e: Exception) : ContentItemState
}