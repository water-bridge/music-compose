package com.example.musiccompose.ui.songdetail

import androidx.lifecycle.*
import com.example.musiccompose.MusicServiceConnection
import com.example.musiccompose.extensions.currentPlayBackPosition
import com.example.musiccompose.media.MusicService
import com.example.musiccompose.models.MediaItemData
import com.example.musiccompose.util.contansts.UPDATE_PLAYER_POSTION_INTERVAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SongDetailViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val playbackState = musicServiceConnection.playbackState
    private val duration = musicServiceConnection.duration

    private val _curPlayerPosition = MutableLiveData<Long>()
    val curPlayerPosition: LiveData<Long> = _curPlayerPosition

    val curProgress: LiveData<Float> = Transformations.map(_curPlayerPosition) {
        it.toFloat() / (duration.value ?: -1).toFloat()
    }

    init {
        updateCurPlayerPosition()
    }

    private fun updateCurPlayerPosition() {
        viewModelScope.launch {
            while (true) {
                val pos = playbackState.value?.currentPlayBackPosition
                if (curPlayerPosition.value != pos) {
                    _curPlayerPosition.postValue(pos)
                    Timber.d("curPosition: ${_curPlayerPosition.value}")
                    Timber.d("curDuration: ${duration.value}")
                }
                delay(UPDATE_PLAYER_POSTION_INTERVAL)
            }
        }
    }

}