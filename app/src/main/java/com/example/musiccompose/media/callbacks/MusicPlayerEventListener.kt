package com.example.musiccompose.media.callbacks

import android.widget.Toast
import com.example.musiccompose.media.MusicNotificationManager
import com.example.musiccompose.media.MusicService
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer

class MusicPlayerEventListener(
    private val musicService: MusicService,
    private val musicNotificationManager: MusicNotificationManager,
) : Player.EventListener {

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        when (playbackState) {
            Player.STATE_BUFFERING -> { musicNotificationManager.showNotification(musicService.exoPlayer) }
            Player.STATE_READY -> {
                musicNotificationManager.showNotification(musicService.exoPlayer)

                if (!playWhenReady) {
                    // If playback is paused we remove the foreground state which allows the
                    // notification to be dismissed.
                    musicService.stopForeground(false)
                    musicService.isForegroundService = false
                }
            }
            else -> {
                musicNotificationManager.hideNotification()
            }
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicService, "An unknown error", Toast.LENGTH_LONG).show()
    }
}


