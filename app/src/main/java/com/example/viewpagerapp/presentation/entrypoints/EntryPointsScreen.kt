package com.example.viewpagerapp.presentation.entrypoints

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.viewpagerapp.R

@Composable
fun EntryPointsScreen(
    viewModel: EntryPointsViewModel = viewModel(factory = EntryPointsViewModel.Factory),
    onItemClick: (Int, IntArray) -> Unit
) {
    LaunchedEffect(key1 = true) {
        viewModel.getEntryPoints()
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading) {
        LoadingState()
    }

    if (uiState.entryPoints.isNotEmpty()) {
        LazyRow() {
            items(uiState.entryPoints) { entryPoint ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(200.dp)
                        .border(Dp(4f), Color.Black)
                        .clickable {
                            onItemClick.invoke(
                                entryPoint.id,
                                uiState.entryPoints
                                    .map { it.id }
                                    .toIntArray()
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(entryPoint.image)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .error(R.drawable.ic_launcher_background)
                            .diskCachePolicy(CachePolicy.DISABLED)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}


@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}