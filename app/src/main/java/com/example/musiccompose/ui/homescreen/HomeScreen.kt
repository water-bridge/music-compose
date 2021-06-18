package com.example.musiccompose.ui.homescreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
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

@ExperimentalFoundationApi
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    navigateToSongList: (String) -> Unit
) {
    val albumMediaItems by mainViewModel.albumMediaItems.observeAsState()
    val artistMediaItems by mainViewModel.artistMediaItems.observeAsState()

    Column {
        albumMediaItems?.let {
            LazyVerticalGrid(cells = GridCells.Fixed(2)) {
                items(it) { item ->
                    CollectionCell(
                        item = item,
                        onClick = navigateToSongList
                    )
                }
            }
        }

        artistMediaItems?.let {
            LazyVerticalGrid(cells = GridCells.Fixed(2)) {
                items(it) { item ->
                    CollectionCell(
                        item = item,
                        onClick = navigateToSongList
                    )
                }
            }
        }
    }
}

@Composable
fun CollectionCell(
    item: MediaItemData,
    onClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick(item.mediaId) }
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