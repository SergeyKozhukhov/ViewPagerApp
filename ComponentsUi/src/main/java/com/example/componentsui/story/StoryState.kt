package com.example.componentsui.story

data class StoryState(
    val currentScreenIndex: Int = 0,
    val previousScreenIndex: Int = -1,
    val screenProgress: Float = 0f, // [0, 1]
    val event: StoryScreenEvent = StoryScreenEvent.IDLE,
    val borderEvent: StoryScreenBorderEvent = StoryScreenBorderEvent.IDLE,
)