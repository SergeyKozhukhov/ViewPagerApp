package com.example.componentsui.stories.page.story.custom

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable

class RenderDefault(
    val idleContent: @Composable BoxScope.() -> Unit,
    val loadingContent: @Composable BoxScope.() -> Unit,
    val successContent: @Composable BoxScope.() -> Unit,
)