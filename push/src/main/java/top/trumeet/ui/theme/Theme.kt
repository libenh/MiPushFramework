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
    primary = Color(0xFF3859B8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE2FF),
    onPrimaryContainer = Color(0xFF00164F),
    secondary = Color(0xFF575D72),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDBDFF9),
    onSecondaryContainer = Color(0xFF141A2C),
    tertiary = Color(0xFF76546F),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD7F2),
    onTertiaryContainer = Color(0xFF2D122A),
    background = Color(0xFFF7F7FC),
    onBackground = Color(0xFF191B22),
    surface = Color(0xFFF7F7FC),
    onSurface = Color(0xFF191B22),
    surfaceVariant = Color(0xFFE9EAF3),
    onSurfaceVariant = Color(0xFF454753),
    outline = Color(0xFF777985)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB8C5FF),
    onPrimary = Color(0xFF092878),
    primaryContainer = Color(0xFF23419F),
    onPrimaryContainer = Color(0xFFDCE2FF),
    secondary = Color(0xFFBFC4DF),
    onSecondary = Color(0xFF292E42),
    secondaryContainer = Color(0xFF40465B),
    onSecondaryContainer = Color(0xFFDBDFF9),
    tertiary = Color(0xFFE6B9D9),
    onTertiary = Color(0xFF44263F),
    tertiaryContainer = Color(0xFF5D3C56),
    onTertiaryContainer = Color(0xFFFFD7F2),
    background = Color(0xFF111319),
    onBackground = Color(0xFFE2E2EB),
    surface = Color(0xFF111319),
    onSurface = Color(0xFFE2E2EB),
    surfaceVariant = Color(0xFF292B35),
    onSurfaceVariant = Color(0xFFC5C6D2),
    outline = Color(0xFF8F909C)
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
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(18.dp),
            large = RoundedCornerShape(24.dp),
            extraLarge = RoundedCornerShape(30.dp)
        ),
        content = content
    )
}
