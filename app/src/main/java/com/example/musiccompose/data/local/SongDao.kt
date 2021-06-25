package com.example.musiccompose.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.example.musiccompose.models.MediaItemData
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Query("SELECT * FROM song")
    fun getAll(): Flow<List<MediaItemData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: MediaItemData)
}