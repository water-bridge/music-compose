package com.example.musiccompose.models

data class Song(
    val mediaId: String = "",
    val title: String = "",
    val artist: String = "",
    val genre: String = "",
    val album: String = "",
    val songUrl: String = "",
    val imageUrl: String = ""
)