package com.example.musiccompose.ui

import android.support.v4.media.MediaMetadataCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.musiccompose.MusicServiceConnection
import com.example.musiccompose.NOTHING_PLAYING
import com.example.musiccompose.SubscriptionCallback
import com.example.musiccompose.extensions.*
import com.example.musiccompose.models.MediaItemData
import com.example.musiccompose.util.contansts.MEDIA_ALBUMS_ROOT
import com.example.musiccompose.util.contansts.MEDIA_ARTISTS_ROOT
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
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

    private var fromAlbum = true

    val curPlayingSong: LiveData<MediaItemData> =
        Transformations.map(musicServiceConnection.nowPlaying) { nowPlaying ->
            val g = MediaItemData(
                nowPlaying?.description?.mediaId ?: "",
                nowPlaying?.description?.title.toString(),
                nowPlaying?.description?.subtitle.toString(),
                nowPlaying?.description?.mediaUri.toString(),
                nowPlaying?.description?.iconUri.toString(),
                nowPlaying.duration,
                false
            )
            Timber.d("current playing song g ${g.mediaId}")
            Timber.d("current playing song g ${g.title}")
            Timber.d("current playing song g ${g.subtitle}")
            Timber.d("current playing song g ${g.songUrl}")
            Timber.d("current playing song g ${g.imageUrl}")
            Timber.d("current playing song g ${g.duration}")
            g
        } // Todo

    val isConnected = musicServiceConnection.isConnected // Todo
    val networkFailure = musicServiceConnection.networkFailure // Todo
    private val nowPlaying = musicServiceConnection.nowPlaying
    val playbackState = musicServiceConnection.playbackState
    private val duration = musicServiceConnection.duration

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
                        if (pauseAllowed) {
                            musicServiceConnection.transportationControls.pause()
                        } else Unit
                    playbackState.isPlayEnabled -> musicServiceConnection.transportationControls.play()
                    else -> Unit
                }
            }
        } else {
            musicServiceConnection.transportationControls
                .playFromMediaId(
                    mediaItem.mediaId,
                    bundleOf("FROM_ALBUM" to fromAlbum)
                )
        }
    }

    fun nextSong() {
        musicServiceConnection.transportationControls.skipToNext()
    }

    fun previousSong() {
        musicServiceConnection.transportationControls.skipToPrevious()
    }

    fun seekTo(value: Float) {
        val position = value * (duration.value ?: -1).toFloat()
        musicServiceConnection.transportationControls.seekTo(position.toLong())
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