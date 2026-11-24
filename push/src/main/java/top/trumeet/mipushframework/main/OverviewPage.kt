package top.trumeet.mipushframework.main

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaomi.xmsf.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.trumeet.mipush.provider.entities.RegisteredApplication
import top.trumeet.mipushframework.main.subpage.ApplicationPageOperation
import top.trumeet.mipushframework.component.StyleActionRow
import top.trumeet.mipushframework.component.StyleMetric
import top.trumeet.mipushframework.component.StylePageHeading
import top.trumeet.mipushframework.component.StylePanel
import top.trumeet.mipushframework.component.StyleSectionTitle
import top.trumeet.ui.theme.LocalUiStyle
import top.trumeet.ui.theme.UiStyle

private data class OverviewStats(
    val total: Int,
    val registered: Int,
    val unregistered: Int,
    val waiting: Int
)

@Composable
internal fun Overview(
    onOpenApps: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val isMiuix = LocalUiStyle.current == UiStyle.MIUIX
    var stats by remember { mutableStateOf<OverviewStats?>(null) }

    LaunchedEffect(Unit) {
        stats = withContext(Dispatchers.IO) {
            val apps = ApplicationPageOperation.getMiPushApplications().res
            OverviewStats(
                total = apps.size,
                registered = apps.count {
                    it.registeredType == RegisteredApplication.RegisteredType.Registered
                },
                unregistered = apps.count {
                    it.registeredType == RegisteredApplication.RegisteredType.Unregistered
                },
                waiting = apps.count {
                    it.registeredType == RegisteredApplication.RegisteredType.NotRegistered
                }
            )
        }
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(if (isMiuix) 18.dp else 14.dp)
    ) {
        StylePageHeading(
            title = "运行概览",
            subtitle = "O Push  ·  realme UI / ColorOS"
        )

        StylePanel {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("应用接入", style = MaterialTheme.typography.titleLarge)
                    Text(
                        if (stats == null) "正在读取应用注册状态"
                        else "已识别 ${stats!!.total} 个推送应用",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    stats?.total?.toString() ?: "—",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (stats != null) {
                val total = stats!!.total.coerceAtLeast(1)
                LinearProgressIndicator(
                    progress = stats!!.registered.toFloat() / total,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    StyleMetric(
                        "已注册",
                        stats!!.registered.toString(),
                        Modifier.weight(1f),
                        MaterialTheme.colorScheme.primary
                    )
                    StyleMetric(
                        "待注册",
                        stats!!.waiting.toString(),
                        Modifier.weight(1f),
                        MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    StyleMetric(
                        "异常",
                        stats!!.unregistered.toString(),
                        Modifier.weight(1f),
                        MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Column(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StyleSectionTitle("快捷入口", "直接进入常用管理页面")
            StyleActionRow("应用管理", "查看应用识别、注册状态与接入情况", onOpenApps)
            StyleActionRow("推送事件", "检索消息到达记录，查看事件详情", onOpenEvents)
            StyleActionRow("设置", "服务参数、系统适配与通知选项", onOpenSettings)
        }

        StylePanel {
            StyleSectionTitle("设备与版本")
            InfoLine("管理端版本", BuildConfig.VERSION_NAME)
            InfoLine("Android 版本", Build.VERSION.RELEASE ?: "未知")
            InfoLine("设备型号", Build.MODEL ?: "未知")
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
