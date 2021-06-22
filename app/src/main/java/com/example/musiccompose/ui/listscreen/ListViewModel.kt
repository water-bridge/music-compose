package com.example.musiccompose.ui.listscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musiccompose.MusicServiceConnection
import com.example.musiccompose.SubscriptionCallback
import com.example.musiccompose.models.MediaItemData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private var mediaId: String = ""

    private val _songList = MutableLiveData<List<MediaItemData>>()
    val songList: LiveData<List<MediaItemData>> = _songList

    private val songsSubscriptionCallback = SubscriptionCallback { items ->
        _songList.postValue(items)
    }

    fun subscribe(mediaId: String) {
        this.mediaId = mediaId
        musicServiceConnection.subscribe(mediaId, songsSubscriptionCallback)
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(mediaId, songsSubscriptionCallback)
    }

}