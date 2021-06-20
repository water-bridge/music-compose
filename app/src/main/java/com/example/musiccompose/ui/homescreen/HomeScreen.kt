package com.example.musiccompose.ui.homescreen

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musiccompose.models.MediaItemData
import com.example.musiccompose.ui.MainViewModel
import com.example.musiccompose.ui.listscreen.SongList
import com.google.accompanist.coil.rememberCoilPainter

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    navigateToSongList: (String) -> Unit
) {
    val albumMediaItems by mainViewModel.albumMediaItems.observeAsState()
    val artistMediaItems by mainViewModel.artistMediaItems.observeAsState()
    val scrollState = rememberScrollState()

    Column {
        albumMediaItems?.let {
            LazyVerticalGrid(cells = GridCells.Fixed(2)) {
                items(it) { item ->
                    CollectionCell(
                        item = item,
                        onChangeFromAlbum = mainViewModel::onChangeFromAlbum,
                        navigateToSongList = navigateToSongList
                    )
                }
            }
        }

        artistMediaItems?.let {
            ScrollableArtistRow(
                artistMediaItems = it,
                scrollState = scrollState,
                onChangeFromAlbum = mainViewModel::onChangeFromAlbum,
                navigateToSongList = navigateToSongList
            )
        }
    }
}

@Composable
fun CollectionCell(
    item: MediaItemData,
    onChangeFromAlbum: (Boolean) -> Unit,
    navigateToSongList: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                onChangeFromAlbum(true)
                navigateToSongList(item.mediaId)
            }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = rememberCoilPainter(
                    request = item.imageUrl,
                    fadeIn = true,
                ),
                null
            )
            Text(text = item.title)
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun ScrollableArtistRow(
    artistMediaItems: List<MediaItemData>,
    scrollState: ScrollState,
    onChangeFromAlbum: (Boolean) -> Unit,
    navigateToSongList: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
    ) {
        artistMediaItems.forEach {
            ArtistCard(
                it,
                onChangeFromAlbum,
                navigateToSongList
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun ArtistCard(
    item: MediaItemData,
    onChangeFromAlbum: (Boolean) -> Unit,
    navigateToSongList: (String) -> Unit
) {
    Card(
        onClick = {
            onChangeFromAlbum(false)
            navigateToSongList(item.mediaId)
        },
        modifier = Modifier
            .width(160.dp)
            .height(70.dp),
        elevation = 8.dp,
        //backgroundColor = MaterialTheme.colors.background.copy(0.7f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row {
            Image(
                painter = rememberCoilPainter(
                    request = item.imageUrl,
                    fadeIn = true,
                ),
                contentDescription = null,
                modifier = Modifier.width(70.dp)
            )
            Text(text = item.title)
        }
    }
}