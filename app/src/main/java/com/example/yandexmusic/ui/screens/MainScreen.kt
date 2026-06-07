package com.example.yandexmusic.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yandexmusic.ui.Screen
import com.example.yandexmusic.viewmodel.MusicViewModel
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(musicViewModel: MusicViewModel, userId: String, onLogout: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    val items = listOf(Screen.Home, Screen.Library, Screen.Player)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Яндекс Музыка", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { 
                            val icon = when(screen) {
                                Screen.Home -> Icons.Default.Home
                                Screen.Library -> Icons.Default.List
                                Screen.Player -> Icons.Default.PlayArrow
                                else -> Icons.Default.Home
                            }
                            Icon(icon, contentDescription = null) 
                        },
                        label = { 
                            val label = when(screen) {
                                Screen.Home -> "Главная"
                                Screen.Library -> "Коллекция"
                                Screen.Player -> "Плеер"
                                else -> screen.route
                            }
                            Text(label) 
                        },
                        selected = currentRoute == screen.route,
                        onClick = { 
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Выход") },
                text = { Text("Вы уверены, что хотите выйти из учетной записи?") },
                confirmButton = {
                    TextButton(onClick = { 
                        showLogoutDialog = false
                        onLogout() 
                    }) {
                        Text("Выйти", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Отмена")
                    }
                }
            )
        }

        NavHost(navController, startDestination = Screen.Home.route, modifier = Modifier.padding(innerPadding)) {
            composable(Screen.Home.route) { HomeScreen(musicViewModel, userId) }
            composable(Screen.Library.route) { LibraryScreen(musicViewModel, userId) }
            composable(Screen.Player.route) { PlayerScreen(musicViewModel) }
        }
    }
}
