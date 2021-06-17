package com.example.musiccompose.extensions

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_URI
import androidx.core.net.toUri
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

inline val MediaMetadataCompat.id: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)

inline val MediaMetadataCompat.artist: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_ARTIST)

inline val MediaMetadataCompat.album: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_ALBUM)

// convert List<MediaMetadataCompat> to List<MediaSource>
fun List<MediaMetadataCompat>.toMediaSource(
    dataSourceFactory: DefaultDataSourceFactory
): ConcatenatingMediaSource {

    val concatenatingMediaSource = ConcatenatingMediaSource()
    forEach { song ->
        val mediaSource = ProgressiveMediaSource
            .Factory(dataSourceFactory)
            .createMediaSource(song.getString(METADATA_KEY_MEDIA_URI).toUri())
        concatenatingMediaSource.addMediaSource(mediaSource)
    }
    return concatenatingMediaSource
}

// convert List<MediaMetadataCompat> to List<MediaItem>
fun List<MediaMetadataCompat>.toMediaItem(): List<MediaBrowserCompat.MediaItem> = map { song ->
    val description = MediaDescriptionCompat.Builder()
        .setMediaUri(song.getString(METADATA_KEY_MEDIA_URI).toUri())
        .setTitle(song.description.title)
        .setSubtitle(song.getString(METADATA_KEY_ARTIST))
        .setMediaId(song.description.mediaId)
        .setIconUri(song.description.iconUri)
        .build()
    MediaBrowserCompat.MediaItem(description, FLAG_PLAYABLE)
}


