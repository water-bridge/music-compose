package com.example.musiccompose.ui.homescreen

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
import androidx.compose.runtime.livedata.observeAsState
import com.example.musiccompose.ui.MainViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musiccompose.models.Song
import com.google.accompanist.coil.rememberCoilPainter

@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    viewModel: MainViewModel
) {
    val songList by viewModel.mediaItems.observeAsState()
    // remember scroll position
    val listState = rememberLazyListState()

    Column {
        songList?.let {
            SongList(
                listState,
                it,
                viewModel::playOrPause
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SongList(
    listState: LazyListState,
    songList: List<Song>,
    onClickPlaySong: (Song) -> Unit
) {
    LazyColumn (state = listState) {
        items(songList) { item ->  
            SongCard(
                item,
                onClickPlaySong
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SongCard(
    song: Song,
    onClickPlaySong: (Song) -> Unit
) {
    Card(
        onClick = { onClickPlaySong(song) },
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        elevation = 8.dp,
        //backgroundColor = MaterialTheme.colors.background.copy(0.7f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row {
            Image(
                painter = rememberCoilPainter(
                    request = song.imageUrl,
                    fadeIn = true,
                ),
                null
            )
            Text(text = song.title)
        }
    }
}