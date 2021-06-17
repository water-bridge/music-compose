package com.example.musiccompose.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.musiccompose.ui.homescreen.HomeScreen
import com.example.musiccompose.ui.theme.MusicComposeTheme

@ExperimentalMaterialApi
@Composable
fun MusicComposeApp() {

    MusicComposeTheme {
        val viewModel = hiltViewModel<MainViewModel>()
        HomeScreen(viewModel = viewModel)
    }
}