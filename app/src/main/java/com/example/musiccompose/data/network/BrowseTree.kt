package com.example.musiccompose.data.network

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE
import com.example.musiccompose.R
import com.example.musiccompose.util.contansts.MEDIA_BROWSABLE_ROOT
import com.example.musiccompose.util.contansts.MEDIA_RECOMMENDED_ROOT

class BrowseTree(
    val context: Context,
    musicSource: MusicSource,
    val recentMediaId: String? = null
) {

    private val mediaIdToChildren = mutableMapOf<String, MutableList<MediaMetadataCompat>>()

    init {
        val rootList = mediaIdToChildren[MEDIA_BROWSABLE_ROOT] ?: mutableListOf()

        val recommendedMetadata = MediaMetadataCompat.Builder()
            .putString(METADATA_KEY_MEDIA_ID, MEDIA_RECOMMENDED_ROOT)
            .putString(METADATA_KEY_TITLE, context.getString(R.string.recommended_title))
            .build()
    }
}