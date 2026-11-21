package top.trumeet.mipushframework.utils

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Applies edge-to-edge system bar behavior consistently across ColorOS,
 * realme UI and OxygenOS builds.
 */
fun Activity.applyOplusWindowStyle() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = Color.TRANSPARENT

    val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    val isLightTheme = nightMode != Configuration.UI_MODE_NIGHT_YES
    WindowInsetsControllerCompat(window, window.decorView).apply {
        isAppearanceLightStatusBars = isLightTheme
        isAppearanceLightNavigationBars = isLightTheme
    }
}
