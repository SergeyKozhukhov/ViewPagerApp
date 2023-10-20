package com.example.viewpagerapp.presentation.content

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.os.bundleOf
import com.example.viewpagerapp.domain.ContentId
import com.example.viewpagerapp.domain.ContentKey
import com.example.viewpagerapp.presentation.content.ui.theme.ViewPagerAppTheme
import com.example.viewpagerapp.presentation.content.view.ViewFactory

class ContentActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentId = checkNotNull(intent.getParcelableExtra<ContentId>(CURRENT_ID_KEY))
        val ids = checkNotNull(intent.getParcelableArrayListExtra<ContentId>(IDS_KEY))

        val viewFactory = ViewFactory()

        setContent {
            ViewPagerAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    ContentScreenV2(
                        currentId = currentId,
                        ids = ids,
                        viewFactory = viewFactory,
                        onCloseClick = { finish() }
                    )
                }
            }
        }
    }

    companion object {

        private const val CURRENT_ID_KEY = "CURRENT_ID_KEY"
        private const val IDS_KEY = "IDS_KEY"

        fun newIntent(context: Context, key: ContentKey, ids: List<ContentKey>) =
            Intent(context, ContentActivity::class.java).apply {
                putExtras(
                    bundleOf(
                        CURRENT_ID_KEY to ContentId(key.id, key.subId),
                        IDS_KEY to ids.map { ContentId(it.id, it.subId) },
                    )
                )
            }
    }
}