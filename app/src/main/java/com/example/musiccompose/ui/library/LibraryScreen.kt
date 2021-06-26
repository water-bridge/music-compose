package com.example.musiccompose.ui.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.musiccompose.R
import com.example.musiccompose.models.MediaItemData
import com.example.musiccompose.ui.MainViewModel
import com.example.musiccompose.ui.base.ContentText
import com.example.musiccompose.ui.base.SongCard

@ExperimentalMaterialApi
@Composable
fun LibraryScreen(
    mainViewModel: MainViewModel
) {
    val listState = rememberLazyListState()
    val songList by mainViewModel.librarySongList.observeAsState()
    val librarySongList by mainViewModel.librarySongList.observeAsState(emptyList())

    songList?.let { itemList ->
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            ContentText(stringResource(id = R.string.library_title))
            LibraryList(
                listState = listState,
                songList = itemList,
                librarySongList = librarySongList,
                playOrPause = mainViewModel::playOrPause,
                insertSong = mainViewModel::insertSong,
                deleteSong = mainViewModel::deleteSong
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun LibraryList(
    listState: LazyListState,
    songList: List<MediaItemData>,
    librarySongList: List<MediaItemData>,
    playOrPause: (MediaItemData, Boolean) -> Unit,
    insertSong: (MediaItemData) -> Unit,
    deleteSong: (MediaItemData) -> Unit
) {

    LazyColumn(state = listState) {
        items(songList) { item ->
            SongCard(
                mediaItemData = item,
                playOrPause = playOrPause,
                isInLibrary = librarySongList.contains(item),
                insertSong = insertSong,
                deleteSong = deleteSong
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


