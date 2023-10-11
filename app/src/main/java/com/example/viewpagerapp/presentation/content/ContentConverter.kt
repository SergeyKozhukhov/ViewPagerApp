package com.example.viewpagerapp.presentation.content

import com.example.viewpagerapp.domain.content.Content
import com.example.viewpagerapp.domain.content.StoryContent
import com.example.viewpagerapp.domain.content.VideoContent
import com.example.componentsui.stories.page.StoryPage
import com.example.viewpagerapp.presentation.content.stories.CustomStoriesPage
import com.example.viewpagerapp.presentation.content.stories.CustomStoryScreen

class ContentConverter {

    fun convert(source: Content) = when (source) {
        is StoryContent -> convertStory(source)
        is VideoContent -> convertVideo(source)
    }

    private fun convertStory(source: StoryContent) = StoryPage(
        screens = source.items.map { item ->
            CustomStoryScreen(item)
            /*when (item) {
                is StoryContent.Story -> {
                    StoryPage.Image(
                        image = item.image,
                        title = item.title
                    )
                }

                is StoryContent.Video -> {
                    StoryPage.Video(
                        video = item.video,
                        title = item.title
                    )
                }
            }*/
        }
    )

    private fun convertVideo(source: VideoContent) =
        CustomStoriesPage(source.title)

    /*VideoPage(
            video = Uri.parse(source.video),
            title = source.title
        )*/
}