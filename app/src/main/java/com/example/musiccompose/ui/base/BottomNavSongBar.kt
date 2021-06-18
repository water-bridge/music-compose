package com.example.musiccompose.ui.base

import androidx.compose.foundation.Image
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
import com.example.musiccompose.models.MediaItemData
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState

@ExperimentalPagerApi
@Composable
fun BottomNavSongBar(
    onClickIconAction: (MediaItemData, Boolean) -> Unit,
    onChangeIsPlaying: (Boolean) -> Unit,
    mediaItemDataList: List<MediaItemData>,
    isPlaying: Boolean,
    pagerState: PagerState
) {
    if (mediaItemDataList.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            HorizontalPager(
                state = pagerState,
            ) { page ->
                val song = mediaItemDataList[page]
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
                    if (isPlaying) {
                        IconButton(
                            onClick = {
                                onClickIconAction(song, true)
                                onChangeIsPlaying(false)
                            },
                            modifier = Modifier.weight(0.3f, true)
                        ) {
                            Icon(imageVector = Icons.Filled.Pause, contentDescription = null)
                        }
                    } else {
                        IconButton(
                            onClick = {
                                onClickIconAction(song, false)
                                onChangeIsPlaying(true)
                            },
                            modifier = Modifier.weight(0.3f, true)
                        ) {
                            Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
                        }
                    }
                }
            }
            // Todo a nav bar may be added bellow in the future
        }
    }
}