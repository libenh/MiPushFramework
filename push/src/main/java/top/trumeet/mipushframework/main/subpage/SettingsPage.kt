package top.trumeet.mipushframework.main.subpage

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.xiaomi.xmsf.R
import com.xiaomi.xmsf.SettingUtils
import top.trumeet.mipushframework.MainPageOperation
import top.trumeet.mipushframework.component.SettingsGroup
import top.trumeet.mipushframework.component.SettingsItem
import top.trumeet.mipushframework.component.StylePageHeading
import top.trumeet.mipushframework.main.HelpPage
import top.trumeet.ui.theme.LocalUiStyle
import top.trumeet.ui.theme.Theme
import top.trumeet.ui.theme.UiStyle

@Composable
fun Settings(onUiStyleChange: (UiStyle) -> Unit = {}) {
    Theme {
        Surface(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            color = MaterialTheme.colorScheme.background
        ) {
            SettingsScreen(onUiStyleChange)
        }
    }
}

@Composable
private fun SettingsScreen(onUiStyleChange: (UiStyle) -> Unit) {
    Column {
        StylePageHeading("设置", "服务运行、系统适配与管理端偏好")
        AppearanceBlock(onUiStyleChange)
        ServiceBlock()
        OSystemBlock()
        NotificationBlock()
        MaintenanceBlock()
        AboutBlock()
    }
}

@Composable
private fun AppearanceBlock(onUiStyleChange: (UiStyle) -> Unit) {
    val context = LocalContext.current
    val current = LocalUiStyle.current
    SettingsGroup(title = "界面风格") {
        SettingsItem(
            title = "界面风格",
            summary = "当前：${current.label}\nMiuix 偏小米系；Material 偏原生材质\n点击切换"
        ) {
            val next = if (current == UiStyle.MIUIX) UiStyle.MATERIAL else UiStyle.MIUIX
            UiStyle.save(context, next)
            onUiStyleChange(next)
        }
    }
}

@Composable
private fun ServiceBlock() {
    val context = LocalContext.current
    SettingsGroup(title = stringResource(R.string.o_settings_service)) {
        SetConfigurationsDirectory()
        SetXMPPServer(context)
    }
}

@Composable
private fun OSystemBlock() {
    val context = LocalContext.current
    SettingsGroup(title = stringResource(R.string.o_settings_adaptation)) {
        SettingsItem(
            title = stringResource(R.string.settings_start_foreground_service),
            summary = stringResource(R.string.settings_start_foreground_service_summary)
        ) {
            SettingUtils.startMiPushServiceAsForegroundService(context)
        }
        SettingsItem(
            title = stringResource(R.string.o_continuous_enabled),
            summary = stringResource(R.string.o_continuous_summary),
            key = "FluidCloudEnabled",
            defaultValue = false
        )
        SettingsItem(
            title = stringResource(R.string.pref_title_access_mode),
            summary = stringResource(R.string.pref_summary_access_mode),
            key = "AccessMode",
            values = stringArrayResource(R.array.pref_title_access_mode_list_titles),
            defaultValue = "0"
        )
    }
}

@Composable
private fun NotificationBlock() {
    val context = LocalContext.current
    SettingsGroup(title = stringResource(R.string.o_settings_notifications)) {
        SettingsItem(
            title = stringResource(R.string.settings_notify_on_register),
            key = "NotificationOnRegister",
            defaultValue = false
        )
        SettingsItem(
            title = stringResource(R.string.settings_show_all_events),
            key = "ShowAllEvents",
            defaultValue = false
        )
        SettingsItem(
            title = stringResource(R.string.settings_debug_mode),
            summary = stringResource(R.string.settings_debug_mode_summary),
            key = "DebugMode",
            defaultValue = false
        )
        SettingsItem(
            title = stringResource(R.string.settings_mock_notification),
            summary = stringResource(R.string.settings_mock_notification_summary)
        ) {
            SettingUtils.notifyMockNotification(context)
        }
    }
}

@Composable
private fun MaintenanceBlock() {
    val context = LocalContext.current
    SettingsGroup(title = stringResource(R.string.o_settings_maintenance)) {
        SettingsItem(
            title = stringResource(R.string.settings_clear_history),
            summary = stringResource(R.string.settings_clear_history_summary)
        ) {
            SettingUtils.clearHistory(context)
        }
        SettingsItem(
            title = stringResource(R.string.settings_clear_log),
            summary = stringResource(R.string.settings_clear_log_summary)
        ) {
            SettingUtils.clearLog(context)
        }
        SettingsItem(title = stringResource(R.string.try_to_force_register_all_applications)) {
            SettingUtils.tryForceRegisterAllApplications()
            Toast.makeText(context, R.string.o_register_requested, Toast.LENGTH_SHORT).show()
        }
        SettingsItem(
            title = stringResource(R.string.settings_get_log),
            summary = stringResource(R.string.settings_get_log_summary)
        ) {
            SettingUtils.shareLogs(context)
        }
    }
}

@Composable
private fun AboutBlock() {
    val context = LocalContext.current
    val mainPageOperation = MainPageOperation(context)
    SettingsGroup(title = stringResource(R.string.action_about)) {
        SettingsItem(title = stringResource(R.string.helplib_title)) {
            context.startActivity(Intent(context, HelpPage::class.java))
        }
        SettingsItem(title = stringResource(R.string.action_update)) {
            mainPageOperation.gotoGitHubReleasePage()
            Toast.makeText(context, R.string.update_toast, Toast.LENGTH_LONG).show()
        }
        SettingsItem(title = stringResource(R.string.action_about)) {
            mainPageOperation.showAboutDialog()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetXMPPServer(context: Context) {
    var text by remember { mutableStateOf(SettingUtils.getXMPPServer(context) ?: "") }
    SettingsItem(
        title = stringResource(R.string.settings_XMPP_server),
        summary = stringResource(R.string.settings_XMPP_server_summary) +
            "\n" + (SettingUtils.getXMPPServer(context) ?: ""),
        confirmButton = { dismiss ->
            TextButton(onClick = {
                SettingUtils.setXMPPServer(context, text)
                SettingUtils.sendXMPPReconnectRequest(context)
                dismiss()
            }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        onDismiss = { text = SettingUtils.getXMPPServer(context) ?: "" },
        content = {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text(SettingUtils.getXMPPServerHint()) },
                singleLine = true
            )
        }
    )
}

@Composable
private fun SetConfigurationsDirectory() {
    val context = LocalContext.current
    var selectedDirectoryUri by remember {
        mutableStateOf(SettingUtils.getConfigurationDirectory(context))
    }
    val openDocumentTreeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedDirectoryUri = uri
            SettingUtils.setConfigurationDirectory(context, uri)
        }
    }
    SettingsItem(
        title = stringResource(R.string.settings_configuration_directory),
        summary = selectedDirectoryUri?.toString()
    ) {
        openDocumentTreeLauncher.launch(null)
    }
}
