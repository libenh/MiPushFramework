package top.trumeet.mipushframework.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import top.trumeet.ui.theme.LocalUiStyle
import top.trumeet.ui.theme.UiStyle

/**
 * Shared visual language for the manager. Material uses Material 3 surfaces;
 * Miuix uses Foundation-first grouped surfaces and its own spacing rhythm.
 * These components contain presentation only and never own push state.
 */
@Composable
fun MiuixSwitch(checked: Boolean, enabled: Boolean = true) {
    val thumbOffset by animateDpAsState(if (checked) 16.dp else 2.dp)
    Box(
        Modifier.size(width = 42.dp, height = 26.dp)
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
            Modifier.align(Alignment.CenterStart)
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
    Column(
        Modifier.fillMaxWidth().padding(
            horizontal = if (isMiuix) 20.dp else 16.dp,
            vertical = if (isMiuix) 18.dp else 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(if (isMiuix) 7.dp else 4.dp)
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
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StyleSectionTitle(title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    val isMiuix = LocalUiStyle.current == UiStyle.MIUIX
    Column(
        modifier.fillMaxWidth().padding(
            horizontal = if (isMiuix) 4.dp else 2.dp,
            vertical = if (isMiuix) 5.dp else 2.dp
        ),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            title,
            style = if (isMiuix) MaterialTheme.typography.titleLarge
            else MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (!subtitle.isNullOrBlank()) {
            Text(subtitle, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun StylePanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val isMiuix = LocalUiStyle.current == UiStyle.MIUIX
    if (isMiuix) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.10f))
        ) {
            Column(
                Modifier.padding(horizontal = 18.dp, vertical = 17.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                content = content
            )
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                Modifier.padding(horizontal = 18.dp, vertical = 17.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                content = content
            )
        }
    }
}

@Composable
fun StyleMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.primary
) {
    val isMiuix = LocalUiStyle.current == UiStyle.MIUIX
    Column(
        modifier = modifier
            .clip(if (isMiuix) RoundedCornerShape(18.dp) else MaterialTheme.shapes.medium)
            .background(
                if (isMiuix) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            )
            .padding(horizontal = if (isMiuix) 14.dp else 12.dp, vertical = 11.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(value, style = MaterialTheme.typography.headlineSmall, color = valueColor)
        Text(label, style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1,
            overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun StyleStatusPill(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    val isMiuix = LocalUiStyle.current == UiStyle.MIUIX
    Box(
        modifier.clip(CircleShape)
            .background(color.copy(alpha = if (isMiuix) 0.13f else 0.10f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall, color = color, maxLines = 1)
    }
}

@Composable
fun StyleActionRow(title: String, summary: String, onClick: () -> Unit) {
    val isMiuix = LocalUiStyle.current == UiStyle.MIUIX
    if (isMiuix) {
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable(onClick = onClick)
                .padding(horizontal = 18.dp, vertical = 17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(summary, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("›", style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        Row(
            Modifier.fillMaxWidth().clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(summary, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("›", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun StyleEmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    val isMiuix = LocalUiStyle.current == UiStyle.MIUIX
    Column(
        modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 42.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (isMiuix) 9.dp else 6.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface)
        Text(message, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
