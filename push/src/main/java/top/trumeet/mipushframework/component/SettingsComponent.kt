package top.trumeet.mipushframework.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.xiaomi.xmsf.R
import com.xiaomi.xmsf.utils.ConfigCenter
import top.trumeet.ui.theme.LocalUiStyle
import top.trumeet.ui.theme.UiStyle

@Composable
fun SettingsItem(
    title: String,
    summary: String? = null,
    content: (@Composable RowScope.() -> Unit)? = null,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val isMiuix = LocalUiStyle.current == UiStyle.MIUIX
    Row(
        Modifier.fillMaxWidth()
            .clip(if (isMiuix) RoundedCornerShape(16.dp) else MaterialTheme.shapes.medium)
            .clickable(onClick = onClick, enabled = enabled)
            .heightIn(min = if (summary.isNullOrEmpty()) {
                if (isMiuix) 66.dp else 56.dp
            } else {
                if (isMiuix) 78.dp else 68.dp
            })
            .padding(horizontal = if (isMiuix) 16.dp else 12.dp, vertical = 5.dp)
            .alpha(if (enabled) 1f else 0.48f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemInfo(title, summary, modifier = Modifier.weight(1f))
        content?.let { it() }
    }
}

@Composable
fun SettingsItem(title: String, summary: String, key: String, values: Array<String>, defaultValue: String) {
    SettingsItem(
        title = title,
        summary = summary,
        confirmButton = { dismiss ->
            TextButton(onClick = dismiss) { Text(stringResource(android.R.string.ok)) }
        },
        content = { dismiss -> ItemLists(key, defaultValue, values, dismiss) }
    )
}

@Composable
fun SettingsItem(
    title: String,
    summary: String,
    confirmButton: @Composable (dismiss: () -> Unit) -> Unit,
    onDismiss: (() -> Unit)? = null,
    content: @Composable (dismiss: () -> Unit) -> Unit
) {
    var shouldShowDialog by remember { mutableStateOf(false) }
    SettingsItem(title = title, summary = summary, content = {
        val hideDialog = {
            shouldShowDialog = false
            onDismiss?.invoke()
            Unit
        }
        SettingsDialog(title, shouldShowDialog, hideDialog, { confirmButton(hideDialog) }) {
            content(hideDialog)
        }
    }) { shouldShowDialog = true }
}

@Composable
fun SettingsDialog(
    title: String,
    shouldShowDialog: Boolean,
    onDismiss: () -> Unit,
    confirmButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    if (!shouldShowDialog) return
    val isMiuix = LocalUiStyle.current == UiStyle.MIUIX
    if (isMiuix) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(22.dp)) {
                    Text(title, style = MaterialTheme.typography.titleLarge)
                    Column(Modifier.padding(top = 18.dp, bottom = 12.dp)) { content() }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End) {
                        confirmButton()
                    }
                }
            }
        }
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = confirmButton,
            title = { Text(title, style = MaterialTheme.typography.titleLarge) },
            text = content,
            shape = MaterialTheme.shapes.large,
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun ItemLists(key: String, defaultValue: String, values: Array<String>, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val preferences = ConfigCenter.getSharedPreferences(context)
    val selected = (
        preferences.getString(key, defaultValue)?.toIntOrNull()
            ?: defaultValue.toIntOrNull()
            ?: 0
        ).let { if (values.isEmpty()) -1 else it.coerceIn(values.indices) }
    LazyColumn {
        itemsIndexed(values) { index, item ->
            Row(
                Modifier.fillMaxWidth().clickable {
                    preferences.edit().putString(key, index.toString()).apply()
                    onDismiss()
                }.padding(vertical = 12.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(index == selected, onClick = null)
                Spacer(Modifier.width(8.dp))
                Text(item, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable () -> Unit) {
    val isMiuix = LocalUiStyle.current == UiStyle.MIUIX
    var expanded by rememberSaveable(title) { mutableStateOf(true) }
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = if (isMiuix) 10.dp else 6.dp)) {
        Row(
            Modifier.fillMaxWidth()
                .clip(if (isMiuix) RoundedCornerShape(14.dp) else MaterialTheme.shapes.medium)
                .clickable { expanded = !expanded }
                .padding(horizontal = if (isMiuix) 4.dp else 5.dp, vertical = if (isMiuix) 14.dp else 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                style = if (isMiuix) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.weight(1f))
            Text(if (expanded) "−" else "+",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (expanded) {
            if (isMiuix) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.10f))
                ) {
                    Column(Modifier.padding(horizontal = 8.dp, vertical = 7.dp)) { content() }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(Modifier.padding(horizontal = 8.dp, vertical = 5.dp)) { content() }
                }
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    summary: String? = null,
    key: String,
    defaultValue: Boolean,
    enabled: Boolean = true,
    onClick: ((Boolean) -> Unit)? = null
) {
    val context = LocalContext.current
    val preferences = ConfigCenter.getSharedPreferences(context)
    var checked by remember { mutableStateOf(preferences.getBoolean(key, defaultValue)) }
    SettingsItem(title = title, summary = summary, checked = checked, enabled = enabled) {
        preferences.edit().putBoolean(key, !checked).apply()
        checked = !checked
        onClick?.invoke(checked)
    }
}

@Composable
fun SettingsItem(title: String, summary: String? = null, enabled: Boolean = true, checked: Boolean, onClick: () -> Unit) {
    val isMiuix = LocalUiStyle.current == UiStyle.MIUIX
    SettingsItem(title, summary, content = {
        if (isMiuix) MiuixSwitch(checked, enabled)
        else Switch(checked = checked, onCheckedChange = null, modifier = Modifier.scale(0.82f))
    }, enabled = enabled, onClick = onClick)
}

@Composable
fun ItemInfo(title: String, summary: String?, modifier: Modifier = Modifier) {
    val isMiuix = LocalUiStyle.current == UiStyle.MIUIX
    Column(modifier.padding(
        start = if (isMiuix) 8.dp else 12.dp,
        end = 12.dp,
        top = if (isMiuix) 12.dp else 9.dp,
        bottom = if (isMiuix) 12.dp else 9.dp
    )) {
        Text(title, style = if (isMiuix) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface)
        summary?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp), maxLines = 2)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InfoDialogPreview() {
    SettingsDialog(title = stringResource(R.string.pref_title_access_mode), shouldShowDialog = true, {}, {}) {
        ItemLists("AccessMode", "0", stringArrayResource(R.array.pref_title_access_mode_list_titles)) {}
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsItemPreview() {
    SettingsItem(title = stringResource(R.string.settings_start_foreground_service),
        summary = stringResource(R.string.settings_start_foreground_service_summary),
        key = "StartForegroundService", defaultValue = false, enabled = false)
}

@Preview(showBackground = true)
@Composable
fun SingleLineSettingsItemPreview() {
    SettingsItem(title = stringResource(R.string.settings_start_foreground_service)) {}
}
