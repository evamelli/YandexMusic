package com.example.yandexmusic.ui

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Main : Screen("main")
    object Home : Screen("home")
    object Library : Screen("library")
    object Player : Screen("player")
}
