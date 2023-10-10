package com.example.viewpagerapp.legacy

import com.example.viewpagerapp.data.models.ContentEntity
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName

// https://www.ianarbuckle.dev/compose-paging
// https://scribe.rip/m/global-identity-2?redirectUrl=https%3A%2F%2Fproandroiddev.com%2Fpagination-in-jetpack-compose-with-and-without-paging-3-e45473a352f4#622d
// https://scribe.rip/m/global-identity-2?redirectUrl=https%3A%2F%2Fproandroiddev.com%2Fandroid-touch-system-part-4-gesture-handling-modifiers-in-jetpack-compose-d7600a8a1ec9


/*@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InnerContent(story: Story) {
    val innerPagerState = rememberPagerState(pageCount = { story.screens.size })
    HorizontalPager(
        state = innerPagerState,
        userScrollEnabled = false,
        modifier = Modifier.pointerInput(Unit) {
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
    ) { innerPosition ->
        Text(
            text = story.screens[innerPosition].title,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}*/

/*

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RowScope.ListOfIndicators(
    currentPosition: Int,
    isActive: Boolean,
    isCurrent: Boolean,
    numberOfPages: Int,
    indicatorModifier: Modifier,
    spaceBetweenIndicator: Dp,
    onTimerNext: () -> Unit,
    viewModel: ProgressViewModel
) {
    if (isActive) {

        val state by viewModel.uiState

        LaunchedEffect(currentPosition) {
            if (isActive) {
                Log.d(
                    TAG,
                    "ListOfIndicators LaunchedEffect: $currentPosition - ${state.toString()}"
                )
                viewModel.startTimer()
            }
        }

        if (state.isEnd) {
            Log.d(TAG, "ListOfIndicators: ${state.toString()}")
            onTimerNext.invoke()
        }

        for (index in 0 until numberOfPages) {
            LinearIndicator(
                progress = if (isCurrent) state.progress else 0f,
                modifier = indicatorModifier.weight(1f),
                isActive = currentPosition == index
            )

            Spacer(modifier = Modifier.padding(spaceBetweenIndicator))
        }
    } else {
        for (index in 0 until numberOfPages) {
            LinearIndicator(
                progress = 0f,
                modifier = indicatorModifier.weight(1f),
                isActive = currentPosition == index
            )

            Spacer(modifier = Modifier.padding(spaceBetweenIndicator))
        }
    }
}*/


/*ListOfIndicators(
           currentPosition = currentInnerPosition.intValue,
           isActive = isActive,
           isCurrent = false,
           numberOfPages = story.screens.size,
           indicatorModifier = Modifier
               .padding(top = 12.dp, bottom = 12.dp)
               .clip(RoundedCornerShape(12.dp)),
           spaceBetweenIndicator = 4.dp,
           onTimerNext = {
               if (currentInnerPosition.intValue < story.screens.size - 1) {
                   currentInnerPosition.intValue++
               } else {
                   onNext.invoke()
               }
           },
           viewModel = viewModel
       )*/


/*
.pointerInput(Unit) {
    detectTapGestures(
        onDoubleTap = { offset ->
            Log.d(TAG, "Out: onDoubleTap $offset")
        },
        onLongPress = { offset ->
            Log.d(TAG, "Out: onLongPress $offset")
        },
        onPress = { offset ->
            Log.d(TAG, "Out: onPress $offset")
        },
        onTap = { offset ->
            Log.d(TAG, "Out: onTap $offset")
        })
}*/

/*


fun startTimer() {
    Log.d(com.example.viewpagerapp.stories.progress.TAG, "startTimer: ")
    viewModelScope.coroutineContext.cancelChildren()

    progress = 0.0f
    uiState.value = TempProgressState(
        isIdle = true, progress = progress, isEnd = false
    )

    viewModelScope.launch {
        while (progress < 1f && isActive) {
            progress += 0.01f
            uiState.value = TempProgressState(
                isIdle = false, progress = progress, isEnd = false
            )
            delay(ProgressViewModel.delayInMillis.toLong())
        }
        uiState.value = TempProgressState(
            isIdle = false, progress = progress, isEnd = true
        )

        progress = 0f

        if (positionState.intValue < count - 1) {
            positionState.intValue++
        } else {
            nextState.value = true
        }
    }
}*/

/*
val story = stories[position]
val storyState = storiesState[position]
when (storyState) {
    is StoryState.Idle -> Text("Idle")
    is StoryState.Loading -> Text("Loading")
    is StoryState.Success -> {
        Text("Success")
        *//*StoryScreen(
            position = position,
            story = story,
            pagerState,
            onNext = {
                Log.d(TAG, "StoriesScreen: animateScrollToPage ${position + 1}")
                scope.launch { pagerState.animateScrollToPage(position + 1) }
            },
            onPrevious = {
                scope.launch { pagerState.animateScrollToPage(position - 1) }
            })*//*

    }

    is StoryState.Error -> Text("Error")
}*/


/*@JsonTypeName("StoryVideoType")
data class StoryVideoContentEntity(
    @JsonProperty("id") override val id: Int,
    @JsonProperty("items") val items: List<Item>
) : ContentEntity() {

    override val type = TYPE

    data class Item(
        @JsonProperty("video") val video: String,
        @JsonProperty("title") val title: String
    )

    companion object {
        const val TYPE = "storyImage"
    }
}*/
