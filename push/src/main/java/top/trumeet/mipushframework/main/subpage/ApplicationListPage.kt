package top.trumeet.mipushframework.main.subpage

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import com.elvishew.xlog.XLog
import com.xiaomi.xmsf.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.trumeet.common.utils.Utils
import top.trumeet.mipush.provider.entities.RegisteredApplication
import top.trumeet.mipushframework.component.AppIcon
import top.trumeet.mipushframework.component.RefreshableLazyColumn
import top.trumeet.mipushframework.component.iconCache
import top.trumeet.mipushframework.main.RegistrationStateStyle
import top.trumeet.mipushframework.utils.ParseUtils

data class AppInfoForDisplay(
    val registrationState: Pair<String, Color>,
    val lastReceiveTime: String,
)

private var g_itemsInfo by mutableStateOf(emptyMap<String, AppInfoForDisplay>())
private var g_items by mutableStateOf(ApplicationPageOperation.MiPushApplications())

@Composable
fun ApplicationList(query: String) {
    val context = LocalContext.current
    ApplicationList(query) {
        val miPushApplications =
            ApplicationPageOperation.getMiPushApplicationsThatQueryMatched(query)
        ApplicationPageOperation.updateRegisteredApplicationDb(
            context,
            miPushApplications.res
        )
        miPushApplications
    }
}

@Composable
fun ApplicationList(
    query: String = "",
    getMiPushApplications: () -> ApplicationPageOperation.MiPushApplications
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    if (isPreview) g_items = getMiPushApplications()
    var isNeedRefresh by rememberSaveable(query) { mutableStateOf(true) }

    val refreshScope = rememberCoroutineScope { Dispatchers.IO }
    val onRefresh: (onRefreshed: () -> Unit) -> Unit = { onRefreshed ->
        refreshScope.launch {
            val applications = getMiPushApplications()
            updateInfos(applications, context)
            withContext(Dispatchers.Main) {
                g_items = applications
                isNeedRefresh = false
                onRefreshed()
            }
            applications.res.forEach {
                iconCache.cache(it.packageName)
            }
        }
    }

    Page {
        RefreshableLazyColumn(onRefresh, { false }, onRefresh, isNeedRefresh) {
            items(g_items.res, { it.packageName }) {
                ApplicationItem(it)
            }
            item {
                val notUseMiPushCount by remember { derivedStateOf { g_items.totalPkg - g_items.res.size } }
                Footer(notUseMiPushCount)
            }
        }
    }
}

private fun updateInfos(
    applications: ApplicationPageOperation.MiPushApplications,
    context: Context
) {
    val infoMap = emptyMap<String, AppInfoForDisplay>().toMutableMap()
    applications.res.forEach {
        infoMap[it.packageName] = AppInfoForDisplay(
            registrationState = RegistrationStateStyle.contentOf(it, context),
            lastReceiveTime = if (it.lastReceiveTime.time == 0L) ""
            else context.getString(R.string.last_receive) + ParseUtils.getFriendlyDateString(
                it.lastReceiveTime,
                Utils.getUTC(),
                context
            ),
        )
    }
    g_itemsInfo = infoMap
}

@Composable
private fun Footer(notUseMiPushCount: Int) {
    val context = LocalContext.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painterResource(R.drawable.ic_info_outline_black_24dp),
            null,
            tint = Color(0xFF757575),
            modifier = Modifier.padding(10.dp)
        )
        Text(
            ApplicationPageOperation.getNotSupportHint(
                context,
                notUseMiPushCount
            )
        )
    }
}

@Composable
private fun ApplicationItem(item: RegisteredApplication) {
    val context = LocalContext.current

    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth()
                .clickable { EventListPageUtils.startManagePermissions(context, item.packageName, true) }
                .padding(horizontal = 20.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon(item.packageName, item.appName, Modifier.size(46.dp))
            Spacer(Modifier.width(15.dp))
            Column(Modifier.weight(1f)) {
                AppInfo(item)
                LastReceive(item)
            }
        }
        Divider(
            modifier = Modifier.padding(start = 81.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
            thickness = 0.6.dp
        )
    }
}

@Composable
private fun LastReceive(item: RegisteredApplication) {
    val info = g_itemsInfo[item.packageName] ?: return
    if (info.lastReceiveTime.isNotBlank()) {
        Text(
            info.lastReceiveTime,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AppInfo(item: RegisteredApplication) {
    val context = LocalContext.current
    val info = g_itemsInfo[item.packageName]
        ?: AppInfoForDisplay(RegistrationStateStyle.contentOf(item, context), "")
    val stateColor = if (info.registrationState.second == Color.Unspecified) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        info.registrationState.second
    }
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            item.appName,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(50),
            color = stateColor.copy(alpha = 0.13f)
        ) {
            Text(
                info.registrationState.first,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = stateColor,
                maxLines = 1
            )
        }
    }
}


@Preview(
    showBackground = true,
    device = Devices.PIXEL_3,
    showSystemUi = true,
)
@Composable
fun ApplicationListPreview() {
    XLog.init()

    ApplicationList {
        val miPushApplications = ApplicationPageOperation.MiPushApplications()
        miPushApplications.res = listOf(
            registeredApplication(
                RegisteredApplication.RegisteredType.NotRegistered,
                "123"
            ),
            registeredApplication(
                RegisteredApplication.RegisteredType.Registered,
                "qwe"
            ),
            registeredApplication(
                RegisteredApplication.RegisteredType.Registered,
                "asd"
            ),
            registeredApplication(
                RegisteredApplication.RegisteredType.Unregistered,
                "zxc"
            ),
            registeredApplication(
                RegisteredApplication.RegisteredType.Unregistered,
                "456",
                false
            ),
        ) + ('a'..'z').map {
            registeredApplication(
                RegisteredApplication.RegisteredType.NotRegistered,
                it.toString()
            )
        }

        miPushApplications
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_3,
    showSystemUi = true,
)
@Composable
fun OneApplicationWithNonMiPushAppPreview() {
    XLog.init()

    ApplicationList {
        val miPushApplications = ApplicationPageOperation.MiPushApplications()
        miPushApplications.res = listOf(
            registeredApplication(
                RegisteredApplication.RegisteredType.NotRegistered,
                "123"
            )
        )
        miPushApplications.totalPkg = 100
        miPushApplications
    }
}

private fun registeredApplication(
    registeredType: Int,
    appName: String,
    existServices: Boolean = true
): RegisteredApplication {
    val registeredApplication =
        RegisteredApplication(
            null,
            appName,
            RegisteredApplication.Type.ASK,
            true,
            registeredType,
            appName
        )
    registeredApplication.existServices = existServices
    return registeredApplication
}