package com.example.viewpagerapp.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.viewpagerapp.data.DataSource
import com.example.viewpagerapp.presentation.content.ContentActivity
import com.example.viewpagerapp.presentation.entrypoints.EntryPointsScreen
import com.example.viewpagerapp.presentation.main.ui.theme.ViewPagerAppTheme
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ViewPagerAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LaunchedEffect(key1 = true) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val res = DataSource(this@MainActivity.applicationContext, ObjectMapper()).getContent2(2)
                        }
                    }

                    // VideoPlayer(uri = Uri.parse("https://samplelib.com/lib/preview/mp4/sample-5s.mp4"))
                    EntryPointsScreen(onItemClick = { currentId, ids ->
                        startActivity(
                            ContentActivity.newIntent(
                                this.applicationContext,
                                currentId,
                                ids
                            )
                        )
                    })
                }
            }
        }
    }
}