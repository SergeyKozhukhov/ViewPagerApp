package com.example.viewpagerapp.presentation.content.stories

import com.example.componentsui.stories.page.StoryPage
import com.example.viewpagerapp.domain.content.StoryContent

data class CustomStoryScreen(val item: StoryContent.Item) : StoryPage.Custom
