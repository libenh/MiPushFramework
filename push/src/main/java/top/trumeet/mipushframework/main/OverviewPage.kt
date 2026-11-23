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
import top.trumeet.mipushframework.main.subpage.ApplicationPageOperation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    var stats by remember { mutableStateOf<OverviewStats?>(null) }

    LaunchedEffect(Unit) {
        stats = withContext(Dispatchers.IO) {
            val applications = ApplicationPageOperation.getMiPushApplications()
            val list = applications.res
            OverviewStats(
                total = list.size,
                registered = list.count { app -> app.registeredType == RegisteredApplication.RegisteredType.Registered },
                unregistered = list.count { app -> app.registeredType == RegisteredApplication.RegisteredType.Unregistered },
                waiting = list.count { app -> app.registeredType == RegisteredApplication.RegisteredType.NotRegistered }
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text("运行概览", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            Text("MiPush Framework · O 系适配", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("应用接入统计", style = MaterialTheme.typography.titleMedium)
                        Text(
                            stats?.let { value -> "当前识别到 " + value.total + " 个 MiPush 应用" } ?: "正在读取应用数据…",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(stats?.total?.toString() ?: "—", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                }
                if (stats != null) {
                    val total = stats!!.total.coerceAtLeast(1)
                    LinearProgressIndicator(
                        progress = stats!!.registered.toFloat() / total,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatValue("已注册", stats!!.registered, MaterialTheme.colorScheme.primary)
                        StatValue("未注册", stats!!.waiting, MaterialTheme.colorScheme.onSurfaceVariant)
                        StatValue("已注销", stats!!.unregistered, MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
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

        Text("管理入口", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 2.dp))
        OverviewAction("应用管理", "查看注册状态与应用接入情况", onOpenApps)
        OverviewAction("推送事件", "查看消息到达记录与事件详情", onOpenEvents)
        OverviewAction("设置", "服务配置、通知选项与流体云", onOpenSettings)
    }
}

@Composable
private fun StatValue(label: String, value: Int, color: androidx.compose.ui.graphics.Color) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(value.toString(), style = MaterialTheme.typography.titleLarge, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun OverviewAction(title: String, summary: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("›", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
