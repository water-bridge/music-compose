package com.example.musiccompose.data.network

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import com.example.musiccompose.util.contansts.SONG_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.musiccompose.data.network.State.*
import com.example.musiccompose.extensions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

class FirebaseSource : AbstractMusicSource() {

    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(SONG_COLLECTION)

    // source to be provided
    private var catalog: List<MediaMetadataCompat> = emptyList()

    init {
        state = STATE_INITIALIZING
    }

    override fun iterator(): Iterator<MediaMetadataCompat> = catalog.iterator()

    override suspend fun load() {
        updateCatalog()?.let { updatedCatlog ->
            catalog = updatedCatlog
            state = STATE_INITIALIZED
        } ?: run {
            catalog = emptyList()
            state = STATE_ERROR
        }
    }

    suspend fun updateCatalog(): List<MediaMetadataCompat>? = withContext(Dispatchers.IO){
        try {
            val songCompat = songCollection.get().await().toObjects(SongDto::class.java).map { song ->
                val durationMs = TimeUnit.SECONDS.toMillis(song.duration)
                Timber.d("duration: $durationMs")
                MediaMetadataCompat.Builder().apply {
                    artist = song.artist
                    id = song.mediaId
                    title = song.title
                    album = song.album
                    genre = song.genre
                    displayTitle = song.title
                    displayIconUri = song.imageUrl
                    mediaUri = song.songUrl
                    albumArtUri = song.imageUrl
                    duration = durationMs
                }.build()
            }
            songCompat
        } catch(e: Exception) {
            return@withContext null
        }
    }

}
