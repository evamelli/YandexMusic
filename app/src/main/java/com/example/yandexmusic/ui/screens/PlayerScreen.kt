package com.example.yandexmusic.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yandexmusic.R
import com.example.yandexmusic.model.Song
import com.example.yandexmusic.viewmodel.MusicViewModel

@Composable
fun PlayerScreen(viewModel: MusicViewModel) {
    val context = LocalContext.current
    val songs by viewModel.songs.collectAsState()
    val currentSongIndex by viewModel.currentSongIndex.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    val currentSong = songs.getOrNull(currentSongIndex)

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        viewModel.initPlayer(context)
        viewModel.fetchSongs()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (currentSong != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Card(
                            modifier = Modifier.size(200.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            CoverImage(currentSong.coverUrl)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(currentSong.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(currentSong.artist, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.previousSong() }) {
                                Text("<<", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                            IconButton(
                                onClick = { viewModel.togglePlayPause() },
                                modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(28.dp))
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = Color.Black
                                )
                            }
                            IconButton(onClick = { viewModel.nextSong() }) {
                                Text(">>", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                        }
                    }
                } else {
                    Text("Загрузка...", color = Color.Gray)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Все треки",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        itemsIndexed(songs) { index, song ->
            PlayerSongItem(
                song = song,
                isCurrent = index == currentSongIndex,
                isPlaying = index == currentSongIndex && isPlaying,
                onClick = { viewModel.playSong(index) }
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun CoverImage(coverUrl: String) {
    val imageRes = when (coverUrl) {
        "cover_i_will_survive" -> R.drawable.cover_i_will_survive
        "posterboy" -> R.drawable.cover_posterboy
        "banditas" -> R.drawable.cover_banditas
        else -> null
    }

    if (imageRes != null) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
        }
    }
}

@Composable
private fun PlayerSongItem(
    song: Song,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            val imageRes = when (song.coverUrl) {
                "cover_i_will_survive" -> R.drawable.cover_i_will_survive
                "posterboy" -> R.drawable.cover_posterboy
                "banditas" -> R.drawable.cover_banditas
                else -> null
            }
            if (imageRes != null) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else if (isCurrent && isPlaying) {
                Icon(Icons.Default.Pause, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            } else {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = if (isCurrent) MaterialTheme.colorScheme.primary else Color.Gray, modifier = Modifier.size(24.dp))
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrent) MaterialTheme.colorScheme.primary else Color.White
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}
