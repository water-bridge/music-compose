package com.example.musiccompose.models

import androidx.room.Entity
import androidx.room.PrimaryKey

// mediaItemData might not be a song. It may be a album ro artist
// it is used to hold the subscription from the service

@Entity(tableName = "song")
data class MediaItemData(
    @PrimaryKey val mediaId: String = "",
    val title: String = "",
    val subtitle: String = "",
    val songUrl: String = "",
    val imageUrl: String = "",
    val duration: Long = -1,
    // Todo
    val browsable: Boolean,
)
