package com.example.componentsui.stories.page

import android.net.Uri

sealed interface ContentPage

data class StoryPage(
    val screens: List<Screen>
) : ContentPage {

    sealed interface Screen

    data class Image(
        val image: String,
        val title: String
    ) : Screen

    data class Video(
        val video: String,
        val title: String
    ) : Screen

    interface Custom : Screen
}

data class VideoPage(
    val video: Uri,
    val title: String
) : ContentPage

interface CustomPage : ContentPage

//         val content: @Composable BoxScope.() -> Unit


data class StoryPageV2<T>(
    val screens: List<T>
)