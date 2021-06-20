package com.example.musiccompose.data.network

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import com.example.musiccompose.models.SongDto
import com.example.musiccompose.util.contansts.SONG_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.musiccompose.data.network.State.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
        update()?.let { updatedCatlog ->
            catalog = updatedCatlog
            state = STATE_INITIALIZED
        } ?: run {
            catalog = emptyList()
            state = STATE_ERROR
        }
    }

    suspend fun update(): List<MediaMetadataCompat>? = withContext(Dispatchers.IO){
        try {
            val songCompat = songCollection.get().await().toObjects(SongDto::class.java).map { song ->
                MediaMetadataCompat.Builder()
                    .putString(METADATA_KEY_ARTIST, song.artist)
                    .putString(METADATA_KEY_MEDIA_ID, song.mediaId)
                    .putString(METADATA_KEY_TITLE, song.title)
                    .putString(METADATA_KEY_ALBUM, song.album)
                    .putString(METADATA_KEY_GENRE, song.genre)
                    .putString(METADATA_KEY_DISPLAY_TITLE, song.title)
                    .putString(METADATA_KEY_DISPLAY_ICON_URI, song.imageUrl)
                    .putString(METADATA_KEY_MEDIA_URI, song.songUrl)
                    .putString(METADATA_KEY_ALBUM_ART_URI, song.imageUrl)
                    .build()
            }
            songCompat
        } catch(e: Exception) {
            return@withContext null
        }
    }

}
