package top.trumeet.mipushframework.main.subpage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import top.trumeet.mipushframework.component.initIconCache
import top.trumeet.ui.theme.Theme

@Composable
fun Page(content: @Composable () -> Unit) {
    val context = LocalContext.current
    initIconCache(context)

    Theme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            color = MaterialTheme.colorScheme.background,
            content = content
        )
    }
}
