package com.example.mahilashaktiunnativ2.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(

    primary = PrimaryGreen,

    secondary = DarkGreen,

    background = AppBackground,

    surface = CardBackground,

    onPrimary = CardBackground,

    onBackground = PrimaryText,

    onSurface = PrimaryText
)

@Composable
fun MahilaShaktiUnnatiV2Theme(

    content: @Composable () -> Unit

) {

    MaterialTheme(

        colorScheme = AppColorScheme,

        typography = AppTypography,

        content = content
    )
}