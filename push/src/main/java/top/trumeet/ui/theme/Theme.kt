package top.trumeet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
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

private val DarkColorScheme = darkColorScheme(
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

@Composable
fun Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = androidx.compose.material3.Shapes(
            extraSmall = RoundedCornerShape(8.dp),
            small = RoundedCornerShape(13.dp),
            medium = RoundedCornerShape(19.dp),
            large = RoundedCornerShape(27.dp)
        ),
        content = content
    )
}
