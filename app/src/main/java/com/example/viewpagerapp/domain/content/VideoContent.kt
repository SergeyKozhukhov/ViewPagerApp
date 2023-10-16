package com.example.viewpagerapp.domain.content

data class VideoContent(
    override val id: Int,
    override val subId: Int,
    val video: String,
    val title: String
) : Content()
