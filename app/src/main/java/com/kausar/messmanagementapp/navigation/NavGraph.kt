package com.kausar.messmanagementapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kausar.messmanagementapp.presentation.AboutScreen
import com.kausar.messmanagementapp.presentation.home_screen.HomeScreen
import com.kausar.messmanagementapp.presentation.auth_screen.AuthScreen
import com.kausar.messmanagementapp.presentation.auth_screen.PinVerifyScreen

@Composable
fun SetupNavGraph(navController: NavHostController, startDestination: String) {

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(route = Screen.Login.route) {
            AuthScreen {
//                navController.popBackStack()
//                navController.navigate(Screen.Home.passName("kausar"))
                navController.navigate(Screen.PinVerify.route)

            }
        }
        composable(route = Screen.Home.route.plus(Screen.Home.path), arguments = listOf(
            navArgument(Screen.Home.argKey) {
                type = NavType.StringType
            }
        )) {
            val username = it.arguments?.getString(Screen.Home.argKey)
            HomeScreen(username)
        }

        composable(route = Screen.PinVerify.route) {
            PinVerifyScreen {

            }
        }

        composable(route = Screen.About.route) {
            AboutScreen()
        }
    }
}