package com.example.musiccompose.ui

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.musiccompose.models.MediaItemData
import com.example.musiccompose.ui.base.BottomNavSongBar
import com.example.musiccompose.ui.homescreen.HomeScreen
import com.example.musiccompose.ui.listscreen.ListScreen
import com.example.musiccompose.ui.listscreen.ListViewModel
import com.example.musiccompose.ui.theme.MusicComposeTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import timber.log.Timber

@ExperimentalFoundationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun MusicComposeApp() {
    MusicComposeTheme {
        val navController = rememberNavController()
        val scaffoldState = rememberScaffoldState()

        // scoped to activity
        val mainViewModel = hiltViewModel<MainViewModel>()
        val isPlaying by mainViewModel.isPlaying.observeAsState(false)
        var barList by remember {
            mutableStateOf(emptyList<MediaItemData>())
        }
        val pagerState = rememberPagerState(pageCount = 0)

        Scaffold(
            scaffoldState = scaffoldState,
            bottomBar = {
                BottomNavSongBar(
                    mainViewModel::playOrPause,
                    mainViewModel::onChangeIsPlaying,
                    barList,
                    isPlaying,
                    pagerState
                )
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = MainDestinations.Home.route
            ) {
                composable(MainDestinations.Home.route) {
                    HomeScreen(
                        mainViewModel = mainViewModel,
                    ) {
                        navController.navigate("${MainDestinations.SongList.route}/$it")
                    }
                }
                composable(
                    MainDestinations.SongList.routeWithArgument,
                    arguments = listOf(
                        navArgument(MainDestinations.SongList.albumId) {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    // scoped to the songList screen
                    val listViewModel = hiltViewModel<ListViewModel>()
                    val albumId = backStackEntry.arguments?.getString(
                        MainDestinations.SongList.albumId
                    ) ?: return@composable
                    // must observe here, otherwise the mutablestate wont update, bug?
                    listViewModel.subscribe(albumId)
                    val songList by listViewModel.songList.observeAsState()
                    barList = songList ?: emptyList()
                    pagerState.pageCount = barList.size

                    ListScreen(
                        mainViewModel = mainViewModel,
                        listViewModel = listViewModel
                    )
                }
            }

        }
    }
}


sealed class MainDestinations(val route: String) {
    object Home : MainDestinations("home")
    object SongList : MainDestinations("songList") {
        const val routeWithArgument: String = "songList/{albumId}"
        const val albumId: String = "albumId"
    }
    object SongDetail : MainDestinations("songDetail") {
        const val routeWithArgument: String = "songDetail/{songId}"
        const val songId: String = "songId"
    }
}