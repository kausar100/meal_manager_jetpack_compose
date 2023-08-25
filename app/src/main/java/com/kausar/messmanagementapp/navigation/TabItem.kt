package com.kausar.messmanagementapp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.kausar.messmanagementapp.presentation.home_screen.SharedHomeScreen
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel

typealias ComposableFun = @Composable () -> Unit

sealed class TabItem(var title: String, var screen: ComposableFun) {
    data class Myself(
        val navController: NavController,
        val mainViewModel: MainViewModel
    ) :
        TabItem(
            "Myself",
            { SharedHomeScreen(mainViewModel) })

    data class Others(
        val navController: NavController,
        val mainViewModel: MainViewModel
    ) :
        TabItem("Others", { OthersScreen(mainViewModel) })
}

@Composable
fun OthersScreen(mainViewModel: MainViewModel) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Yellow),
        contentAlignment = Alignment.Center) {
        Text(text = "Others")

    }

}