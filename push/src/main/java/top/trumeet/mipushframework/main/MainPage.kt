package top.trumeet.mipushframework.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.xiaomi.xmsf.R
import top.trumeet.mipushframework.MainPageUtils
import top.trumeet.mipushframework.component.SearchBar
import top.trumeet.mipushframework.main.subpage.ApplicationList
import top.trumeet.mipushframework.main.subpage.EventList
import top.trumeet.mipushframework.main.subpage.Settings
import top.trumeet.mipushframework.utils.applyOplusWindowStyle
import top.trumeet.ui.theme.Theme

private val mainPageUtils = MainPageUtils()
private var placeholder by mutableStateOf("搜索应用或推送事件")

class MainPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyOplusWindowStyle()
        mainPageUtils.initOnCreate(applicationContext) { placeholder = it.toString() }
        setContent {
            Main(Screen.Apps.route.toString()) {
                {
                    composable(Screen.Events.route.toString()) {
                        Column {
                            var query by rememberSaveable { mutableStateOf("") }
                            SearchBar(placeholder) { query = it }
                            EventList(query)
                        }
                    }
                    composable(Screen.Apps.route.toString()) {
                        Column {
                            var query by rememberSaveable { mutableStateOf("") }
                            SearchBar(placeholder) { query = it }
                            ApplicationList(query)
                        }
                    }
                    composable(Screen.Settings.route.toString()) { Settings() }
                }
            }
        }
    }
}

private sealed class Screen(val route: Int, val icon: Int, val subtitle: String, val eyebrow: String) {
    object Events : Screen(R.string.main_event, R.drawable.ic_event_note_black_24dp, "查看推送到达记录、状态与消息详情", "DELIVERY MONITOR")
    object Apps : Screen(R.string.main_apps, R.drawable.ic_apps_black_24dp, "管理应用注册状态与推送接入情况", "APP MANAGEMENT")
    object Settings : Screen(R.string.main_settings, R.drawable.ic_settings_black_24dp, "服务配置、运行诊断与个性化选项", "SYSTEM PREFERENCES")
}

@Composable
private fun BrandBar() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = RoundedCornerShape(15.dp),
            color = MaterialTheme.colorScheme.primary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.ic_notifications_black_24dp),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(23.dp)
                )
            }
        }
        Column(Modifier.padding(start = 12.dp).weight(1f)) {
            Text("MiPush Framework", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text("PUSH CONTROL CENTER", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
            Text(
                "O 系适配",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun DashboardHeader(navController: NavController) {
    val entry by navController.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    val screen = when (route) {
        Screen.Events.route.toString() -> Screen.Events
        Screen.Settings.route.toString() -> Screen.Settings
        else -> Screen.Apps
    }
    val title = stringResource(screen.route)

    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 5.dp)
            .height(158.dp).clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        Color(0xFF394E9C),
                        Color(0xFF286E82)
                    )
                )
            )
    ) {
        Box(
            Modifier.align(Alignment.TopEnd).offset(x = 30.dp, y = (-42).dp)
                .size(178.dp).border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
        )
        Box(
            Modifier.align(Alignment.BottomEnd).offset(x = 24.dp, y = 55.dp)
                .size(130.dp).border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape)
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 19.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(screen.eyebrow, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.78f))
            Column {
                Text(title, style = MaterialTheme.typography.headlineLarge, color = Color.White)
                Text(screen.subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.86f), modifier = Modifier.padding(top = 5.dp))
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(Screen.Events, Screen.Apps, Screen.Settings)
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        NavigationBar(
            modifier = Modifier.height(72.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            val entry by navController.currentBackStackEntryAsState()
            val currentRoute = entry?.destination?.route
            items.forEach { screen ->
                val name = stringResource(screen.route)
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(screen.icon),
                            contentDescription = name,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text(name, style = MaterialTheme.typography.labelMedium) },
                    selected = currentRoute == screen.route.toString(),
                    onClick = {
                        navController.navigate(screen.route.toString()) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun Main(startDestination: String, navContent: () -> NavGraphBuilder.() -> Unit) {
    val navController = rememberNavController()
    Theme {
        Column(
            Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding()
        ) {
            BrandBar()
            DashboardHeader(navController)
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.weight(1f),
                builder = navContent()
            )
            BottomNavigationBar(navController)
        }
    }
}
