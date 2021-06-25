package com.example.musiccompose.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song")
data class MediaItemData(
    @PrimaryKey val mediaId: String = "",
    val title: String = "",
    val subtitle: String = "",
    val songUrl: String = "",
    val imageUrl: String = "",
    val duration: Long = -1,
    val browsable: Boolean,
)
