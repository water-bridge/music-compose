package com.example.musiccompose.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.musiccompose.models.MediaItemData

@Database(entities = [MediaItemData::class], version = 1)
abstract class SongDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
}