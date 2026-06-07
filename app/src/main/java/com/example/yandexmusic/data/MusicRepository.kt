package com.example.yandexmusic.data

import com.example.yandexmusic.model.Song
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import android.util.Log

class MusicRepository(private val db: FirebaseDatabase = FirebaseDatabase.getInstance()) {

    private val songsRef = db.getReference("songs")
    private val favoritesRef = db.getReference("favorites")

    private val localSongs = listOf(
        Song(
            id = "local_1",
            title = "posterboy",
            artist = "2hollis",
            coverUrl = "posterboy", 
            audioUrl = "android.resource://com.example.yandexmusic/raw/posterboy"
        ),
        Song(
            id = "local_2",
            title = "BANDITAS",
            artist = "2KE",
            coverUrl = "banditas", 
            audioUrl = "android.resource://com.example.yandexmusic/raw/banditas"
        ),
        Song(
            id = "local_3",
            title = "I will survive x survivor",
            artist = "Glee Cast",
            coverUrl = "cover_i_will_survive",
            audioUrl = "android.resource://com.example.yandexmusic/raw/i_will_survive"
        )
    )

    fun getLocalSongs(): List<Song> = localSongs

    suspend fun getAllSongs(): List<Song> {
        return try {
            val snapshot = songsRef.get().await()
            val firebaseSongs = snapshot.children.mapNotNull { it.getValue(Song::class.java)?.copy(id = it.key ?: "") }
            
            // ALWAYS combine local songs with Firebase songs so user sees them
            (localSongs + firebaseSongs).distinctBy { it.title }
        } catch (e: Exception) {
            Log.e("MusicRepository", "Error fetching songs: ${e.message}")
            localSongs
        }
    }

    suspend fun getFavorites(userId: String): List<Song> {
        if (userId.isEmpty()) return emptyList()
        return try {
            val snapshot = favoritesRef.child(userId).get().await()
            val songIds = snapshot.children.mapNotNull { it.key }
            val allSongs = getAllSongs()
            allSongs.filter { it.id in songIds }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addToFavorites(userId: String, songId: String) {
        if (userId.isEmpty()) return
        try {
            favoritesRef.child(userId).child(songId).setValue(true).await()
        } catch (e: Exception) {
            Log.e("MusicRepository", "Error adding to favorites: ${e.message}")
        }
    }

    suspend fun removeFromFavorites(userId: String, songId: String) {
        if (userId.isEmpty()) return
        try {
            favoritesRef.child(userId).child(songId).removeValue().await()
        } catch (e: Exception) {
            Log.e("MusicRepository", "Error removing from favorites: ${e.message}")
        }
    }
}
