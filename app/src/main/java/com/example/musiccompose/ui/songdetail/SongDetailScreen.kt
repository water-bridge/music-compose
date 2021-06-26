package com.example.musiccompose.ui.songdetail

import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import com.example.musiccompose.ui.MainViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musiccompose.EMPTY_PLAYBACK_STATE
import com.example.musiccompose.extensions.isPlaying
import com.example.musiccompose.models.MediaItemData
import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun SongDetailScreen(
    mainViewModel: MainViewModel,
    songDetailViewModel: SongDetailViewModel
) {
    val curPlayingSong by mainViewModel.nowPlayingSong.observeAsState()
    val playbackState by mainViewModel.playbackState.observeAsState(EMPTY_PLAYBACK_STATE)
    val progress by songDetailViewModel.curProgress.observeAsState(0f)

    curPlayingSong?.let {
        Column {
            SongArtView(
                curPlayingSong = it,
                playbackState = playbackState,
                progress = progress,
                modifier = Modifier
                    .fillMaxSize(),
                nextSong = mainViewModel::nextSong,
                previousSong = mainViewModel::previousSong,
                playOrPause = mainViewModel::playOrPause,
                seekTo = mainViewModel::seekTo
            )
        }
    }


}

@Composable
fun SongArtView(
    curPlayingSong: MediaItemData,
    playbackState: PlaybackStateCompat,
    progress: Float,
    modifier: Modifier,
    nextSong: () -> Unit,
    previousSong: () -> Unit,
    playOrPause: (MediaItemData, Boolean) -> Unit,
    seekTo: (Float) -> Unit
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
        Column(
            modifier = Modifier
                .padding(top = 40.dp)
                .align(Alignment.TopCenter)
        ) {
            Text(
                text = curPlayingSong.title,
                fontSize = 30.sp
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(color = MaterialTheme.colors.background.copy(0.7f))
        ) {
            SeekBar(
                progress = progress,
                seekTo = seekTo
            )
            PlaybackBar(
                playbackState = playbackState,
                nextSong = nextSong,
                previousSong = previousSong,
            ) {
                playOrPause(curPlayingSong, it)
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun PlaybackBar(
    playbackState: PlaybackStateCompat,
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
            Icon(imageVector = Icons.Filled.SkipPrevious, contentDescription = null, modifier = Modifier.size(40.dp))
        }

        if (playbackState.isPlaying) {
            IconButton(
                onClick = {
                    playOrPause(true)
                },
                modifier = Modifier.weight(0.3f, true)
            ) {
                Icon(imageVector = Icons.Filled.Pause, contentDescription = null, modifier = Modifier.size(40.dp))
            }
        } else {
            IconButton(
                onClick = {
                    playOrPause(false)
                },
                modifier = Modifier.weight(0.3f, true)
            ) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(40.dp))
            }
        }

        IconButton(
            onClick = {
                nextSong()
            },
            modifier = Modifier.weight(0.3f, true)
        ) {
            Icon(imageVector = Icons.Filled.SkipNext, contentDescription = null, modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
fun SeekBar(
    progress: Float,
    seekTo: (Float) -> Unit
) {
    var sliderPosition  by remember { mutableStateOf(progress) }

    Slider(
        value = progress ,
        onValueChange = { newValue ->
            sliderPosition = newValue
        },
        onValueChangeFinished = {
            seekTo(sliderPosition)
        },
    )
    Spacer(Modifier.height(20.dp))
}
