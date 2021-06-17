package com.example.musiccompose.media

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.example.musiccompose.data.network.MusicSource
import com.example.musiccompose.extensions.artist
import com.example.musiccompose.extensions.toMediaItem
import com.example.musiccompose.extensions.toMediaSource
import com.example.musiccompose.media.callbacks.MusicPlaybackPreparer
import com.example.musiccompose.media.callbacks.MusicPlayerEventListener
import com.example.musiccompose.media.callbacks.MusicPlayerNotificationListener
import com.example.musiccompose.util.contansts.MEDIA_BROWSABLE_ROOT
import com.example.musiccompose.util.contansts.NETWORK_FAILURE
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

private const val SERVICE_TAG = "MusicService"

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat(){

    @Inject
    lateinit var dataSourceFactory: DefaultDataSourceFactory

    @Inject
    lateinit var exoPlayer: SimpleExoPlayer

    @Inject
    lateinit var firebaseMusicSource: MusicSource
    private var currentPlaylistItems: List<MediaMetadataCompat> = emptyList()

    private lateinit var musicNotificationManager: MusicNotificationManager

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    var isForegroundService = false

    private var curPlayingSong: MediaMetadataCompat? = null

    private val isPlayerInitialized = false

    private lateinit var musicPlayerEventListener: MusicPlayerEventListener

    companion object {
        var curSongDuration = 0L
            private set
    }

    override fun onCreate() {
        super.onCreate()

        serviceScope.launch {
            firebaseMusicSource.load()
        }

        val sessionActivityIntent = packageManager?.getLeanbackLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, 0)
        }

        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(sessionActivityIntent)
            isActive = true
        }

        sessionToken = mediaSession.sessionToken

        musicNotificationManager = MusicNotificationManager(
            this,
            mediaSession.sessionToken,
            MusicPlayerNotificationListener(this)
        )

        musicPlayerEventListener = MusicPlayerEventListener(
            this,
            musicNotificationManager
        )
        exoPlayer.addListener(musicPlayerEventListener)

        curSongDuration = exoPlayer.duration

        val musicPlaybackPreparer = MusicPlaybackPreparer(firebaseMusicSource) { itemToPlay ->
            preparePlaylist(
                metadataList = buildPlaylist(itemToPlay),
                itemToPlay = itemToPlay,
                playWhenReady = true
            )
        }

        mediaSessionConnector = MediaSessionConnector(mediaSession).apply {
            setPlaybackPreparer(musicPlaybackPreparer)
            setQueueNavigator(MusicQueueNavigator())
            setPlayer(exoPlayer)
        }

        musicNotificationManager.showNotification(exoPlayer)

    }

    private inner class MusicQueueNavigator : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return currentPlaylistItems[windowIndex].description
        }
    }

    private fun preparePlaylist(
        metadataList: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playWhenReady: Boolean,
        //playbackStartPositionMs: Long
    ) {
        val initialWindowIndex = if (itemToPlay == null) 0 else metadataList.indexOf(itemToPlay)
        // Todo may cause problems?
        currentPlaylistItems = metadataList

        exoPlayer.playWhenReady = playWhenReady
        exoPlayer.stop(true)
        exoPlayer.prepare(metadataList.toMediaSource(dataSourceFactory))
        // for simplicity
        exoPlayer.seekTo(initialWindowIndex, 0L)
    }

    /**
     * Builds a playlist based on a [MediaMetadataCompat].
     *
     * TODO: Support building a playlist by artist, genre, etc...
     *
     * @param item Item to base the playlist on.
     * @return a [List] of [MediaMetadataCompat] objects representing a playlist.
     */
    private fun buildPlaylist(item: MediaMetadataCompat): List<MediaMetadataCompat> =
        firebaseMusicSource.filter { it.artist == item.artist }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // exoPlayer is still not release yet.!! we need to call release() when the service is destroyed
        exoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()

        exoPlayer.removeListener(musicPlayerEventListener)
        exoPlayer.release()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        // Todo
        return BrowserRoot(MEDIA_BROWSABLE_ROOT, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        when (parentId) {
            MEDIA_BROWSABLE_ROOT -> {
                val resultSent = firebaseMusicSource.whenReady { successfullyInitialized ->
                    if (successfullyInitialized) {
                        val children = firebaseMusicSource.toList().toMediaItem() // Todo
                        result.sendResult(children.toMutableList())
                    } else {
                        mediaSession.sendSessionEvent(NETWORK_FAILURE, null)
                        result.sendResult(null)
                    }
                }

                if (!resultSent) {
                    result.detach()
                }
            }
            else -> Unit
        }
    }
}