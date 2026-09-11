package com.hotaro.mascot.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.lightColorScheme

private val DarkColorScheme = darkColorScheme(
    primary = defaultPrimaryDark,
    onPrimary = defaultOnPrimaryDark,
    primaryContainer = defaultPrimaryContainerDark,
    onPrimaryContainer = defaultOnPrimaryContainerDark,
    secondary = defaultSecondaryDark,
    onSecondary = defaultOnSecondaryDark,
    secondaryContainer = defaultSecondaryContainerDark,
    onSecondaryContainer = defaultOnSecondaryContainerDark,
    tertiary = defaultTertiaryDark,
    onTertiary = defaultOnTertiaryDark,
    tertiaryContainer = defaultTertiaryContainerDark,
    onTertiaryContainer = defaultOnTertiaryContainerDark,
    error = defaultErrorDark,
    onError = defaultOnErrorDark,
    errorContainer = defaultErrorContainerDark,
    onErrorContainer = defaultOnErrorContainerDark,
    background = defaultBackgroundDark,
    onBackground = defaultOnBackgroundDark,
    surface = defaultSurfaceDark,
    onSurface = defaultOnSurfaceDark,
    surfaceVariant = defaultSurfaceVariantDark,
    onSurfaceVariant = defaultOnSurfaceVariantDark,
    outline = defaultOutlineDark,
    outlineVariant = defaultOutlineVariantDark,
    scrim = defaultScrimDark,
    inverseSurface = defaultInverseSurfaceDark,
    inverseOnSurface = defaultInverseOnSurfaceDark,
    inversePrimary = defaultInversePrimaryDark,
    surfaceTint = defaultPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF665590),
    onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFE9DDFF),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF211049),
    secondary = androidx.compose.ui.graphics.Color(0xFF625B71),
    onSecondary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFE8DEF8),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF1D192B),
    tertiary = androidx.compose.ui.graphics.Color(0xFF7D5260),
    onTertiary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    tertiaryContainer = androidx.compose.ui.graphics.Color(0xFFFFD9E3),
    onTertiaryContainer = androidx.compose.ui.graphics.Color(0xFF31101D),
    error = androidx.compose.ui.graphics.Color(0xFFB3261E),
    onError = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    errorContainer = androidx.compose.ui.graphics.Color(0xFFF9DEDC),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFF410E0B),
    background = androidx.compose.ui.graphics.Color(0xFFFFFBFE),
    onBackground = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    surface = androidx.compose.ui.graphics.Color(0xFFFFFBFE),
    onSurface = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFE7E0EC),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF49454F),
    outline = androidx.compose.ui.graphics.Color(0xFF79747E),
    outlineVariant = androidx.compose.ui.graphics.Color(0xFFCAC4D0),
    scrim = androidx.compose.ui.graphics.Color(0xFF000000),
    inverseSurface = androidx.compose.ui.graphics.Color(0xFF313033),
    inverseOnSurface = androidx.compose.ui.graphics.Color(0xFFF4EFF4),
    inversePrimary = androidx.compose.ui.graphics.Color(0xFFD0BCFF)
)

val NoctaliColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF8AB4F8),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF002C6B),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF0D47A1),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFD6E4FF),
    secondary = androidx.compose.ui.graphics.Color(0xFFAECBFA),
    background = androidx.compose.ui.graphics.Color(0xFF0A0F1A),
    surface = androidx.compose.ui.graphics.Color(0xFF131924),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF202A3D)
)

val LavenderColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFC0A6FF),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF2C007A),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF4500AB),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFE4D5FF),
    secondary = androidx.compose.ui.graphics.Color(0xFFD6C0FF),
    background = androidx.compose.ui.graphics.Color(0xFF130E1F),
    surface = androidx.compose.ui.graphics.Color(0xFF1C162E),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF2B2345)
)

val PinkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFFFB2C2),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF670025),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF8E0036),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFFFD9E1),
    secondary = androidx.compose.ui.graphics.Color(0xFFFFC6D3),
    background = androidx.compose.ui.graphics.Color(0xFF1C0D11),
    surface = androidx.compose.ui.graphics.Color(0xFF29141A),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF3F202A)
)

val MochaColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFE5BCA7),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF45271A),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF613B2A),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFFFDBCB),
    secondary = androidx.compose.ui.graphics.Color(0xFFEDD2C4),
    background = androidx.compose.ui.graphics.Color(0xFF1C1816),
    surface = androidx.compose.ui.graphics.Color(0xFF26211E),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF3D3531)
)

val NoctaliLightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF0D47A1),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFD6E4FF),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF001945),
    secondary = androidx.compose.ui.graphics.Color(0xFF4A658A),
    background = androidx.compose.ui.graphics.Color(0xFFFDFBFF),
    surface = androidx.compose.ui.graphics.Color(0xFFFDFBFF),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFDFE2EB)
)

val LavenderLightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF6200EE),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFE4D5FF),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF1B0047),
    secondary = androidx.compose.ui.graphics.Color(0xFF61537A),
    background = androidx.compose.ui.graphics.Color(0xFFFFFBFF),
    surface = androidx.compose.ui.graphics.Color(0xFFFFFBFF),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFE6E0EE)
)

val PinkLightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFC00050),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFFFD9E1),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF3F0015),
    secondary = androidx.compose.ui.graphics.Color(0xFF75565D),
    background = androidx.compose.ui.graphics.Color(0xFFFFFBFF),
    surface = androidx.compose.ui.graphics.Color(0xFFFFFBFF),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFF3DDE1)
)

val MochaLightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF8A5134),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFFFDBCB),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF331200),
    secondary = androidx.compose.ui.graphics.Color(0xFF76584A),
    background = androidx.compose.ui.graphics.Color(0xFFFFFBFF),
    surface = androidx.compose.ui.graphics.Color(0xFFFFFBFF),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFF3DFD2)
)

private object NoRippleTheme : androidx.compose.material.ripple.RippleTheme {
    @Composable
    override fun defaultColor(): androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Transparent

    @Composable
    override fun rippleAlpha(): androidx.compose.material.ripple.RippleAlpha =
        androidx.compose.material.ripple.RippleAlpha(0f, 0f, 0f, 0f)
}

private object NoIndication : androidx.compose.foundation.Indication {
    private class NoIndicationInstance : androidx.compose.foundation.IndicationInstance {
        override fun androidx.compose.ui.graphics.drawscope.ContentDrawScope.drawIndication() {
            drawContent()
        }
    }
    @Composable
    override fun rememberUpdatedInstance(interactionSource: androidx.compose.foundation.interaction.InteractionSource): androidx.compose.foundation.IndicationInstance {
        return androidx.compose.runtime.remember { NoIndicationInstance() }
    }
}

@Composable
fun StrictClockTheme(
    content: @Composable () -> Unit
) {
    val activeScheme by ThemeManager.activeScheme.collectAsState()
    val isAmoled by ThemeManager.isAmoled.collectAsState()
    val themeMode by ThemeManager.themeMode.collectAsState()
    val context = LocalContext.current

    val isSystemDark = isSystemInDarkTheme()
    val useDarkTheme = when(themeMode) {
        "Dark" -> true
        "Light" -> false
        else -> isSystemDark
    }

    val baseColorScheme = if (useDarkTheme) {
        when (activeScheme) {
            "Dynamic" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) dynamicDarkColorScheme(context) else DarkColorScheme
            "Strict Default" -> DarkColorScheme
            "Noctali" -> NoctaliColorScheme
            "Lavender" -> LavenderColorScheme
            "Pink" -> PinkColorScheme
            "Mocha" -> MochaColorScheme
            else -> DarkColorScheme
        }
    } else {
        when (activeScheme) {
            "Dynamic" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) androidx.compose.material3.dynamicLightColorScheme(context) else LightColorScheme
            "Strict Default" -> LightColorScheme
            "Noctali" -> NoctaliLightColorScheme
            "Lavender" -> LavenderLightColorScheme
            "Pink" -> PinkLightColorScheme
            "Mocha" -> MochaLightColorScheme
            else -> LightColorScheme
        }
    }

    val colorScheme = if (isAmoled && useDarkTheme) {
        baseColorScheme.copy(
            background = androidx.compose.ui.graphics.Color.Black,
            surface = androidx.compose.ui.graphics.Color.Black
        )
    } else {
        baseColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        androidx.compose.runtime.SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.navigationBarDividerColor = android.graphics.Color.TRANSPARENT
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !useDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.material.ripple.LocalRippleTheme provides NoRippleTheme,
            androidx.compose.foundation.LocalIndication provides NoIndication,
            content = content
        )
    }
}
