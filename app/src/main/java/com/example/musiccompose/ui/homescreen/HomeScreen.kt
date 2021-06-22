package com.example.musiccompose.ui.homescreen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musiccompose.R
import com.example.musiccompose.models.MediaItemData
import com.example.musiccompose.ui.MainViewModel
import com.example.musiccompose.util.Status.*
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState

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
        albumMediaItems?.let { result ->
            when(result.status) {
                SUCCESS -> {
                    result.data?.let { items ->
                        Spacer(modifier = Modifier.height(20.dp))
                        ContentText(text = stringResource(id = R.string.recommended_title))
                        LazyVerticalGrid(
                            cells = GridCells.Fixed(2),
                            modifier = Modifier.padding(8.dp),
                            contentPadding = PaddingValues(start = 7.5.dp, end = 7.5.dp, top = 20.dp, bottom = 7.5.dp),
                        ) {
                            items(items) { item ->
                                CollectionCell(
                                    item = item,
                                    onChangeFromAlbum = mainViewModel::onChangeFromAlbum,
                                    navigateToSongList = navigateToSongList
                                )
                            }
                        }
                    }
                }
                LOADING -> LoadingScreen()
                else -> Unit
            }
        }

        artistMediaItems?.let { result ->
            when(result.status) {
                SUCCESS -> {
                    result.data?.let { items ->
                        ContentText(text = stringResource(id = R.string.artists_title))
                        ScrollableArtistRow(
                            artistMediaItems = items,
                            scrollState = scrollState,
                            onChangeFromAlbum = mainViewModel::onChangeFromAlbum,
                            navigateToSongList = navigateToSongList
                        )
                    }
                }
                else -> Unit
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun CollectionCell(
    item: MediaItemData,
    onChangeFromAlbum: (Boolean) -> Unit,
    navigateToSongList: (String) -> Unit
) {
    Card(
        onClick = {
            onChangeFromAlbum(true)
            navigateToSongList(item.mediaId)
        },
        modifier = Modifier
            .padding(8.dp),
        elevation = 8.dp,
        //backgroundColor = MaterialTheme.colors.background.copy(0.7f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            val painter = rememberCoilPainter(
                request = item.imageUrl,
                fadeIn = true,
            )
            Image(
                painter = painter,
                null
            )
            when (painter.loadState) {
                is ImageLoadState.Loading -> {
                    CircularProgressIndicator(
                        Modifier
                            .align(CenterHorizontally)
                    )
                }
                is ImageLoadState.Error -> Unit
            }
            Text(
                text = item.title,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
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
            .padding(8.dp)
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
            .padding(8.dp),
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
            Text(
                text = item.title,
                maxLines = 1,
                modifier = Modifier
                    .padding(8.dp)
                    .width(70.dp)
            )
        }
    }
}

@Composable
fun ContentText(
    text: String
) {
    Text(
        text = text,
        fontSize = 20.sp,
        modifier = Modifier.padding(start = 20.dp),
        textAlign = TextAlign.Center
    )
    Divider(
        thickness = 2.dp,
        modifier = Modifier.padding(5.dp)
    )
}

@Composable
fun LoadingScreen() {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(top = 100.dp)
            .wrapContentSize(Alignment.Center)
    )
}