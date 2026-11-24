package top.trumeet.mipushframework.main

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import top.trumeet.mipush.provider.entities.RegisteredApplication
import top.trumeet.mipushframework.component.StyleActionRow
import top.trumeet.mipushframework.component.StylePageHeading
import top.trumeet.mipushframework.main.subpage.ApplicationPageOperation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
            val list = ApplicationPageOperation.getMiPushApplications().res
            OverviewStats(
                total = list.size,
                registered = list.count { it.registeredType == RegisteredApplication.RegisteredType.Registered },
                unregistered = list.count { it.registeredType == RegisteredApplication.RegisteredType.Unregistered },
                waiting = list.count { it.registeredType == RegisteredApplication.RegisteredType.NotRegistered }
            )
        }
    }

    if (isMiuix) {
        MiuixOverview(stats, onOpenApps, onOpenEvents, onOpenSettings)
    } else {
        MaterialOverview(stats, onOpenApps, onOpenEvents, onOpenSettings)
    }
}

@Composable
private fun MiuixOverview(
    stats: OverviewStats?,
    onOpenApps: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        StylePageHeading("运行概览", "O Push · realme UI / ColorOS")
        Surface(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("应用接入", style = MaterialTheme.typography.titleLarge)
                        Text("当前识别到的推送应用", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(stats?.total?.toString() ?: "—",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MiuixMetric("已注册", stats?.registered, Modifier.weight(1f),
                        MaterialTheme.colorScheme.primary)
                    MiuixMetric("待注册", stats?.waiting, Modifier.weight(1f),
                        MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MiuixMetric("注册异常", stats?.unregistered, Modifier.weight(1f),
                        MaterialTheme.colorScheme.error)
                    MiuixMetric("识别总数", stats?.total, Modifier.weight(1f),
                        MaterialTheme.colorScheme.tertiary)
                }
            }
        }
        Surface(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("设备信息", style = MaterialTheme.typography.titleLarge)
                InfoLine("管理端版本", BuildConfig.VERSION_NAME)
                InfoLine("Android 版本", Build.VERSION.RELEASE ?: "未知")
                InfoLine("设备型号", Build.MODEL ?: "未知")
            }
        }
        Text("管理入口", modifier = Modifier.padding(start = 4.dp),
            style = MaterialTheme.typography.titleLarge)
        StyleActionRow("应用管理", "查看注册状态与应用接入情况", onOpenApps)
        StyleActionRow("推送事件", "查看消息到达记录与事件详情", onOpenEvents)
        StyleActionRow("设置", "服务配置、通知选项与流体云", onOpenSettings)
    }
}

@Composable
private fun MiuixMetric(
    label: String,
    value: Int?,
    modifier: Modifier,
    valueColor: androidx.compose.ui.graphics.Color
) {
    Surface(
        modifier,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.68f)
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Text(value?.toString() ?: "—", style = MaterialTheme.typography.headlineMedium,
                color = valueColor)
            Text(label, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MaterialOverview(
    stats: OverviewStats?,
    onOpenApps: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text("运行概览", style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface)
            Text("O Push · realme UI / ColorOS", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("应用接入统计", style = MaterialTheme.typography.titleMedium)
                        Text(stats?.let { "当前识别到 ${it.total} 个接入应用" } ?: "正在读取应用数据…",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(stats?.total?.toString() ?: "—", style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary)
                }
                if (stats != null) {
                    val total = stats.total.coerceAtLeast(1)
                    LinearProgressIndicator(
                        progress = stats.registered.toFloat() / total,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatValue("已注册", stats.registered, MaterialTheme.colorScheme.primary)
                        StatValue("待注册", stats.waiting, MaterialTheme.colorScheme.onSurfaceVariant)
                        StatValue("注册异常", stats.unregistered, MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("版本与设备", style = MaterialTheme.typography.titleMedium)
                InfoLine("管理端版本", BuildConfig.VERSION_NAME)
                InfoLine("Android 版本", Build.VERSION.RELEASE ?: "未知")
                InfoLine("设备型号", Build.MODEL ?: "未知")
            }
        }
        Text("管理入口", style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 2.dp))
        StyleActionRow("应用管理", "查看注册状态与应用接入情况", onOpenApps)
        StyleActionRow("推送事件", "查看消息到达记录与事件详情", onOpenEvents)
        StyleActionRow("设置", "服务配置、通知选项与流体云", onOpenSettings)
    }
}

@Composable
private fun StatValue(label: String, value: Int, color: androidx.compose.ui.graphics.Color) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(value.toString(), style = MaterialTheme.typography.titleLarge, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface)
    }
}
