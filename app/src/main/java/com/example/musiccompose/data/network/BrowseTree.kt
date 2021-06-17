package com.example.musiccompose.data.network

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import com.example.musiccompose.R
import com.example.musiccompose.extensions.album
import com.example.musiccompose.util.contansts.FAKE_ALBUM_ART_URL
import com.example.musiccompose.util.contansts.MEDIA_ALBUMS_ROOT
import com.example.musiccompose.util.contansts.MEDIA_BROWSABLE_ROOT
import com.example.musiccompose.util.contansts.MEDIA_RECOMMENDED_ROOT

class BrowseTree(
    val context: Context,
    musicSource: MusicSource,
    val recentMediaId: String? = null
) {
    // Todo all
    private val mediaIdToChildren = mutableMapOf<String, MutableList<MediaMetadataCompat>>()

    init {
        val rootList = mediaIdToChildren[MEDIA_BROWSABLE_ROOT] ?: mutableListOf()

        val recommendedMetadata = MediaMetadataCompat.Builder()
            .putString(METADATA_KEY_MEDIA_ID, MEDIA_RECOMMENDED_ROOT)
            .putString(METADATA_KEY_TITLE, context.getString(R.string.recommended_title))
            .putString(METADATA_KEY_ALBUM_ART_URI, FAKE_ALBUM_ART_URL)
            .build()

        val albumsMetadata = MediaMetadataCompat.Builder()
            .putString(METADATA_KEY_MEDIA_ID, MEDIA_ALBUMS_ROOT)
            .putString(METADATA_KEY_TITLE, context.getString(R.string.albums_title))
            .putString(METADATA_KEY_ALBUM_ART_URI, FAKE_ALBUM_ART_URL)
            .build()

        rootList += recommendedMetadata
        rootList += albumsMetadata
        mediaIdToChildren[MEDIA_BROWSABLE_ROOT] = rootList


    }
}

