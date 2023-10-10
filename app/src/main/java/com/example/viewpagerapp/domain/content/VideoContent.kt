package com.example.viewpagerapp.domain.content

data class VideoContent(
    override val id: Int,
    val video: String,
    val title: String
) : Content()
