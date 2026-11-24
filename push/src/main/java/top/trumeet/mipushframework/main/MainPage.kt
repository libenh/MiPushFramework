package top.trumeet.mipushframework.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import top.trumeet.ui.theme.LocalUiStyle
import top.trumeet.ui.theme.Theme
import top.trumeet.ui.theme.UiStyle

private val mainPageUtils = MainPageUtils()
private var placeholder by mutableStateOf("搜索应用或推送记录")

class MainPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyOplusWindowStyle()
        mainPageUtils.initOnCreate(applicationContext) { placeholder = it.toString() }
        setContent {
            var uiStyle by rememberSaveable {
                mutableStateOf(UiStyle.current(this).key)
            }
            Main(
                startDestination = Screen.Overview.route.toString(),
                uiStyleKey = uiStyle,
                onUiStyleChange = {
                    UiStyle.save(this, it)
                    uiStyle = it.key
                }
            ) {
                {
                    composable(Screen.Overview.route.toString()) {
                        Overview(
                            onOpenApps = { pendingNavigation = Screen.Apps.route.toString() },
                            onOpenEvents = { pendingNavigation = Screen.Events.route.toString() },
                            onOpenSettings = { pendingNavigation = Screen.Settings.route.toString() }
                        )
                    }
                    composable(Screen.Events.route.toString()) {
                        Column(Modifier.fillMaxSize()) {
                            var query by rememberSaveable { mutableStateOf("") }
                            SearchBar(placeholder) { query = it }
                            EventList(query)
                        }
                    }
                    composable(Screen.Apps.route.toString()) {
                        Column(Modifier.fillMaxSize()) {
                            var query by rememberSaveable { mutableStateOf("") }
                            SearchBar(placeholder) { query = it }
                            ApplicationList(query)
                        }
                    }
                    composable(Screen.Settings.route.toString()) {
                        Settings(onUiStyleChange = { style ->
                            UiStyle.save(this@MainPage, style)
                            uiStyle = style.key
                        })
                    }
                }
            }
        }
    }

    companion object {
        internal var pendingNavigation by mutableStateOf<String?>(null)
    }
}

private sealed class Screen(val route: Int, val icon: Int) {
    object Overview : Screen(R.string.main_overview, R.drawable.ic_notifications_black_24dp)
    object Apps : Screen(R.string.main_apps, R.drawable.ic_apps_black_24dp)
    object Events : Screen(R.string.main_event, R.drawable.ic_event_note_black_24dp)
    object Settings : Screen(R.string.main_settings, R.drawable.ic_settings_black_24dp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Main(
    startDestination: String,
    uiStyleKey: String,
    onUiStyleChange: (UiStyle) -> Unit,
    navContent: () -> NavGraphBuilder.() -> Unit
) {
    val navController = rememberNavController()
    androidx.compose.runtime.LaunchedEffect(MainPage.pendingNavigation) {
        val route = MainPage.pendingNavigation ?: return@LaunchedEffect
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
        }
        MainPage.pendingNavigation = null
    }

    Theme(style = UiStyle.fromKey(uiStyleKey)) {
        val isMiuix = LocalUiStyle.current == UiStyle.MIUIX
        Scaffold(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            topBar = { MainTopBar(navController, isMiuix) },
            bottomBar = { BottomNavigationBar(navController, isMiuix) }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.fillMaxSize().padding(padding),
                builder = navContent()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(navController: NavController, isMiuix: Boolean) {
    val entry by navController.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    val screen = when (route) {
        Screen.Apps.route.toString() -> Screen.Apps
        Screen.Events.route.toString() -> Screen.Events
        Screen.Settings.route.toString() -> Screen.Settings
        else -> Screen.Overview
    }
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = if (isMiuix) 0.dp else 1.dp,
        shadowElevation = if (isMiuix) 0.dp else 2.dp
    ) {
        TopAppBar(
            title = {
                Text(
                    stringResource(screen.route),
                    style = if (isMiuix) MaterialTheme.typography.titleLarge
                    else MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}

@Composable
private fun BottomNavigationBar(navController: NavController, isMiuix: Boolean) {
    val items = listOf(Screen.Overview, Screen.Apps, Screen.Events, Screen.Settings)
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (isMiuix) 8.dp else 3.dp,
        tonalElevation = if (isMiuix) 0.dp else 2.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            val entry by navController.currentBackStackEntryAsState()
            val currentRoute = entry?.destination?.route
            items.forEach { screen ->
                val label = stringResource(screen.route)
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(screen.icon),
                            contentDescription = label
                        )
                    },
                    label = { Text(label, style = MaterialTheme.typography.labelMedium) },
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
