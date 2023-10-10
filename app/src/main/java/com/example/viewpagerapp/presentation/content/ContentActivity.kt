package com.example.viewpagerapp.presentation.content

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import com.example.viewpagerapp.presentation.content.ui.theme.ViewPagerAppTheme

class ContentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentId = intent.getIntExtra(CURRENT_ID_KEY, -1)
        val ids = checkNotNull(intent.getIntArrayExtra(IDS_KEY))

        setContent {
            ViewPagerAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ContentScreen(currentId = currentId, ids = ids, onCloseClick = { finish() })
                }
            }
        }
    }

    companion object {

        private const val CURRENT_ID_KEY = "CURRENT_ID_KEY"
        private const val IDS_KEY = "IDS_KEY"

        fun newIntent(context: Context, id: Int, ids: IntArray) =
            Intent(context, ContentActivity::class.java).apply {
                putExtras(
                    bundleOf(
                        CURRENT_ID_KEY to id,
                        IDS_KEY to ids,
                    )
                )
            }
    }
}