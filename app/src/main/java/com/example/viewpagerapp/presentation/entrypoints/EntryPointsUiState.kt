package com.example.viewpagerapp.presentation.entrypoints

import com.example.viewpagerapp.domain.EntryPoint

data class EntryPointsUiState(
    val isLoading: Boolean = false,
    val entryPoints: List<EntryPoint> = emptyList(),
    val isError: Boolean = false
)
