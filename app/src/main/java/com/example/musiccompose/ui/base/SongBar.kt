package com.example.musiccompose.ui.base

import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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

@Composable
fun SongBar(
    onClickIconAction: (MediaItemData, Boolean) -> Unit,
    nowPlayingSong: MediaItemData?,
    playbackState: PlaybackStateCompat,
    navigateToSongDetail: (String) -> Unit
) {
    nowPlayingSong?.let { song ->
        if (song.mediaId.isNotEmpty()) {
            Divider(thickness = 3.dp)
            SongBarRow(
                song = song,
                playbackState = playbackState,
                onClickIconAction = onClickIconAction,
                navigateToSongDetail = navigateToSongDetail
            )
        }
    }
}


@Composable
fun SongBarRow(
    song: MediaItemData,
    playbackState: PlaybackStateCompat,
    onClickIconAction: (MediaItemData, Boolean) -> Unit,
    navigateToSongDetail: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clickable {
                navigateToSongDetail(song.mediaId)
            },
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
                .weight(0.6f, true)
                .padding(horizontal = 8.dp)
        )

        if (playbackState.isPlaying) {
            IconButton(
                onClick = {
                    onClickIconAction(song, true)
                },
                modifier = Modifier.weight(0.2f, true)
            ) {
                Icon(imageVector = Icons.Filled.Pause, contentDescription = null)
            }
        } else {
            IconButton(
                onClick = {
                    onClickIconAction(song, false)
                },
                modifier = Modifier.weight(0.2f, true)
            ) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
            }
        }
    }
}