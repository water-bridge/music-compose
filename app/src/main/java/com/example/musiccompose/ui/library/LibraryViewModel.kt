package com.example.musiccompose.ui.library

import androidx.lifecycle.ViewModel
import com.example.musiccompose.data.local.SongDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val songDao: SongDao
) : ViewModel() {


}