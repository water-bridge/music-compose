package com.example.musiccompose.ui.base

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musiccompose.models.MediaItemData
import com.google.accompanist.coil.rememberCoilPainter

@ExperimentalMaterialApi
@Composable
fun SongCard(
    mediaItemData: MediaItemData,
    playOrPause: (MediaItemData, Boolean) -> Unit,
    isInLibrary: Boolean,
    insertSong: (MediaItemData) -> Unit,
    deleteSong: (MediaItemData) -> Unit
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
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    .weight(0.8f, true)
            )
            if (!isInLibrary) {
                IconButton(
                    onClick = {
                        insertSong(mediaItemData)
                    },
                    modifier = Modifier.weight(0.2f, true)
                ) {
                    Icon(imageVector = Icons.Filled.FavoriteBorder, contentDescription = null)
                }
            } else {
                IconButton(
                    onClick = {
                        deleteSong(mediaItemData)
                    },
                    modifier = Modifier.weight(0.2f, true)
                ) {
                    Icon(imageVector = Icons.Filled.Favorite, contentDescription = null)
                }
            }
        }
    }
}

