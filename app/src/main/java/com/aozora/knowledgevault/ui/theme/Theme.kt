package com.aozora.knowledgevault.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 文艺简约配色 - 浅色模式
private val LightColorScheme = lightColorScheme(
    // 主色调：柔和的蓝灰色
    primary = Color(0xFF5B7C99),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD8E6F3),
    onPrimaryContainer = Color(0xFF1A3A52),
    
    // 次要色：温暖的米色
    secondary = Color(0xFFB08968),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF5EBE0),
    onSecondaryContainer = Color(0xFF3D2817),
    
    // 第三色：淡雅的绿色
    tertiary = Color(0xFF7B9E87),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE0F2E9),
    onTertiaryContainer = Color(0xFF1F4235),
    
    // 背景色：纯白与极浅灰
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF2C3E50),
    
    // 表面色：纯白
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF2C3E50),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF546E7A),
    
    // 轮廓色：极浅灰
    outline = Color(0xFFE0E0E0),
    outlineVariant = Color(0xFFF5F5F5),
    
    // 错误色：柔和的红
    error = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B)
)

// 文艺简约配色 - 深色模式
private val DarkColorScheme = darkColorScheme(
    // 主色调：柔和的蓝色
    primary = Color(0xFF9DB4CE),
    onPrimary = Color(0xFF1A3A52),
    primaryContainer = Color(0xFF3D5A75),
    onPrimaryContainer = Color(0xFFD8E6F3),
    
    // 次要色：温暖的米色
    secondary = Color(0xFFD4B5A0),
    onSecondary = Color(0xFF3D2817),
    secondaryContainer = Color(0xFF6B4F3A),
    onSecondaryContainer = Color(0xFFF5EBE0),
    
    // 第三色：淡雅的绿色
    tertiary = Color(0xFFA5C4AE),
    onTertiary = Color(0xFF1F4235),
    tertiaryContainer = Color(0xFF456B54),
    onTertiaryContainer = Color(0xFFE0F2E9),
    
    // 背景色：深灰黑
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE3E2E6),
    
    // 表面色：稍浅的黑
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF2C2E30),
    onSurfaceVariant = Color(0xFFC4C7C5),
    
    // 轮廓色
    outline = Color(0xFF3A3C3E),
    outlineVariant = Color(0xFF2C2E30),
    
    // 错误色
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun KnowledgeVaultTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // 不使用动态颜色，保持统一美学
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
