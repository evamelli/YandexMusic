package com.example.yandexmusic.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yandexmusic.ui.components.SongItem
import com.example.yandexmusic.viewmodel.MusicViewModel

@Composable
fun HomeScreen(viewModel: MusicViewModel, userId: String) {
    val songs by viewModel.songs.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        viewModel.fetchSongs()
        if (userId.isNotEmpty()) {
            viewModel.fetchFavorites(userId)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Трек, артист, альбом", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Text(
                    "Популярные артисты",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                ArtistRow()
            }

            item {
                Text(
                    "Ваша волна",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(songs.filter { it.title.contains(searchQuery, ignoreCase = true) || it.artist.contains(searchQuery, ignoreCase = true) }) { song ->
                SongItem(
                    song = song,
                    isFavorite = favorites.any { it.id == song.id },
                    onPlayClick = { 
                        Toast.makeText(context, "Сейчас играет ваша волна", Toast.LENGTH_SHORT).show()
                    },
                    onFavoriteClick = { viewModel.toggleFavorite(userId, song) }
                )
            }
        }
    }
}

@Composable
fun ArtistRow() {
    val artists = listOf("Tame Impala", "Radiohead", "XXXtentacion", "Kendrick Lamar", "Дора", "Coldplay")
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
        items(artists) { artist ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 16.dp).width(80.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(artist.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Text(
                    artist,
                    maxLines = 1,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp),
                    color = Color.Gray
                )
            }
        }
    }
}
