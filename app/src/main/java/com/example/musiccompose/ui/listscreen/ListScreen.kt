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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musiccompose.models.MediaItemData
import com.google.accompanist.coil.rememberCoilPainter

@ExperimentalMaterialApi
@Composable
fun ListScreen(
    mainViewModel: MainViewModel,
    listViewModel: ListViewModel,
) {
    val listState = rememberLazyListState()
    val songList by listViewModel.songList.observeAsState()

    songList?.let { itemList ->
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Divider(thickness = 5.dp)
            SongList(
                listState,
                itemList,
                mainViewModel::playOrPause,
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
) {
    LazyColumn(state = listState) {
        items(mediaItemDataList) { item ->
            SongCard(
                item,
                playOrPause,
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
) {
    Card(
        onClick = {
            playOrPause(mediaItemData, false)
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
            Text(
                text = mediaItemData.title,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(12.dp)
            )
        }
    }
}