package com.example.musiccompose.media.callbacks

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.example.musiccompose.data.network.MusicSource
import com.example.musiccompose.extensions.album
import com.example.musiccompose.extensions.artist
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector

class MusicPlaybackPreparer(
    private val firebaseMusicSource: MusicSource,
    private val preparePlaylist: (List<MediaMetadataCompat>, MediaMetadataCompat) -> Unit
) : MediaSessionConnector.PlaybackPreparer {

    override fun onCommand(
        player: Player,
        controlDispatcher: ControlDispatcher,
        command: String,
        extras: Bundle?,
        cb: ResultReceiver?
    ): Boolean = false

    override fun getSupportedPrepareActions(): Long {
        return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
    }

    override fun onPrepare(playWhenReady: Boolean) = Unit

    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
        firebaseMusicSource.whenReady {
            val itemToPlay: MediaMetadataCompat? = firebaseMusicSource.find { mediaId == it.description.mediaId }
            itemToPlay?.let {

                preparePlaylist(
                    buildPlaylist(it, extras?.getBoolean("FROM_ALBUM") ?: true),
                    it
                )
            } ?: run {
                // Todo handle error
            }
        }
    }

    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) = Unit

    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) = Unit

    // for simplicity
    private fun buildPlaylist(
        item: MediaMetadataCompat,
        fromAlbum: Boolean
    ): List<MediaMetadataCompat> = if (fromAlbum) {
        firebaseMusicSource.filter { it.album == item.album }
    } else {
        firebaseMusicSource.filter { it.artist == item.artist }
    }

}