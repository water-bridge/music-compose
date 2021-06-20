package com.example.musiccompose.ui.songdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import com.example.musiccompose.ui.MainViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import com.example.musiccompose.models.MediaItemData
import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun SongDetailScreen(
    mainViewModel: MainViewModel,
    songDetailViewModel: SongDetailViewModel
) {
    val curPlayingSong by mainViewModel.curPlayingSong.observeAsState()

    curPlayingSong?.let {
        Column {
            SongArtView(
                curPlayingSong = it,
                modifier = Modifier
                    .fillMaxSize(),
                mainViewModel::nextSong,
                mainViewModel::previousSong,
                mainViewModel::playOrPause
            )
        }
    }


}

@Composable
fun SongArtView(
    curPlayingSong: MediaItemData,
    modifier: Modifier,
    nextSong: () -> Unit,
    previousSong: () -> Unit,
    playOrPause: (MediaItemData, Boolean) -> Unit
) {
    Box(modifier = modifier) {
        Image(
            painter = rememberCoilPainter(
                request = curPlayingSong.imageUrl,
                fadeIn = true,
            ),
            null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.7f)
        )
        PlaybackBar(
            nextSong,
            previousSong,
        ) {
            playOrPause(curPlayingSong, it)
        }
    }
}

@Composable
fun PlaybackBar(
    nextSong: () -> Unit,
    previousSong: () -> Unit,
    playOrPause: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = {
                previousSong()
            },
            modifier = Modifier.weight(0.3f, true)
        ) {
            Icon(imageVector = Icons.Filled.SkipPrevious, contentDescription = null)
        }

        IconButton(
            onClick = {
                playOrPause(false)
            },
            modifier = Modifier.weight(0.3f, true)
        ) {
            Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
        }

        IconButton(
            onClick = {
                nextSong()
            },
            modifier = Modifier.weight(0.3f, true)
        ) {
            Icon(imageVector = Icons.Filled.SkipNext, contentDescription = null)
        }
    }
}

