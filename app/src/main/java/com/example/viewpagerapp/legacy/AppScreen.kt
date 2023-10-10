package com.example.viewpagerapp.legacy

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.random.Random

private const val TAG = "AppScreen"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppScreen() {
    // Display 10 items
    val pagerState = rememberPagerState(pageCount = {
        10
    })

    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->
            // Do something with each page change, for example:
            Log.d("Page change", "currentPage $page")
        }
    }

    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.settledPage }.collect { page ->
            // Do something with each page change, for example:
            Log.d("Page change", "settledPage $page")
        }
    }

    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.targetPage }.collect { page ->
            // Do something with each page change, for example:
            Log.d("Page change", "targetPage $page")
        }
    }
    Column {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(10.dp),
            pageSpacing = 4.dp
        ) { page ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = { offset ->
                                Log.d(TAG, "AppScreen: onDoubleTap $offset")
                            },
                            onLongPress = { offset ->
                                Log.d(TAG, "AppScreen: onLongPress $offset")
                            },
                            onPress = { offset ->
                                Log.d(TAG, "AppScreen: onPress $offset")
                            },
                            onTap = { offset ->
                                Log.d(TAG, "AppScreen: onTap $offset")
                            })
                    }
                    .background(
                        color = Color(
                            rnd
                                .nextDouble(0.0, 1.0)
                                .toFloat(),
                            rnd
                                .nextDouble(0.0, 1.0)
                                .toFloat(),
                            rnd
                                .nextDouble(0.0, 1.0)
                                .toFloat(),
                        )
                    )
                    .graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState
                                    .currentPageOffsetFraction
                                ).absoluteValue

                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Page: $page",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        Row(
            Modifier
                .height(50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(10) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(20.dp)

                )
            }
        }
        // scroll to page
        val coroutineScope = rememberCoroutineScope()
        Button(onClick = {
            coroutineScope.launch {
                // Call scroll to on pagerState
                pagerState.animateScrollToPage(5)
            }
        }, modifier = Modifier) {
            Text("Jump to Page 5")
        }
    }

}

private val rnd = Random