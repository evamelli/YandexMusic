package com.example.yandexmusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yandexmusic.ui.components.SongItem
import com.example.yandexmusic.viewmodel.MusicViewModel

@Composable
fun LibraryScreen(viewModel: MusicViewModel, userId: String) {
    val favorites by viewModel.favorites.collectAsState()

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.fetchFavorites(userId)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            Text(
                "Моя коллекция",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LibraryCategoryItem("Треки", Icons.Default.MusicNote)
                LibraryCategoryItem("Альбомы", Icons.Default.History)
                LibraryCategoryItem("Плейлисты", Icons.Default.Favorite)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Любимые треки",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (favorites.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("Здесь пока ничего нет", color = Color.Gray)
                }
            }
        } else {
            items(favorites) { song ->
                SongItem(
                    song = song,
                    isFavorite = true,
                    onPlayClick = { /* Handle Play */ },
                    onFavoriteClick = { viewModel.toggleFavorite(userId, song) }
                )
            }
        }
    }
}

@Composable
fun LibraryCategoryItem(label: String, icon: ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(label, fontSize = 12.sp, color = Color.White, modifier = Modifier.padding(top = 4.dp))
    }
}
