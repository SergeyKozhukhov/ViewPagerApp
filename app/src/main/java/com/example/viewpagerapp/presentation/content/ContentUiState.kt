package com.example.viewpagerapp.presentation.content

sealed interface ContentUiState {
    object IDLE : ContentUiState
    object Process : ContentUiState
    object Finish : ContentUiState
}