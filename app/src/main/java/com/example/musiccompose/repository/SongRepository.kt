package com.example.musiccompose.repository

import com.example.musiccompose.models.MediaItemData
import kotlinx.coroutines.flow.Flow

interface SongRepository {

    fun getAllSong(): Flow<List<MediaItemData>>

    suspend fun insertSong(song: MediaItemData)

    suspend fun deleteSong(song: MediaItemData)
}