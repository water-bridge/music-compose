package com.example.musiccompose.ui.listscreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items // import manually
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import com.example.musiccompose.ui.MainViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musiccompose.models.MediaItemData
import com.google.accompanist.coil.rememberCoilPainter

@ExperimentalMaterialApi
@Composable
fun ListScreen(
    mainViewModel: MainViewModel,
    listViewModel: ListViewModel,
    navigateToSongDetail: (String) -> Unit
) {
    val listState = rememberLazyListState()
    val songList by listViewModel.songList.observeAsState()

    Column {
        songList?.let {
            SongList(
                listState,
                it,
                mainViewModel::playOrPause,
                navigateToSongDetail
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SongList(
    listState: LazyListState,
    mediaItemDataList: List<MediaItemData>,
    playOrPause: (MediaItemData, Boolean) -> Unit,
    navigateToSongDetail: (String) -> Unit
) {
    LazyColumn (state = listState) {
        items(mediaItemDataList) { item ->
            SongCard(
                item,
                playOrPause,
                navigateToSongDetail
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SongCard(
    mediaItemData: MediaItemData,
    playOrPause: (MediaItemData, Boolean) -> Unit,
    navigateToSongDetail: (String) -> Unit
) {
    Card(
        onClick = {
            playOrPause(mediaItemData, false)
            navigateToSongDetail(mediaItemData.mediaId)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        elevation = 8.dp,
        //backgroundColor = MaterialTheme.colors.background.copy(0.7f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row {
            Image(
                painter = rememberCoilPainter(
                    request = mediaItemData.imageUrl,
                    fadeIn = true,
                ),
                null
            )
            Text(text = mediaItemData.title)
        }
    }
}