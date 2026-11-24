package top.trumeet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val LocalUiStyle = staticCompositionLocalOf { UiStyle.MIUIX }

/** Miuix-inspired palette: Xiaomi orange accent, neutral surfaces and generous shapes. */
private val MiuixLight = lightColorScheme(
    primary = Color(0xFFFF6900),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE4CC),
    onPrimaryContainer = Color(0xFF4A1C00),
    secondary = Color(0xFF0D84FF),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6E9FF),
    onSecondaryContainer = Color(0xFF001D36),
    tertiary = Color(0xFF3482FF),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDCE8FF),
    onTertiaryContainer = Color(0xFF001A41),
    background = Color(0xFFF5F5F7),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF0F0F2),
    onSurfaceVariant = Color(0xFF666666),
    outline = Color(0xFFB0B0B5),
    error = Color(0xFFE53935),
    onError = Color.White
)

private val MiuixDark = darkColorScheme(
    primary = Color(0xFFFF8A3D),
    onPrimary = Color(0xFF3B1400),
    primaryContainer = Color(0xFF7A3100),
    onPrimaryContainer = Color(0xFFFFE4CC),
    secondary = Color(0xFF7CB8FF),
    onSecondary = Color(0xFF00315C),
    secondaryContainer = Color(0xFF004A8A),
    onSecondaryContainer = Color(0xFFD6E9FF),
    tertiary = Color(0xFF9CBCFF),
    onTertiary = Color(0xFF002E6B),
    tertiaryContainer = Color(0xFF1A448F),
    onTertiaryContainer = Color(0xFFDCE8FF),
    background = Color(0xFF101012),
    onBackground = Color(0xFFECECEC),
    surface = Color(0xFF1C1C1E),
    onSurface = Color(0xFFECECEC),
    surfaceVariant = Color(0xFF2C2C2E),
    onSurfaceVariant = Color(0xFFAAAAAA),
    outline = Color(0xFF5C5C60),
    error = Color(0xFFFF8A80),
    onError = Color(0xFF5C0000)
)

/** Material 3 palette: indigo primary, tonal containers and restrained corner radii. */
private val MaterialLight = lightColorScheme(
    primary = Color(0xFF4056B5),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE2FF),
    onPrimaryContainer = Color(0xFF101D58),
    secondary = Color(0xFF286C78),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC6EDF1),
    onSecondaryContainer = Color(0xFF062F37),
    tertiary = Color(0xFF765A9B),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEEDCFF),
    onTertiaryContainer = Color(0xFF2B1748),
    background = Color(0xFFF5F6FC),
    onBackground = Color(0xFF171A29),
    surface = Color(0xFFFCFCFF),
    onSurface = Color(0xFF171A29),
    surfaceVariant = Color(0xFFE9EBF5),
    onSurfaceVariant = Color(0xFF4B5065),
    outline = Color(0xFF777D93),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val MaterialDark = darkColorScheme(
    primary = Color(0xFFB9C3FF),
    onPrimary = Color(0xFF172B79),
    primaryContainer = Color(0xFF2C418F),
    onPrimaryContainer = Color(0xFFDDE2FF),
    secondary = Color(0xFF9DD5DD),
    onSecondary = Color(0xFF00363E),
    secondaryContainer = Color(0xFF174E57),
    onSecondaryContainer = Color(0xFFC6EDF1),
    tertiary = Color(0xFFD7BDF7),
    onTertiary = Color(0xFF3D285F),
    tertiaryContainer = Color(0xFF543F78),
    onTertiaryContainer = Color(0xFFEEDCFF),
    background = Color(0xFF10131D),
    onBackground = Color(0xFFE4E6F4),
    surface = Color(0xFF171B28),
    onSurface = Color(0xFFE4E6F4),
    surfaceVariant = Color(0xFF272D3D),
    onSurfaceVariant = Color(0xFFC1C5D8),
    outline = Color(0xFF8B91A7),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private fun shapesFor(style: UiStyle): Shapes = when (style) {
    UiStyle.MIUIX -> Shapes(
        small = RoundedCornerShape(14.dp),
        medium = RoundedCornerShape(20.dp),
        large = RoundedCornerShape(28.dp)
    )
    UiStyle.MATERIAL -> Shapes(
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(16.dp)
    )
}

private fun typographyFor(style: UiStyle) = when (style) {
    UiStyle.MIUIX -> Typography.copy(
        titleLarge = Typography.titleLarge.copy(fontSize = 21.sp, fontWeight = FontWeight.Medium),
        titleMedium = Typography.titleMedium.copy(fontSize = 17.sp, fontWeight = FontWeight.Medium),
        titleSmall = Typography.titleSmall.copy(fontSize = 15.sp, fontWeight = FontWeight.Medium),
        bodyMedium = Typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 22.sp)
    )
    UiStyle.MATERIAL -> Typography
}

@Composable
fun Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    style: UiStyle? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val uiStyle = style ?: UiStyle.current(context)
    val colorScheme = when (uiStyle) {
        UiStyle.MIUIX -> if (darkTheme) MiuixDark else MiuixLight
        UiStyle.MATERIAL -> if (darkTheme) MaterialDark else MaterialLight
    }
    CompositionLocalProvider(LocalUiStyle provides uiStyle) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typographyFor(uiStyle),
            shapes = shapesFor(uiStyle),
            content = content
        )
    }
}
