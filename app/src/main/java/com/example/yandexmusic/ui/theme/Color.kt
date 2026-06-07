package com.example.yandexmusic.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

val YandexYellow = Color(0xFFFFCC00)
val YandexBlack = Color(0xFF111111)
val YandexDarkGray = Color(0xFF1C1C1C)
val YandexLightGray = Color(0xFF999999)

val DarkColorScheme = darkColorScheme(
    primary = YandexYellow,
    secondary = YandexYellow,
    background = YandexBlack,
    surface = YandexDarkGray,
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = YandexLightGray
)
