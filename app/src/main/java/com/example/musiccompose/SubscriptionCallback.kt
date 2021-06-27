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
                mediaId = it.mediaId!!,
                title = it.description.title.toString(),
                subtitle = it.description.subtitle.toString(),
                songUrl = it.description.mediaUri.toString(),
                imageUrl = it.description.iconUri.toString(),
                duration = -99,
                browsable = it.isBrowsable // may be used in future
            )
        }
        onCallback(items)
    }
}