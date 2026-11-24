package top.trumeet.mipushframework.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import top.trumeet.ui.theme.LocalUiStyle
import top.trumeet.ui.theme.UiStyle

/**
 * Small primitives used by the Miuix presentation. These are deliberately built
 * from Compose Foundation rather than re-skinning Material widgets.
 */
@Composable
fun MiuixSwitch(checked: Boolean, enabled: Boolean = true) {
    val thumbOffset by animateDpAsState(if (checked) 16.dp else 2.dp)
    Box(
        Modifier
            .size(width = 42.dp, height = 26.dp)
            .clip(CircleShape)
            .background(
                when {
                    !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
                    checked -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.16f)
                }
            )
            .padding(2.dp)
    ) {
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .padding(start = thumbOffset)
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

@Composable
fun StylePageHeading(title: String, subtitle: String? = null) {
    val isMiuix = LocalUiStyle.current == UiStyle.MIUIX
    androidx.compose.foundation.layout.Column(
        Modifier.fillMaxWidth().padding(
            horizontal = if (isMiuix) 20.dp else 16.dp,
            vertical = if (isMiuix) 18.dp else 12.dp
        )
    ) {
        Text(
            title,
            style = if (isMiuix) MaterialTheme.typography.headlineMedium
            else MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                subtitle,
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StyleActionRow(title: String, summary: String, onClick: () -> Unit) {
    val isMiuix = LocalUiStyle.current == UiStyle.MIUIX
    if (isMiuix) {
        Row(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable(onClick = onClick)
                .padding(horizontal = 18.dp, vertical = 17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.layout.Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    summary,
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text("›", style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        Row(
            Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.layout.Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(summary, modifier = Modifier.padding(top = 3.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("›", style = MaterialTheme.typography.titleLarge)
        }
    }
}
