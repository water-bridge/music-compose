package com.example.musiccompose.data.local

import androidx.room.*

import com.example.musiccompose.models.MediaItemData
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Query("SELECT * FROM song")
    fun getAllSong(): Flow<List<MediaItemData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: MediaItemData)

    @Delete
    suspend fun delete(song: MediaItemData)

}