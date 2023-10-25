package com.example.componentsui.standard.defaults

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable

class RenderDefault(
    val idleContent: @Composable BoxScope.() -> Unit,
    val loadingContent: @Composable BoxScope.() -> Unit,
    val successContent: @Composable BoxScope.() -> Unit,
)