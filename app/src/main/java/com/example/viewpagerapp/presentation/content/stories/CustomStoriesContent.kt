package com.example.viewpagerapp.presentation.content.stories

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.componentsui.stories.page.StoryPage

@Composable
fun CustomStoriesContent(title: String, number: Int) {
    Text(text = "$title - $number")
}

class Test1 : StoryPage.Custom() {

}