package com.example.componentsui.standard

import com.example.componentsui.stories.page.PageState

data class StandardStoryPage(
    override val screens: List<Screen>
) : PageState<StandardStoryPage.Screen> {

    sealed interface Screen

    data class Image(
        val image: String,
        val title: String
    ) : Screen

    data class Video(
        val video: String,
        val title: String
    ) : Screen
}
