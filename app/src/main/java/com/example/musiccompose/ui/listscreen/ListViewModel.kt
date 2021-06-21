package com.example.musiccompose.ui.listscreen

import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musiccompose.MusicServiceConnection
import com.example.musiccompose.extensions.urlEncoded
import com.example.musiccompose.models.MediaItemData
import com.example.musiccompose.util.contansts
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private var mediaId: String = ""

    private val _songList = MutableLiveData<List<MediaItemData>>()
    val songList: LiveData<List<MediaItemData>> = _songList

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            super.onChildrenLoaded(parentId, children)
            val items = children.map {
                MediaItemData(
                    it.mediaId!!,
                    it.description.title.toString(),
                    it.description.subtitle.toString(),
                    it.description.mediaUri.toString(),
                    it.description.iconUri.toString(),
                    -99,
                    it.isBrowsable // may be used in future
                )
            }
            _songList.postValue(items)
        }
    }

    fun subscribe(mediaId: String) {
        this.mediaId = mediaId
        musicServiceConnection.subscribe(mediaId, subscriptionCallback)
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(mediaId, subscriptionCallback)
    }

}