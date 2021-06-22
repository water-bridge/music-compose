package com.example.musiccompose

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musiccompose.extensions.duration
import com.example.musiccompose.media.MusicService
import com.example.musiccompose.util.Event
import com.example.musiccompose.util.Resource
import com.example.musiccompose.util.contansts.NETWORK_FAILURE

class MusicServiceConnection(
    context: Context
) {

    private val _duration = MutableLiveData<Long>()
    val duration: LiveData<Long> = _duration

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> = _isConnected

    private val _networkFailure = MutableLiveData<Boolean>()
    val networkFailure: LiveData<Boolean> = _networkFailure

    private val _playbackState = MutableLiveData<PlaybackStateCompat>()
        .apply { postValue(EMPTY_PLAYBACK_STATE) }
    val playbackState: LiveData<PlaybackStateCompat> = _playbackState

    private val _nowPlaying = MutableLiveData<MediaMetadataCompat>()
        .apply { postValue(NOTHING_PLAYING) }
    val nowPlaying: LiveData<MediaMetadataCompat> = _nowPlaying

    lateinit var mediaController: MediaControllerCompat

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(
            context,
            MusicService::class.java
        ),
        mediaBrowserConnectionCallback,
        null
    ).apply { connect() }

    val transportationControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }

    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
            _isConnected.postValue(true)
        }

        override fun onConnectionSuspended() {
            _isConnected.postValue(false)
        }

        override fun onConnectionFailed() {
            _isConnected.postValue(false)
        }
    }

    private inner class MediaControllerCallback() : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            _nowPlaying.postValue(
                if (metadata?.getString(METADATA_KEY_MEDIA_ID) == null) {
                    NOTHING_PLAYING
                } else {
                    metadata
                }
            )
            _duration.postValue(metadata?.duration ?: -1)
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playbackState.postValue(state ?: EMPTY_PLAYBACK_STATE)
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when (event) {
                NETWORK_FAILURE -> _networkFailure.postValue(true)
            }
        }
    }
}

@Suppress("PropertyName")
val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()

@Suppress("PropertyName")
val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()