package com.kausar.messmanagementapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.kausar.messmanagementapp.presentation.home_screen.HomeScreen
import com.kausar.messmanagementapp.presentation.home_screen.SharedHomeScreen
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel

typealias ComposableFun = @Composable () -> Unit

sealed class TabItem(var title: String, var screen: ComposableFun) {
    data class Myself(
        val navController: NavController,
        val mainViewModel: MainViewModel
    ) :
        TabItem(
            "Mine",
            { SharedHomeScreen(mainViewModel) })

    data class Total(
        val navController: NavController,
        val mainViewModel: MainViewModel
    ) :
        TabItem("Total", { HomeScreen(mainViewModel) })
}