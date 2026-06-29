package com.notipay.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NotiPayColors = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    secondary = TealDark,
    onSecondary = Color.White,
    background = ScreenBackground,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
)

@Composable
fun NotiPayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NotiPayColors,
        typography = NotiPayTypography,
        content = content,
    )
}
