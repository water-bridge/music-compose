package com.example.musiccompose.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.musiccompose.EMPTY_PLAYBACK_STATE
import com.example.musiccompose.ui.base.BottomNavSongBar
import com.example.musiccompose.ui.homescreen.HomeScreen
import com.example.musiccompose.ui.listscreen.ListScreen
import com.example.musiccompose.ui.listscreen.ListViewModel
import com.example.musiccompose.ui.songdetail.SongDetailScreen
import com.example.musiccompose.ui.songdetail.SongDetailViewModel
import com.example.musiccompose.ui.theme.MusicComposeTheme
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun MusicComposeApp() {
    MusicComposeTheme {
        // similar to "scoped to activity"
        val mainViewModel = hiltViewModel<MainViewModel>()

        val playbackState by mainViewModel.playbackState.observeAsState(EMPTY_PLAYBACK_STATE)
        val nowPlayingSong by mainViewModel.nowPlayingSong.observeAsState()

        val navController = rememberNavController()
        val coroutineScope = rememberCoroutineScope()
        val scaffoldState = rememberScaffoldState()

        var barVisible by remember { mutableStateOf(true) }

        Scaffold(
            scaffoldState = scaffoldState,
            bottomBar = {
                if (barVisible) {
                    BottomNavSongBar(
                        onClickIconAction = mainViewModel::playOrPause,
                        nowPlayingSong = nowPlayingSong,
                        playbackState = playbackState,
                    ) {
                        navController.navigate("${MainDestinations.SongDetail.route}/$it")
                    }
                }
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = MainDestinations.Home.route
            ) {
                composable(MainDestinations.Home.route) {
                    barVisible = true
                    HomeScreen(
                        mainViewModel = mainViewModel,
                    ) {
                        navController.navigate("${MainDestinations.SongList.route}/$it")
                    }
                }
                composable(
                    MainDestinations.SongList.routeWithArgument,
                    arguments = listOf(
                        navArgument(MainDestinations.SongList.mediaId) {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    val mediaId = backStackEntry.arguments?.getString(
                        MainDestinations.SongList.mediaId
                    ) ?: return@composable
                    // scoped to the songList screen
                    val listViewModel = hiltViewModel<ListViewModel>()
                    // Todo not sure
                    coroutineScope.launch {
                        listViewModel.subscribe(mediaId)
                    }
                    barVisible = true
                    ListScreen(
                        mainViewModel = mainViewModel,
                        listViewModel = listViewModel
                    )
                }

                composable(
                    MainDestinations.SongDetail.routeWithArgument,
                    arguments = listOf(
                        navArgument(MainDestinations.SongDetail.songId) {
                            type = NavType.StringType
                        }
                    )
                ) {
                    val songDetailViewModel = hiltViewModel<SongDetailViewModel>()
                    barVisible = false
                    SongDetailScreen(
                        mainViewModel = mainViewModel,
                        songDetailViewModel = songDetailViewModel
                    )
                }
            }
        }
    }
}


sealed class MainDestinations(val route: String) {
    object Home : MainDestinations("home")
    object SongList : MainDestinations("songList") {
        const val routeWithArgument: String = "songList/{mediaId}"
        const val mediaId: String = "mediaId"
    }

    object SongDetail : MainDestinations("songDetail") {
        const val routeWithArgument: String = "songDetail/{songId}"
        const val songId: String = "songId"
    }
}