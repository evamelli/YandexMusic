package com.example.yandexmusic.viewmodel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.yandexmusic.data.MusicRepository
import com.example.yandexmusic.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MusicViewModel(private val repository: MusicRepository = MusicRepository()) : ViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    private val _favorites = MutableStateFlow<List<Song>>(emptyList())
    val favorites: StateFlow<List<Song>> = _favorites

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private var exoPlayer: ExoPlayer? = null
    private var pendingAutoPlay = true
    private var appContext: Context? = null

    private val _currentSongIndex = MutableStateFlow(0)
    val currentSongIndex: StateFlow<Int> = _currentSongIndex

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "yandex_music_playback"
        private const val NOTIFICATION_ID = 1001
    }

    fun fetchSongs() {
        viewModelScope.launch {
            _loading.value = true
            
            // Показываем локальные треки мгновенно
            _songs.value = repository.getLocalSongs()
            if (pendingAutoPlay && exoPlayer != null) {
                pendingAutoPlay = false
                playSong(0)
            }
            
            // Firebase в фоне — обновит список когда загрузится
            val allSongs = repository.getAllSongs()
            _songs.value = allSongs
            if (pendingAutoPlay && exoPlayer != null) {
                pendingAutoPlay = false
                playSong(0)
            }
            
            _loading.value = false
        }
    }

    fun fetchFavorites(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            _favorites.value = repository.getFavorites(userId)
            _loading.value = false
        }
    }

    fun toggleFavorite(userId: String, song: Song) {
        viewModelScope.launch {
            if (_favorites.value.any { it.id == song.id }) {
                repository.removeFromFavorites(userId, song.id)
                _favorites.value = _favorites.value.filter { it.id != song.id }
            } else {
                repository.addToFavorites(userId, song.id)
                _favorites.value = _favorites.value + song
            }
        }
    }

    fun initPlayer(context: Context) {
        if (exoPlayer != null) return
        appContext = context.applicationContext
        createNotificationChannel()
        exoPlayer = ExoPlayer.Builder(context).build().also { player ->
            player.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                    if (!isPlaying) cancelNotification()
                }
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        nextSong()
                    }
                }
            })
        }
        if (pendingAutoPlay && _songs.value.isNotEmpty()) {
            pendingAutoPlay = false
            playSong(0)
        }
    }

    private fun createNotificationChannel() {
        val context = appContext ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Воспроизведение",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Сейчас играет ваша волна" }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    private fun showNowPlayingNotification(song: Song) {
        val context = appContext ?: return
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(song.title)
            .setContentText("Сейчас играет ваша волна")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        nm.notify(NOTIFICATION_ID, notification)
    }

    private fun cancelNotification() {
        val context = appContext ?: return
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIFICATION_ID)
    }

    fun playSong(index: Int) {
        val songs = _songs.value
        if (index < 0 || index >= songs.size) return
        _currentSongIndex.value = index
        val song = songs[index]
        val mediaItem = MediaItem.fromUri(song.audioUrl)
        exoPlayer?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
        showNowPlayingNotification(song)
    }

    fun togglePlayPause() {
        val player = exoPlayer ?: return
        val songs = _songs.value
        if (songs.isEmpty()) return
        if (player.isPlaying) {
            player.pause()
            cancelNotification()
        } else {
            if (player.mediaItemCount == 0) {
                playSong(_currentSongIndex.value)
            } else {
                player.play()
            }
        }
    }

    fun nextSong() {
        val songs = _songs.value
        if (songs.isEmpty()) return
        val next = (_currentSongIndex.value + 1) % songs.size
        playSong(next)
    }

    fun previousSong() {
        val songs = _songs.value
        if (songs.isEmpty()) return
        val prev = if (_currentSongIndex.value - 1 < 0) songs.size - 1 else _currentSongIndex.value - 1
        playSong(prev)
    }

    fun releasePlayer() {
        cancelNotification()
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}
