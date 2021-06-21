package com.example.musiccompose.ui.listscreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items // import manually
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import com.example.musiccompose.ui.MainViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musiccompose.EMPTY_PLAYBACK_STATE
import com.example.musiccompose.models.MediaItemData
import com.example.musiccompose.ui.base.BottomNavSongBar
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun ListScreen(
    mainViewModel: MainViewModel,
    listViewModel: ListViewModel,
    pagerState: PagerState,
    navigateToSongDetail: (String) -> Unit
) {
    val listState = rememberLazyListState()
    val songList by listViewModel.songList.observeAsState()
    val playbackState by mainViewModel.playbackState.observeAsState(EMPTY_PLAYBACK_STATE)
    val scaffoldState = rememberScaffoldState()
    val scrollState = rememberScrollState()

    val curPlayingSong by mainViewModel.curPlayingSong.observeAsState()

    pagerState.pageCount = songList?.size ?: 0

    songList?.let { itemList ->
        Scaffold(
            scaffoldState = scaffoldState,
            bottomBar = {

            }
        ) {
            Column {
                SongList(
                    listState,
                    itemList,
                    mainViewModel::playOrPause,
                    navigateToSongDetail
                )
            }
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