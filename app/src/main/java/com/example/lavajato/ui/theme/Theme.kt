package com.example.lavajato.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Declaramos a cor ANTES de usar no color scheme
private val RedError = Color(0xFFB00020)

private val LavaJatoColorScheme = lightColorScheme(
    primary = NavyDark,
    onPrimary = White,
    secondary = YellowAccent,
    onSecondary = NavyDark,
    background = LightGray,
    onBackground = Black,
    surface = White,
    onSurface = Black,
    error = RedError
)

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