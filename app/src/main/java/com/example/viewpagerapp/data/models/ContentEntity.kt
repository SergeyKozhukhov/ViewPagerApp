package com.example.viewpagerapp.data.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName

// https://habr.com/ru/companies/otus/articles/593941/
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(
        value = StoryContentEntity::class,
        name = StoryContentEntity.TYPE
    ), JsonSubTypes.Type(
        value = VideoContentEntity::class,
        name = VideoContentEntity.TYPE
    )
)
sealed class ContentEntity {
    abstract val id: Int
    abstract val subId: Int
    abstract val type: String
}

@JsonTypeName("StoryType")
data class StoryContentEntity(
    @JsonProperty("id") override val id: Int,
    @JsonProperty("subId") override val subId: Int,
    @JsonProperty("items") val items: List<Item>
) : ContentEntity() {

    override val type = TYPE

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true
    )
    @JsonSubTypes(
        JsonSubTypes.Type(
            value = Image::class,
            name = Image.TYPE
        ), JsonSubTypes.Type(
            value = Video::class,
            name = Video.TYPE
        )
    )
    sealed interface Item {
        val type: String
    }

    data class Image(
        @JsonProperty("image") val image: String,
        @JsonProperty("title") val title: String
    ) : Item {

        override val type = TYPE

        companion object {
            const val TYPE = "image"
        }
    }

    data class Video(
        @JsonProperty("video") val video: String,
        @JsonProperty("title") val title: String
    ) : Item {

        override val type = TYPE

        companion object {
            const val TYPE = "video"
        }
    }


    companion object {
        const val TYPE = "story"
    }
}

@JsonTypeName("VideoType")
data class VideoContentEntity(
    @JsonProperty("id") override val id: Int,
    @JsonProperty("subId") override val subId: Int,
    @JsonProperty("video") val video: String,
    @JsonProperty("title") val title: String
) : ContentEntity() {

    override val type = TYPE

    companion object {
        const val TYPE = "video"
    }
}
