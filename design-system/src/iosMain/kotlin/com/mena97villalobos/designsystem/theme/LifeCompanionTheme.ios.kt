package com.mena97villalobos.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
@Suppress("UNUSED_PARAMETER")
actual fun LifeCompanionTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content,
    )
}
