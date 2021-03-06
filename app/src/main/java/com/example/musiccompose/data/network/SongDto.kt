package com.example.musiccompose.data.network

data class SongDto(
    val mediaId: String = "",
    val title: String = "",
    val artist: String = "",
    val genre: String = "",
    val album: String = "",
    val songUrl: String = "",
    val imageUrl: String = "",
    val duration: Long = -1
)