package com.example.musiccompose.ui

import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.musiccompose.MusicServiceConnection
import com.example.musiccompose.extensions.id
import com.example.musiccompose.extensions.isPlayEnabled
import com.example.musiccompose.extensions.isPlaying
import com.example.musiccompose.extensions.isPrepared
import com.example.musiccompose.models.Song
import com.example.musiccompose.util.contansts.MEDIA_BROWSABLE_ROOT
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {


    val rootMedia: LiveData<String> =
        Transformations.map(musicServiceConnection.isConnected) { isConnected ->
            if (isConnected) {
                musicServiceConnection.rootMediaId
            } else {
                null
            }
        }


    private val _mediaItems = MutableLiveData<List<Song>>()
    val mediaItems: LiveData<List<Song>> = _mediaItems

    val isConnected = musicServiceConnection.isConnected
    val networkFailure = musicServiceConnection.networkFailure
    val nowPlaying = musicServiceConnection.nowPlaying
    val playbackState = musicServiceConnection.playbackState

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            super.onChildrenLoaded(parentId, children)
            val items = children.map {
                Song(
                    it.mediaId!!,
                    it.description.title.toString(),
                    it.description.subtitle.toString(),
                    it.description.mediaUri.toString(),
                    it.description.iconUri.toString()
                )
            }
            _mediaItems.postValue(items)
        }
    }

    init {
        musicServiceConnection.subscribe(MEDIA_BROWSABLE_ROOT, subscriptionCallback)
    }

    fun playOrPause(mediaItem: Song, pauseAllowed: Boolean = false) {

        val isPrepared = playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaItem.mediaId == nowPlaying.value?.id) {
            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying ->
                        if (pauseAllowed) musicServiceConnection.transportationControls.pause() else Unit
                    playbackState.isPlayEnabled -> musicServiceConnection.transportationControls.play()
                    else -> Unit
                }
            }
        } else {
            musicServiceConnection.transportationControls.playFromMediaId(mediaItem.mediaId, null)
        }
    }

    fun skipToNextSong() {
        musicServiceConnection.transportationControls.skipToNext()
    }

    fun skipToPreviousSong() {
        musicServiceConnection.transportationControls.skipToPrevious()
    }

    fun seekTo(position: Long) {
        musicServiceConnection.transportationControls.seekTo(position)
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(MEDIA_BROWSABLE_ROOT, subscriptionCallback)
    }
}