package com.example.viewpagerapp.data.converters

import com.example.viewpagerapp.data.models.ContentEntity
import com.example.viewpagerapp.data.models.StoryContentEntity
import com.example.viewpagerapp.data.models.VideoContentEntity
import com.example.viewpagerapp.domain.content.Content
import com.example.viewpagerapp.domain.content.StoryContent
import com.example.viewpagerapp.domain.content.VideoContent

class ContentConverter {

    fun convert(list: List<ContentEntity>) = list.map { convert(it) }

    fun convert(source: ContentEntity): Content = when (source) {
        is StoryContentEntity -> convertStoryImage(source)
        is VideoContentEntity -> convertVideo(source)
    }


    private fun convertStoryImage(source: StoryContentEntity) =
        StoryContent(id = source.id, subId = source.subId, items = source.items.map { item ->
            when (item) {
                is StoryContentEntity.Image -> {
                    StoryContent.Story(
                        image = item.image, title = item.title
                    )
                }

                is StoryContentEntity.Video -> {
                    StoryContent.Video(
                        video = item.video, title = item.title
                    )
                }
            }
        })

    private fun convertVideo(source: VideoContentEntity) = VideoContent(
        id = source.id, subId = source.subId, video = source.video, title = source.title
    )
}