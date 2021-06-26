package com.example.musiccompose.di

import com.example.musiccompose.data.local.SongDao
import com.example.musiccompose.repository.SongRepository
import com.example.musiccompose.repository.SongRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideSongRepository(songDao: SongDao) : SongRepository =
        SongRepositoryImpl(
            songDao
        )
}