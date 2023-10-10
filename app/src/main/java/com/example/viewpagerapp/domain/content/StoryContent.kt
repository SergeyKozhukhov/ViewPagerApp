package com.example.viewpagerapp.domain.content

data class StoryContent(
    override val id: Int,
    val items: List<Item>
) : Content() {

    sealed interface Item

    data class Story(
        val image: String,
        val title: String
    ) : Item

    data class Video(
        val video: String,
        val title: String
    ) : Item
}
