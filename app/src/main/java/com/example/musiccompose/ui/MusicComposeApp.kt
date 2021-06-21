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
import com.example.musiccompose.models.MediaItemData
import com.example.musiccompose.ui.homescreen.HomeScreen
import com.example.musiccompose.ui.listscreen.ListScreen
import com.example.musiccompose.ui.listscreen.ListViewModel
import com.example.musiccompose.ui.songdetail.SongDetailScreen
import com.example.musiccompose.ui.songdetail.SongDetailViewModel
import com.example.musiccompose.ui.theme.MusicComposeTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState

@ExperimentalFoundationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun MusicComposeApp() {
    MusicComposeTheme {
        val navController = rememberNavController()

        // scoped to activity
        val mainViewModel = hiltViewModel<MainViewModel>()
        var barList by remember {
            mutableStateOf(emptyList<MediaItemData>())
        }
        val pagerState = rememberPagerState(pageCount = 0)

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
                    navArgument(MainDestinations.SongList.mediaId) {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                // scoped to the songList screen
                val listViewModel = hiltViewModel<ListViewModel>()
                val mediaId = backStackEntry.arguments?.getString(
                    MainDestinations.SongList.mediaId
                ) ?: return@composable
                // must observe here, otherwise the mutablestate wont update, bug?
                listViewModel.subscribe(mediaId)
                //val songList by listViewModel.songList.observeAsState()
                //barList = songList ?: emptyList()
                //pagerState.pageCount = barList.size

                ListScreen(
                    mainViewModel = mainViewModel,
                    listViewModel = listViewModel,
                    pagerState = pagerState
                ) {
                    navController.navigate("${MainDestinations.SongDetail.route}/$it")
                }
            }

            composable(
                MainDestinations.SongDetail.routeWithArgument,
                arguments = listOf(
                    navArgument(MainDestinations.SongDetail.songId) {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val songDetailViewModel = hiltViewModel<SongDetailViewModel>()
                val songId = backStackEntry.arguments?.getString(
                    MainDestinations.SongDetail.songId
                )
                SongDetailScreen(
                    mainViewModel = mainViewModel,
                    songDetailViewModel = songDetailViewModel
                )
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