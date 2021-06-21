package com.example.musiccompose.ui.base

import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musiccompose.extensions.isPlaying
import com.example.musiccompose.models.MediaItemData
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState

@ExperimentalPagerApi
@Composable
fun BottomNavSongBar(
    shouldDisplayBar: Boolean,
    onClickIconAction: (MediaItemData, Boolean) -> Unit,
    songList: List<MediaItemData>,
    playbackState: PlaybackStateCompat,
    scrollState: ScrollState
) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
           Row(
               modifier = Modifier
                   .fillMaxWidth()
                   .horizontalScroll(scrollState)
           ) {
               songList.forEach { item ->
                   SongBarRow(
                       song = item,
                       playbackState = playbackState,
                       onClickIconAction = onClickIconAction
                   )
               }
           }


            // Todo a nav bar may be added bellow in the future
        }

}

@Composable
fun SongBarRow(
    song: MediaItemData,
    playbackState: PlaybackStateCompat,
    onClickIconAction: (MediaItemData, Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberCoilPainter(
                request = song.imageUrl,
            ),
            null,
            //modifier = Modifier.weight(0.2f, true)
        )
        Text(
            text = song.title,
            modifier = Modifier
                .weight(0.7f, true)
                .padding(horizontal = 8.dp)
        )
        if (playbackState.isPlaying) {
            IconButton(
                onClick = {
                    onClickIconAction(song, true)
                },
                modifier = Modifier.weight(0.3f, true)
            ) {
                Icon(imageVector = Icons.Filled.Pause, contentDescription = null)
            }
        } else {
            IconButton(
                onClick = {
                    onClickIconAction(song, false)
                },
                modifier = Modifier.weight(0.3f, true)
            ) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
            }
        }
    }
}