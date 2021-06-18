package com.example.musiccompose.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.musiccompose.MusicServiceConnection
import com.example.musiccompose.SubscriptionCallback
import com.example.musiccompose.extensions.id
import com.example.musiccompose.extensions.isPlayEnabled
import com.example.musiccompose.extensions.isPlaying
import com.example.musiccompose.extensions.isPrepared
import com.example.musiccompose.models.MediaItemData
import com.example.musiccompose.util.contansts.MEDIA_ALBUMS_ROOT
import com.example.musiccompose.util.contansts.MEDIA_ARTISTS_ROOT
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

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


    private val _albumMediaItems = MutableLiveData<List<MediaItemData>>()
    val albumMediaItems: LiveData<List<MediaItemData>> = _albumMediaItems

    private val _artistMediaItems = MutableLiveData<List<MediaItemData>>()
    val artistMediaItems: LiveData<List<MediaItemData>> = _artistMediaItems

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    val isConnected = musicServiceConnection.isConnected // Todo
    val networkFailure = musicServiceConnection.networkFailure // Todo
    val nowPlaying = musicServiceConnection.nowPlaying
    val playbackState = musicServiceConnection.playbackState

    private val albumsSubscriptionCallback = SubscriptionCallback { items ->
        _albumMediaItems.postValue(items)
    }

    private val artistsSubscriptionCallback = SubscriptionCallback { items ->
        _artistMediaItems.postValue(items)
    }


    init {
        // Todo the recommendation part is still not implemented yet
        musicServiceConnection.subscribe(MEDIA_ALBUMS_ROOT, albumsSubscriptionCallback)
        musicServiceConnection.subscribe(MEDIA_ARTISTS_ROOT, artistsSubscriptionCallback)
    }

    fun playOrPause(mediaItem: MediaItemData, pauseAllowed: Boolean) {

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

    fun onChangeIsPlaying(value: Boolean) {
        _isPlaying.value = value
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(MEDIA_ALBUMS_ROOT, albumsSubscriptionCallback)
        musicServiceConnection.unsubscribe(MEDIA_ARTISTS_ROOT, artistsSubscriptionCallback)
    }
}