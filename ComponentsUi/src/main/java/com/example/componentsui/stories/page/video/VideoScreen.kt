package com.example.componentsui.stories.page.video

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.componentsui.stories.elements.VideoPlayer
import com.example.componentsui.stories.page.VideoPage

// https://www.goodrequest.com/blog/jetpack-compose-basics-how-to-use-and-implement-the-exoplayer-library-for-video-playing
@Composable
fun VideoScreen(
    videoPage: VideoPage,
    isActive: Boolean,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = videoPage.title)
        VideoPlayer(uri = videoPage.video, isActive)
    }
}