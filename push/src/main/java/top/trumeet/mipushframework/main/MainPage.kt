package top.trumeet.mipushframework.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.weight
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
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
import top.trumeet.mipushframework.main.subpage.ApplicationListPreview
import top.trumeet.mipushframework.main.subpage.EventDetailsDialogPreview
import top.trumeet.mipushframework.main.subpage.EventList
import top.trumeet.mipushframework.main.subpage.EventListPreview
import top.trumeet.mipushframework.main.subpage.Settings
import top.trumeet.mipushframework.main.subpage.SettingsPagePreview
import top.trumeet.mipushframework.utils.applyOplusWindowStyle
import top.trumeet.ui.theme.Theme

private val mainPageUtils1 = MainPageUtils()
private var placeholder by mutableStateOf("Search...")

class MainPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyOplusWindowStyle()
        mainPageUtils1.initOnCreate(applicationContext) { placeholder = it.toString() }
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

private sealed class Screen(val route: Int, val icon: Int, val subtitle: String) {
    object Events : Screen(R.string.main_event, R.drawable.ic_event_note_black_24dp, "Recent delivery activity")
    object Apps : Screen(R.string.main_apps, R.drawable.ic_apps_black_24dp, "Manage app registration and delivery")
    object Settings : Screen(R.string.main_settings, R.drawable.ic_settings_black_24dp, "Service, diagnostics and preferences")
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(Screen.Events, Screen.Apps, Screen.Settings)
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 10.dp
    ) {
        NavigationBar(
            modifier = Modifier.height(76.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { screen ->
                val name = stringResource(screen.route)
                NavigationBarItem(
                    icon = { Icon(painterResource(screen.icon), contentDescription = name, modifier = Modifier.size(22.dp)) },
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
private fun DashboardHeader(navController: NavController) {
    val entry by navController.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    val screen = when (route) {
        Screen.Events.route.toString() -> Screen.Events
        Screen.Settings.route.toString() -> Screen.Settings
        else -> Screen.Apps
    }
    val title = stringResource(screen.route)
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(15.dp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(42.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_apps_black_24dp),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Column(Modifier.padding(start = 12.dp)) {
                Text("MiPush Framework", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.weight(1f))
            Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primaryContainer) {
                Text("O 系适配", modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Column(Modifier.padding(horizontal = 18.dp, vertical = 15.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(screen.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f), modifier = Modifier.padding(top = 3.dp))
            }
        }
    }
}

@Composable
private fun Main(startDestination: String, navContent: () -> NavGraphBuilder.() -> Unit) {
    val navController = rememberNavController()
    Theme {
        Column(
            Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding().navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                DashboardHeader(navController)
                NavHost(navController = navController, startDestination = startDestination, modifier = Modifier.weight(1f), builder = navContent())
            }
            BottomNavigationBar(navController)
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_3)
@Composable
private fun MainEventsPreview() {
    Main(Screen.Events.route.toString()) {
        {
            composable(Screen.Events.route.toString()) {
                Column {
                    val onValueChange: (String) -> Unit = {}
                    SearchBar(placeholder, onValueChange)
                    EventListPreview()
                }
            }
            composable(Screen.Apps.route.toString()) { }
            composable(Screen.Settings.route.toString()) { }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_3)
@Composable
private fun MainAppsPreview() {
    Main(Screen.Apps.route.toString()) {
        {
            composable(Screen.Events.route.toString()) { }
            composable(Screen.Apps.route.toString()) {
                Column {
                    val onValueChange: (String) -> Unit = {}
                    SearchBar(placeholder, onValueChange)
                    ApplicationListPreview()
                }
            }
            composable(Screen.Settings.route.toString()) { }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_3)
@Composable
private fun MainSettingsPreview() {
    Main(Screen.Settings.route.toString()) {
        {
            composable(Screen.Events.route.toString()) { }
            composable(Screen.Apps.route.toString()) { }
            composable(Screen.Settings.route.toString()) { SettingsPagePreview() }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_3)
@Composable
private fun MainDialogPreview() {
    Main(Screen.Events.route.toString()) {
        {
            composable(Screen.Events.route.toString()) { EventDetailsDialogPreview() }
            composable(Screen.Apps.route.toString()) { }
            composable(Screen.Settings.route.toString()) { }
        }
    }
}
