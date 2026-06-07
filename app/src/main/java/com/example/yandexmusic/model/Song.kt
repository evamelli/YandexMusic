package com.example.yandexmusic.model

data class Song(
    val id: String = "",
    val title: String = "",
    val artist: String = "",
    val coverUrl: String = "",
    val audioUrl: String = "",
    val duration: Long = 0
)
