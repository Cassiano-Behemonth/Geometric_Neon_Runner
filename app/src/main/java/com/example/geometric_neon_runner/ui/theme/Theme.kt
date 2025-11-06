package com.example.geometric_neon_runner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.geometric_neon_runner.ui.color.DarkColorScheme
import com.example.geometric_neon_runner.ui.type.NeonTypography


@Composable
fun NeonTunnelTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NeonTypography,
        content = content
    )
}