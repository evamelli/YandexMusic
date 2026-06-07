package com.example.yandexmusic.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.yandexmusic.ui.theme.DarkColorScheme

@Composable
fun YandexMusicTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
