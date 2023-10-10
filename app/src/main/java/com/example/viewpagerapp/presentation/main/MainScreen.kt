package com.example.viewpagerapp.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.viewpagerapp.presentation.entrypoints.EntryPointsViewModel

@Composable
fun MainScreen(
    entryPointsViewModel: EntryPointsViewModel = viewModel(factory = EntryPointsViewModel.Factory),
) {
    LaunchedEffect(key1 = true) {
        entryPointsViewModel.getEntryPoints()
    }

    val uiState by entryPointsViewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading) {
        LoadingState()
    }

    if (uiState.entryPoints.isNotEmpty()) {

    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}