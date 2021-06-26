package com.example.musiccompose.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.musiccompose.models.MediaItemData
import com.example.musiccompose.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val songRepository: SongRepository
) : ViewModel() {
}