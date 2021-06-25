package com.example.musiccompose.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
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
import com.example.musiccompose.ui.base.NavBar
import com.example.musiccompose.ui.base.SongBar
import com.example.musiccompose.ui.home.HomeScreen
import com.example.musiccompose.ui.library.LibraryScreen
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

        var songBarVisible by remember { mutableStateOf(true) }

        Scaffold(
            scaffoldState = scaffoldState,
            bottomBar = {
                Column {
                    if (songBarVisible) {
                        SongBar(
                            onClickIconAction = mainViewModel::playOrPause,
                            nowPlayingSong = nowPlayingSong,
                            playbackState = playbackState,
                        ) {
                            navController.navigate("${NavigationItem.SongDetail.route}/$it")
                        }
                        NavBar(
                            navController = navController,
                            items = listOf(
                                NavigationItem.Home,
                                NavigationItem.Library
                            )
                        )
                    }

                }
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = NavigationItem.Home.route
            ) {
                composable(NavigationItem.Home.route) {
                    songBarVisible = true
                    HomeScreen(
                        mainViewModel = mainViewModel,
                    ) {
                        navController.navigate("${NavigationItem.SongList.route}/$it")
                    }
                }

                composable(NavigationItem.Library.route) {
                    songBarVisible = true
                    LibraryScreen()
                }

                composable(
                    NavigationItem.SongList.routeWithArgument,
                    arguments = listOf(
                        navArgument(NavigationItem.SongList.mediaId) {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    val mediaId = backStackEntry.arguments?.getString(
                        NavigationItem.SongList.mediaId
                    ) ?: return@composable
                    // scoped to the songList screen
                    val listViewModel = hiltViewModel<ListViewModel>()
                    // Todo not sure
                    coroutineScope.launch {
                        listViewModel.subscribe(mediaId)
                    }
                    songBarVisible = true
                    ListScreen(
                        mainViewModel = mainViewModel,
                        listViewModel = listViewModel
                    )
                }

                composable(
                    NavigationItem.SongDetail.routeWithArgument,
                    arguments = listOf(
                        navArgument(NavigationItem.SongDetail.songId) {
                            type = NavType.StringType
                        }
                    )
                ) {
                    val songDetailViewModel = hiltViewModel<SongDetailViewModel>()
                    songBarVisible = false
                    SongDetailScreen(
                        mainViewModel = mainViewModel,
                        songDetailViewModel = songDetailViewModel
                    )
                }
            }
        }
    }
}


