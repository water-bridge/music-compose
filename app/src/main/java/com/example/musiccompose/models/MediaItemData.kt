package com.example.musiccompose.models

data class MediaItemData(
    val mediaId: String = "",
    val title: String = "",
    val subtitle: String = "",
    val songUrl: String = "",
    val imageUrl: String = "",
    val browsable: Boolean,
)
