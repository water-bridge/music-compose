package com.example.musiccompose

import android.support.v4.media.MediaBrowserCompat
import com.example.musiccompose.models.MediaItemData

class SubscriptionCallback(
    private val onCallback: (List<MediaItemData>) -> Unit
) : MediaBrowserCompat.SubscriptionCallback() {
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
                it.isBrowsable // may be used in future
            )
        }
        onCallback(items)
    }
}