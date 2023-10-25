package com.example.componentsui.other

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.componentsui.other.elements.VideoPlayer

// https://www.goodrequest.com/blog/jetpack-compose-basics-how-to-use-and-implement-the-exoplayer-library-for-video-playing
@Composable
fun VideoScreen(
    uri: Uri,
    title: String,
    isActive: Boolean,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = title)
        VideoPlayer(uri = uri, isActive)
    }
}