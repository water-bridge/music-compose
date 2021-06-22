package com.example.musiccompose.ui

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.musiccompose.MusicServiceConnection
import com.example.musiccompose.SubscriptionCallback
import com.example.musiccompose.extensions.*
import com.example.musiccompose.models.MediaItemData
import com.example.musiccompose.util.Resource
import com.example.musiccompose.util.contansts.MEDIA_ALBUMS_ROOT
import com.example.musiccompose.util.contansts.MEDIA_ARTISTS_ROOT
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val _albumMediaItems = MutableLiveData<Resource<List<MediaItemData>>>()
    val albumMediaItems: LiveData<Resource<List<MediaItemData>>> = _albumMediaItems

    private val _artistMediaItems = MutableLiveData<Resource<List<MediaItemData>>>()
    val artistMediaItems: LiveData<Resource<List<MediaItemData>>> = _artistMediaItems

    val nowPlayingSong: LiveData<MediaItemData> =
        Transformations.map(musicServiceConnection.nowPlaying) { nowPlaying ->
            MediaItemData(
                nowPlaying?.description?.mediaId ?: "",
                nowPlaying?.description?.title.toString(),
                nowPlaying?.description?.subtitle.toString(),
                nowPlaying?.description?.mediaUri.toString(),
                nowPlaying?.description?.iconUri.toString(),
                nowPlaying.duration,
                false
            )
        }

    private var fromAlbum = true

    val isConnected = musicServiceConnection.isConnected // Todo
    val networkFailure = musicServiceConnection.networkFailure // Todo
    private val nowPlaying = musicServiceConnection.nowPlaying
    val playbackState = musicServiceConnection.playbackState
    private val duration = musicServiceConnection.duration

    private val transportationControls by lazy {
        musicServiceConnection.transportationControls
    }

    private val albumsSubscriptionCallback = SubscriptionCallback { items ->
        _albumMediaItems.postValue(Resource.success(items))
    }

    private val artistsSubscriptionCallback = SubscriptionCallback { items ->
        _artistMediaItems.postValue(Resource.success(items))
    }

    init {
        // Todo the recommendation part is still not implemented yet
        _albumMediaItems.postValue(Resource.loading(null))
        _artistMediaItems.postValue((Resource.loading(null)))
        musicServiceConnection.subscribe(MEDIA_ALBUMS_ROOT, albumsSubscriptionCallback)
        musicServiceConnection.subscribe(MEDIA_ARTISTS_ROOT, artistsSubscriptionCallback)
    }

    fun playOrPause(mediaItem: MediaItemData, pauseAllowed: Boolean) {

        val isPrepared = playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaItem.mediaId == nowPlaying.value?.id) {
            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying ->
                        if (pauseAllowed) {
                            transportationControls.pause()
                        } else Unit
                    playbackState.isPlayEnabled -> transportationControls.play()
                    else -> Unit
                }
            }
        } else {
            transportationControls.playFromMediaId(
                    mediaItem.mediaId,
                    bundleOf("FROM_ALBUM" to fromAlbum)
                )
        }
    }

    fun nextSong() {
        transportationControls.skipToNext()
    }

    fun previousSong() {
        transportationControls.skipToPrevious()
    }

    fun seekTo(value: Float) {
        val position = value * (duration.value ?: -1).toFloat()
        transportationControls.seekTo(position.toLong())
    }

    fun onChangeFromAlbum(value: Boolean) {
        fromAlbum = value
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(MEDIA_ALBUMS_ROOT, albumsSubscriptionCallback)
        musicServiceConnection.unsubscribe(MEDIA_ARTISTS_ROOT, artistsSubscriptionCallback)
    }
}