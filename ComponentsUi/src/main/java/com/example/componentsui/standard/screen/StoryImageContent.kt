package com.example.componentsui.standard.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.componentsui.R


// https://github.com/coil-kt/coil#jetpack-compose
@Composable
fun StoryImageContent(
    image: String,
    title: String,
    onContentLoaded: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_background)
                .diskCachePolicy(CachePolicy.DISABLED)
                // .networkCachePolicy(CachePolicy.DISABLED)
                .build(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            onSuccess = {
                Log.d("ContentViewModel", "StoryImageContent: $title")
                onContentLoaded.invoke()
            },
            onError = { onContentLoaded.invoke() }
        )
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        val context = LocalContext.current
        Button(onClick = {
            Toast.makeText(context, "Click 1", Toast.LENGTH_SHORT).show()
        }, Modifier.align(Alignment.BottomStart)) {
            Text(text = "Click me")
        }

        Button(onClick = {
            Toast.makeText(context, "Click 2", Toast.LENGTH_SHORT).show()
        }, Modifier.align(Alignment.BottomEnd)) {
            Text(text = "Click me")
        }
    }
}
/*

@Composable
fun loadImage(url: String, onLoaded: (img: Drawable) -> Unit) {
    val imageLoader = ImageLoader(LocalContext.current)
    val request = ImageRequest.Builder(LocalContext.current)
        .data(url)
        .target { drawable -> onLoaded.invoke(drawable) }
        .build()
    val disposable = imageLoader.enqueue(request)
}

@Composable
fun StoryImageContent2(
    image: Drawable,
    title: String,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_background)
                .diskCachePolicy(CachePolicy.DISABLED)
                // .networkCachePolicy(CachePolicy.DISABLED)
                .build(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}*/
