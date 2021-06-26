package com.example.musiccompose.repository

import com.example.musiccompose.data.local.SongDao
import com.example.musiccompose.models.MediaItemData
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityScoped
class SongRepositoryImpl @Inject constructor(
    private val songDao: SongDao
) : SongRepository {

    override fun getAllSong(): Flow<List<MediaItemData>> = songDao.getAllSong()

    override suspend fun insertSong(song: MediaItemData) = songDao.insert(song)

    override suspend fun deleteSong(song: MediaItemData) = songDao.delete(song)
}