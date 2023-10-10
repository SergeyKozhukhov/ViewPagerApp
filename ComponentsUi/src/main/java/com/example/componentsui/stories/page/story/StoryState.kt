package com.example.componentsui.stories.page.story

data class StoryState(
    val screenIndex: Int = 0,
    val progress: Float = 0f, // [0, 1]
    val isNextScreenTap: Boolean = false,
    val isPreviousScreenTap: Boolean = false,
    val isNextScreenTime: Boolean = false,
    val isNextPageTap: Boolean = false,
    val isPreviousPageTap: Boolean = false,
    val isNextPageTime: Boolean = false,
)