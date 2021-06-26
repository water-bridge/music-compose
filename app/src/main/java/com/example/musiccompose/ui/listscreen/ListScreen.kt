package com.example.musiccompose.ui.listscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items // import manually
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import com.example.musiccompose.ui.MainViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musiccompose.R
import com.example.musiccompose.models.MediaItemData
import com.example.musiccompose.ui.base.ContentText
import com.example.musiccompose.ui.base.SongCard
import com.google.accompanist.coil.rememberCoilPainter

@ExperimentalMaterialApi
@Composable
fun ListScreen(
    mainViewModel: MainViewModel,
    listViewModel: ListViewModel
) {
    val listState = rememberLazyListState()
    val songList by listViewModel.songList.observeAsState()
    val librarySongList by mainViewModel.librarySongList.observeAsState(emptyList())

    songList?.let { itemList ->
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            ContentText(stringResource(id = R.string.song_list_title))
            SongList(
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
fun SongList(
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


