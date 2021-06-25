package com.example.musiccompose.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.musiccompose.R

sealed class NavigationItem(
    val route: String,
    @StringRes val title: Int? = null,
    val icon: ImageVector? = null
) {
    object Home : NavigationItem("home", R.string.home, Icons.Filled.Home)

    object Library: NavigationItem("library", R.string.library, Icons.Filled.LibraryMusic)

    object SongList : NavigationItem("songList") {
        const val routeWithArgument: String = "songList/{mediaId}"
        const val mediaId: String = "mediaId"
    }

    object SongDetail : NavigationItem("songDetail") {
        const val routeWithArgument: String = "songDetail/{songId}"
        const val songId: String = "songId"
    }
}