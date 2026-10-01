package com.example.lavajato.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LavaJatoColorScheme = lightColorScheme(
    primary = NavyDark,
    onPrimary = White,
    secondary = YellowAccent,
    onSecondary = NavyDark,
    background = LightGray,
    onBackground = Black,
    surface = White,
    onSurface = Black,
    error = Color0xFFB00020
)

// pequeno helper para manter o arquivo simples
private val Color0xFFB00020 = androidx.compose.ui.graphics.Color(0xFFB00020)

@Composable
fun LavaJatoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LavaJatoColorScheme,
        typography = Typography,
        content = content
    )
}
