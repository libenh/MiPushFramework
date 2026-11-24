package top.trumeet.mipushframework.main.subpage

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xiaomi.xmsf.BuildConfig
import com.xiaomi.xmsf.R
import com.xiaomi.xmsf.SettingUtils
import top.trumeet.mipushframework.component.SettingsGroup
import top.trumeet.mipushframework.component.SettingsItem
import top.trumeet.mipushframework.main.HelpPage
import top.trumeet.ui.theme.LocalUiStyle
import top.trumeet.ui.theme.Theme
import top.trumeet.ui.theme.UiStyle

private const val REPO_URL = "https://github.com/libenh/MiPushFramework"
private const val RELEASES_URL = "https://github.com/libenh/MiPushFramework/releases"
private const val ACTIONS_URL = "https://github.com/libenh/MiPushFramework/actions"
private const val ISSUES_URL = "https://github.com/libenh/MiPushFramework/issues"
private const val UPSTREAM_URL = "https://github.com/NihilityT/MiPushFramework"

@Composable
fun Settings(onUiStyleChange: (UiStyle) -> Unit = {}) {
    Theme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(Modifier.padding(bottom = 28.dp)) {
                AboutHeroCard()
                AppearanceBlock(onUiStyleChange)
                ServiceBlock()
                OSystemBlock()
                NotificationBlock()
                MaintenanceBlock()
                AboutLinksBlock()
            }
        }
    }
}

/** 顶部关于信息卡 */
@Composable
private fun AboutHeroCard() {
    val context = LocalContext.current
    val versionLine = "${BuildConfig.VERSION_NAME} · ${BuildConfig.FLAVOR} · ${BuildConfig.BUILD_TYPE}"
    val detail = buildString {
        appendLine("versionName: ${BuildConfig.VERSION_NAME}")
        appendLine("versionCode: ${BuildConfig.VERSION_CODE}")
        appendLine("flavor: ${BuildConfig.FLAVOR}")
        appendLine("buildType: ${BuildConfig.BUILD_TYPE}")
        appendLine("sdk: ${Build.VERSION.SDK_INT}")
        appendLine("device: ${Build.MANUFACTURER} ${Build.MODEL}")
        appendLine("android: ${Build.VERSION.RELEASE}")
    }.trim()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Mi",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "ColorOS 17 / O 系专用推送服务",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.55f))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    versionLine,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "versionCode ${BuildConfig.VERSION_CODE} · SDK ${Build.VERSION.SDK_INT}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SmallActionChip(
                    label = "复制版本",
                    modifier = Modifier.weight(1f)
                ) {
                    copyText(context, detail)
                    Toast.makeText(context, "已复制版本信息", Toast.LENGTH_SHORT).show()
                }
                SmallActionChip(
                    label = "检查更新",
                    modifier = Modifier.weight(1f)
                ) {
                    openUrl(context, RELEASES_URL)
                }
            }
        }
    }
}

@Composable
private fun SmallActionChip(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(
            label,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun AppearanceBlock(onUiStyleChange: (UiStyle) -> Unit) {
    val context = LocalContext.current
    val current = LocalUiStyle.current
    SettingsGroup(title = "界面") {
        SettingsItem(
            title = "界面风格",
            summary = "当前：${current.label}\nMiuix 偏小米系，Material 偏原生材质"
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
        SettingsItem(
            title = stringResource(R.string.try_to_force_register_all_applications),
            summary = "清理缓存并对已接入应用发起强制注册"
        ) {
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

/** 关于 / 链接：分组清晰、跳转本仓库 */
@Composable
private fun AboutLinksBlock() {
    val context = LocalContext.current

    SettingsGroup(title = "帮助与反馈") {
        SettingsItem(
            title = stringResource(R.string.helplib_title),
            summary = "FAQ、常见问题与联系方式"
        ) {
            context.startActivity(Intent(context, HelpPage::class.java))
        }
        SettingsItem(
            title = "提交 Issue",
            summary = "报 bug、提需求（GitHub）"
        ) {
            openUrl(context, ISSUES_URL)
        }
    }

    SettingsGroup(title = "开源与更新") {
        SettingsItem(
            title = "本仓库",
            summary = "libenh/MiPushFramework\nC17 适配主线"
        ) {
            openUrl(context, REPO_URL)
        }
        SettingsItem(
            title = "CI 构建",
            summary = "下载最新 MiPush-C17 产物"
        ) {
            openUrl(context, ACTIONS_URL)
        }
        SettingsItem(
            title = "Releases",
            summary = "发布版本与更新说明"
        ) {
            openUrl(context, RELEASES_URL)
        }
        SettingsItem(
            title = "上游项目",
            summary = "NihilityT/MiPushFramework"
        ) {
            openUrl(context, UPSTREAM_URL)
        }
    }

    SettingsGroup(title = "关于") {
        SettingsItem(
            title = "许可协议",
            summary = "GNU GPL v3.0"
        ) {
            openUrl(context, "$REPO_URL/blob/master/LICENSE")
        }
        SettingsItem(
            title = "设备信息",
            summary = "${Build.MANUFACTURER} ${Build.MODEL} · Android ${Build.VERSION.RELEASE}"
        ) {
            val info = "${Build.MANUFACTURER} ${Build.MODEL}\nAndroid ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
            copyText(context, info)
            Toast.makeText(context, "已复制设备信息", Toast.LENGTH_SHORT).show()
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
        summary = selectedDirectoryUri?.toString() ?: "未设置"
    ) {
        openDocumentTreeLauncher.launch(null)
    }
}

private fun openUrl(context: Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: Exception) {
        Toast.makeText(context, "无法打开链接", Toast.LENGTH_SHORT).show()
    }
}

private fun copyText(context: Context, text: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("mipush", text))
}
